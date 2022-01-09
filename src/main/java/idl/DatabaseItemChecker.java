package idl;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseItemChecker implements ItemChecker{
    private HikariDataSource dataSource;

    public DatabaseItemChecker(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void check() {

    }

    public void migrate() {
        try {
            PreparedStatement stmt = this.dataSource.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `idl_items` (`id` INT(11) unsigned NOT NULL AUTO_INCREMENT,`uuid` VARCHAR(36) NOT NULL,`type` VARCHAR(255) NOT NULL,`value` INT(11) unsigned NOT NULL,`qty` INT(11) NOT NULL,PRIMARY KEY (`id`)) ENGINE=InnoDB;");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
