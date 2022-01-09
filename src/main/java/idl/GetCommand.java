package idl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GetCommand implements CommandExecutor {
    private FileConfiguration config;

    public GetCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {

            ArrayList<ItemStack> items = new ArrayList<>(); //TODO: get from database

            items.add(new ItemStack(Material.DIAMOND, 64));
            items.add(new ItemStack(Material.BRICK, 64));

            if (this.getFreeSlots(player) >= items.size()) {
                for (ItemStack item: items) {
                    player.getInventory().addItem(item);
                }
                player.sendMessage("Done! Check Your inventory!"); //Delete from database
            } else {
                if (this.config.getBoolean("general.dropIfInventoryIsFull")) {
                    Location loc = player.getLocation();
                    for (ItemStack item: items) {
                        player.getWorld().dropItem(loc, item);
                    }
                    player.sendMessage("Done! Items on ground near you!");
                } else {
                    player.sendMessage("Please clear your inventory first.");
                }

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
