package idl.DataSource;

import idl.Data.Item;
import idl.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemMysqlDataSource implements ItemDataSource {

    private final MysqlDataSource dataSource;

    public ItemMysqlDataSource(MysqlDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Item> getItemForUUID(String uuid, int status) {
        String SQL_QUERY = "select * from idl_items WHERE uuid = '%s' AND status = %d".formatted(uuid, status);
        List<Item> items = null;

        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement( SQL_QUERY );
                ResultSet rs = pst.executeQuery()
        ) {
            items = new ArrayList<>();
            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("uuid"),
                        rs.getString("type"),
                        rs.getString("value"),
                        rs.getInt("qty"),
                        rs.getInt("status")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void migrate() {
        try {
            PreparedStatement stmt = this.dataSource.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `idl_items` (`id` INT(11) unsigned NOT NULL AUTO_INCREMENT,`uuid` VARCHAR(36) NOT NULL,`type` VARCHAR(255) NOT NULL,`value` VARCHAR(255) NOT NULL,`qty` INT(11) NOT NULL,`status` INT(11) NOT NULL DEFAULT 0,PRIMARY KEY (`id`)) ENGINE=InnoDB;");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatus(ArrayList<Integer> gotIds, int newStatus) { //TODO: check statuses
        String ids = "";
        for (int gotId : gotIds) {
            ids = ids.concat(Integer.toString(gotId)).concat(",");
        }
        ids = removeLastCharOptional(ids);

        try {
            PreparedStatement stmt = this.dataSource.getConnection().prepareStatement("UPDATE idl_items SET status = %d WHERE id IN (%s);".formatted(newStatus, ids));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String removeLastCharOptional(String s) {
        return Optional.ofNullable(s)
                .filter(str -> str.length() != 0)
                .map(str -> str.substring(0, str.length() - 1))
                .orElse(s);
    }
}
