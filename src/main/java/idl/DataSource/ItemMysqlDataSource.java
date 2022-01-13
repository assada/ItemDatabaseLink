package idl.DataSource;

import idl.Data.Item;
import idl.MysqlDataSource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemMysqlDataSource implements ItemDataSource {

    private final MysqlDataSource dataSource;
    private String database;

    public ItemMysqlDataSource(MysqlDataSource dataSource, String database) {
        this.dataSource = dataSource;
        this.database = database;
    }

    public List<Item> getItemForUUID(String uuid, int status) {
        String SQL_QUERY = "select * from idl_items WHERE uuid = '%s' AND status = %d".formatted(uuid, status);
        List<Item> items = null;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(SQL_QUERY);
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
                        rs.getInt("status"),
                        rs.getDate("created")
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
        //migration
        try {
            PreparedStatement stmt = this.dataSource.getConnection().prepareStatement("SELECT count(*) as co FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'idl_items' AND table_schema = '" + this.database + "' AND column_name = 'created'");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt("co") == 0) {
                stmt = this.dataSource.getConnection().prepareStatement("ALTER TABLE idl_items ADD created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL");
                stmt.executeUpdate();
                Bukkit.getLogger().info(ChatColor.YELLOW + "[ItemDatabaseLink] Migration 'created' applied!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean updateStatus(ArrayList<Integer> gotIds, int newStatus) { //TODO: check statuses
        String ids = "";
        boolean status = false;
        for (int gotId : gotIds) {
            ids = ids.concat(Integer.toString(gotId)).concat(",");
        }
        ids = removeLastCharOptional(ids);

        try {
            PreparedStatement stmt = this.dataSource.getConnection().prepareStatement("UPDATE idl_items SET status = %d WHERE id IN (%s);".formatted(newStatus, ids));
            stmt.executeUpdate();
            status = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public static String removeLastCharOptional(String s) {
        return Optional.ofNullable(s)
                .filter(str -> str.length() != 0)
                .map(str -> str.substring(0, str.length() - 1))
                .orElse(s);
    }
}
