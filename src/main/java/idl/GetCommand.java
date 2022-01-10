package idl;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetCommand implements CommandExecutor {
    private final FileConfiguration config;
    private final ItemChecker checker;

    public GetCommand(FileConfiguration config, ItemChecker checker) {
        this.config = config;
        this.checker = checker;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            HashMap<Integer, ItemStack> items = new HashMap<>();
            List<Item> itemsRecords = this.checker.get(player);
            ArrayList<Integer> gotIds = new ArrayList<>();

            for (Item itemRecord : itemsRecords) {
                if (itemRecord.getType().equals("Item")) { //TODO: Enum types?
                    Material mat = Material.matchMaterial(itemRecord.getValue().toUpperCase());
                    if (null != mat) {
                        items.put(itemRecord.getId(), new ItemStack(mat, itemRecord.getQty()));
                    } else {
                        Bukkit.getLogger().warning("[ItemDatabaseLink] Item %s not found!".formatted(itemRecord.getValue()));
                    }
                }
                if (itemRecord.getType().equals("Experience")) {
                    player.giveExp(itemRecord.getQty());
                    gotIds.add(itemRecord.getId());
                    player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Done! Experience increased!");
                }
                if (itemRecord.getType().equals("Heal")) {
                    player.setHealth(20.0);
                    player.setFoodLevel(20);
                    player.setFireTicks(0);
                    gotIds.add(itemRecord.getId());
                    player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Done! You are completely healed and completely full!");
                }
                if (itemRecord.getType().equals("PotionEffect")) {
                    PotionEffectType effectType = PotionEffectType.getByName(itemRecord.getValue().toUpperCase());
                    if (effectType != null) {
                        player.addPotionEffect(new PotionEffect(effectType, itemRecord.getQty(), 1, true, true, true));
                        gotIds.add(itemRecord.getId());
                        player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Done! Are you already feeling the effect?");
                    } else {
                        Bukkit.getLogger().warning("[ItemDatabaseLink] PotionEffect %s not found!".formatted(itemRecord.getValue()));
                    }
                }
            }
            if (items.size() > 0) {
                if (this.getFreeSlots(player) >= items.size()) {
                    for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                        int key = entry.getKey();
                        ItemStack value = entry.getValue();
                        player.getInventory().addItem(value);
                        gotIds.add(key);
                    }
                    player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Done! Check your inventory!");
                } else {
                    if (this.config.getBoolean("general.dropIfInventoryIsFull")) {
                        Location loc = player.getLocation();
                        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                            int key = entry.getKey();
                            ItemStack value = entry.getValue();
                            player.getWorld().dropItem(loc, value);
                            gotIds.add(key);
                        }
                        player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.GREEN + " Done! Items on ground near you!");
                    } else {
                        player.sendMessage(ChatColor.DARK_GREEN + "[" + config.getString("general.chatPrefix") + ChatColor.DARK_GREEN + "]" + ChatColor.RED + " Error! Please clear your inventory first.");
                    }
                }
            }
            if(gotIds.size() > 0) {
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
