package idl;

import idl.Data.IDLItemStack;
import idl.Data.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
            ArrayList<IDLItemStack> rewardList = new ArrayList<>();
            List<Item> itemsRecords = this.itemChecker.get(player);
            for (Item itemRecord : itemsRecords) {
                if (itemRecord.getType().equals("Item")) { //TODO: Enum types?
                    Material mat = Material.matchMaterial(itemRecord.getValue().toUpperCase());
                    if (null != mat) {
                        int qty = itemRecord.getQty();
                        int max = mat.getMaxStackSize();
                        if (qty > mat.getMaxStackSize()) {
                            while (qty >= max) {
                                rewardList.add(new IDLItemStack(itemRecord.getId(), new ItemStack(mat, max)));
                                qty = qty - max;
                            };
                        }
                        if (qty > 0) {
                            rewardList.add(new IDLItemStack(itemRecord.getId(), new ItemStack(mat, qty)));
                        }
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
