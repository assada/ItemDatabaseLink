package idl;

import idl.DataSource.ItemDataSource;
import idl.DataSource.ItemMysqlDataSource;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    FileConfiguration config = getConfig();
    MysqlDataSource dataSource = new MysqlDataSource(config);
    private static Economy econ;
    private static LuckPerms LuckPermsApi;

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public boolean setupPermissions() { //TODO: Factory
        if(!config.getBoolean("integration.LuckPerms", false)) {
            return false;
        }
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPermsApi = provider.getProvider();
            return true;
        }

        return false;
    }
    public static LuckPerms getLuckPermsApi() {
        return LuckPermsApi;
    }
    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Enabled " + this.getName());
        if (!setupEconomy() ) {
            Bukkit.getLogger().severe(String.format("[%s] - No Vault soft-dependency found!", getDescription().getName()));
            /*getServer().getPluginManager().disablePlugin(this);
            return;*/
        }

        if(!setupPermissions()) {
            Bukkit.getLogger().severe(String.format("[%s] - No LuckPerms soft-dependency found!", getDescription().getName()));
        }

        if (config.getInt("general.configVersion", 1) < 5) { //config migration
            if (config.getInt("general.configVersion", 1) < 4) {
                config.set("general.configVersion", 4);
                config.set("general.checkTicks", 3600);
                config.set("general.chatPrefix", "IDL");
                Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Config migrated to version 4");
            }
            if (config.getInt("general.configVersion", 1) < 5) {
                config.set("general.configVersion", 5);
                config.set("mysql.autoMigration", true);
                Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Config migrated to version 5");
            }
            if (config.getInt("general.configVersion", 1) < 6) {
                config.set("general.configVersion", 6);
                config.set("general.addDescriptionToItems", true);
                Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Config migrated to version 6");
            } //TODO: migrations to 7 version

            if(config.getInt("general.configVersion", 1) < 8) {
                config.set("general.configVersion", 8);
                config.set("messages.no_rewards", "&2No any rewards available =(");
                config.set("messages.permission_claimed", "&2&lDone! &r&bNew permission in your pocket!");
                Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Config migrated to version 8");
            }
            this.saveConfig();
        } else {
            Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Creating default config");
            this.saveDefaultConfig();
        }

        ItemDataSource itemDataSource = new ItemMysqlDataSource(dataSource, config.getString("mysql.database", "minecraft"));
        ItemChecker itemChecker = new DatabaseItemChecker(itemDataSource);
        Listener listener;

        ChatFormatter chatFormatter = new SimpleChatFormatter(config);

        if (config.getBoolean("mysql.autoMigration", true)) {
            itemDataSource.migrate();
        }

        if (config.getBoolean("integration.AuthMe") && getServer().getPluginManager().getPlugin("AuthMe") != null) {
            Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] AuthMe detected! Using Login event instead Join...");
            listener = new LoginListener(itemChecker, config, chatFormatter);
        } else {
            listener = new JoinListener(itemChecker, config, chatFormatter);
        }

        getServer().getPluginManager().registerEvents(listener, this);
        this.getCommand("claim").setExecutor(new ClaimCommand(config, itemChecker, getEconomy(), chatFormatter, getLuckPermsApi()));
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new ItemCheckTask(itemChecker, config, chatFormatter), 0L, config.getLong("general.checkTicks", 3600L));
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "] Disabled " + this.getName());
        dataSource.close();
    }
}
