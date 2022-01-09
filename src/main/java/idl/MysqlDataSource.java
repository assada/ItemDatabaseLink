package idl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class MysqlDataSource {
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public MysqlDataSource(FileConfiguration configuration) {
        config.setJdbcUrl("jdbc:mysql://%s:%d/%s".formatted(configuration.getString("mysql.host", "localhost"), configuration.getInt("mysql.port", 3306), configuration.getString("mysql.database", "minecraft")));
        config.setUsername(configuration.getString("mysql.username", "root"));
        config.setPassword(configuration.getString("mysql.password", ""));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void close() {
        try {
            if (ds.getConnection() != null && !ds.getConnection().isClosed()) {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
