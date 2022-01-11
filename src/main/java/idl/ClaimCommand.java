package idl;

import idl.Data.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClaimCommand implements CommandExecutor {
    private Main plugin;
    private FileConfiguration config;
    private ItemChecker itemChecker;

    public ClaimCommand(Main plugin, FileConfiguration config, ItemChecker itemChecker) {
        this.plugin = plugin;
        this.config = config;
        this.itemChecker = itemChecker;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            ArrayList<ItemStack> rewardList = new ArrayList<>();
            List<Item> itemsRecords = this.itemChecker.get(player);
            for (Item itemRecord : itemsRecords) {
                if (itemRecord.getType().equals("Item")) { //TODO: Enum types?
                    Material mat = Material.matchMaterial(itemRecord.getValue().toUpperCase());
                    if (null != mat) {
                        rewardList.add(new ItemStack(mat, itemRecord.getQty()));
                    } else {
                        Bukkit.getLogger().warning("[ItemDatabaseLink] Item %s not found!".formatted(itemRecord.getValue()));
                    }
                }
            }

            ClaimItemsMenu claimItemsMenu = new ClaimItemsMenu(plugin, 45, rewardList);
            player.openInventory(claimItemsMenu.getInventory());
        }
        return true;
    }
}
