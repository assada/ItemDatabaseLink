package idl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    FileConfiguration config = getConfig();
    HikariDataSource ds;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());
        this.saveDefaultConfig();

        HikariConfig dconfig = new HikariConfig();
        dconfig.setJdbcUrl("jdbc:mysql://%s:%d/%s".formatted(config.getString("mysql.host", "localhost"), config.getInt("mysql.port", 3306), config.getString("mysql.database", "minecraft")));
        dconfig.setUsername(config.getString("mysql.username", "root"));
        dconfig.setPassword(config.getString("mysql.username", ""));
        dconfig.setConnectionTestQuery("SELECT 1");

        this.ds = new HikariDataSource(dconfig);

        ItemChecker itemChecker = new DatabaseItemChecker(this.ds);
        Listener listener;

        itemChecker.migrate();

        if (config.getBoolean("integration.AuthMe") && getServer().getPluginManager().getPlugin("AuthMe") != null) {
            Bukkit.getLogger().info(ChatColor.GREEN + "AuthMe detected! Using Login event instead Join...");
            listener = new LoginListener(itemChecker);
        } else {
            listener = new JoinListener(itemChecker);
        }
        getServer().getPluginManager().registerEvents(listener, this);
        this.getCommand("get").setExecutor(new GetCommand(config));
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "Disabled " + this.getName());
        if (this.ds != null && !this.ds.isClosed()) {
            this.ds.close();
        }
    }
}
