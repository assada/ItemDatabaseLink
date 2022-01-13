package idl;

import idl.Data.IDLItemStack;
import idl.Data.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ClaimCommand implements CommandExecutor {
    private final FileConfiguration config;
    private final ItemChecker checker;

    public ClaimCommand(FileConfiguration config, ItemChecker checker) {
        this.config = config;
        this.checker = checker;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            List<IDLItemStack> items = new ArrayList<>();
            List<IDLItemStack> itemsRecords = this.checker.get(player);
            ArrayList<Integer> gotIds = new ArrayList<>();
            for (IDLItemStack itemRecord : itemsRecords) {
                Item idlItem = itemRecord.getItem();
                if (idlItem.getType().equals("Item")) { //TODO: Enum types?
                    Material mat = Material.matchMaterial(idlItem.getValue().toUpperCase());
                    if (null != mat) {
                        itemRecord.setItemStack(new ItemStack(mat, idlItem.getQty()));
                        items.add(itemRecord);
                    } else {
                        Bukkit.getLogger().warning("[ItemDatabaseLink] Item %s not found!".formatted(idlItem.getValue()));
                    }
                }
                if (idlItem.getType().equals("Experience")) {
                    player.giveExp(idlItem.getQty());
                    gotIds.add(idlItem.getId());
                    player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Done! Experience increased!");
                }
                if (idlItem.getType().equals("Heal")) {
                    player.setHealth(20.0);
                    player.setFoodLevel(20);
                    player.setFireTicks(0);
                    gotIds.add(idlItem.getId());
                    player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Done! You are completely healed and completely full!");
                }
                if (idlItem.getType().equals("PotionEffect")) {
                    PotionEffectType effectType = PotionEffectType.getByName(idlItem.getValue().toUpperCase());
                    if (effectType != null) {
                        player.addPotionEffect(new PotionEffect(effectType, idlItem.getQty(), 1, true, true, true));
                        gotIds.add(idlItem.getId());
                        player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Done! Are you already feeling the effect?");
                    } else {
                        Bukkit.getLogger().warning("[ItemDatabaseLink] PotionEffect %s not found!".formatted(idlItem.getValue()));
                    }
                }
            }

            if (items.size() > 0) {
                boolean freeSlots = this.getFreeSlots(player) >= items.size();
                boolean dropIfInventoryIsFull = this.config.getBoolean("general.dropIfInventoryIsFull");
                String message = ChatColor.RED + "Oops! Please clear your inventory first.";
                boolean transferred = false;
                for (IDLItemStack item : items) {
                    ItemStack value = item.getItemStack();
                    ItemMeta im = value.getItemMeta();
                    assert im != null;
                    if(this.config.getBoolean("general.addDescriptionToItems")) {
                        im.setLore(List.of("From the Void")); //TODO: from config
                        value.setItemMeta(im);
                    }
                    if (freeSlots) {
                        transferred = player.getInventory().addItem(value).isEmpty();
                        message = ChatColor.GREEN + "Done! Check your inventory!";
                    } else if (dropIfInventoryIsFull) {
                        Location loc = player.getLocation();
                        player.getWorld().dropItem(loc, value);
                        transferred = true;
                        message = ChatColor.GREEN + "Done! Items on ground near you!";
                    }
                    if (transferred) {
                        gotIds.add(item.getItem().getId());
                    }
                }
                player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "] " + message);
            }
            if (gotIds.size() > 0) {
                this.checker.updateStatus(gotIds, 1); //TODO: Enum statuses?
            }
        }

        return true;
    }

    private int getFreeSlots(Player player) {
        int freeslots = 0;
        for (ItemStack it : player.getInventory().getContents()) {
            if (it == null || it.getType() == Material.AIR) {
                freeslots++;
            }
        }
        freeslots = freeslots - 5; // subtract shield and armor

        return freeslots;
    }
}
