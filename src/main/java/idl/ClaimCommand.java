package idl;

import idl.Data.IDLItemStack;
import idl.Data.Item;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ClaimCommand implements CommandExecutor {
    private final FileConfiguration config;
    private final ItemChecker checker;
    private final Economy econ;
    private final ChatFormatter chatFormatter;
    private final LuckPerms luckPermsApi;

    public ClaimCommand(FileConfiguration config, ItemChecker checker, Economy economy, ChatFormatter chatFormatter, LuckPerms luckPermsApi) {
        this.config = config;
        this.checker = checker;
        this.econ = economy;
        this.chatFormatter = chatFormatter;
        this.luckPermsApi = luckPermsApi;
    }

    private ArrayList<PotionEffectType> getBedEffects() {
        ArrayList<PotionEffectType> badEffects = new ArrayList<>();
        badEffects.add(PotionEffectType.getByName("POISON"));
        badEffects.add(PotionEffectType.getByName("WEAKNESS"));
        badEffects.add(PotionEffectType.getByName("BAD_OMEN"));
        badEffects.add(PotionEffectType.getByName("BLINDNESS"));
        badEffects.add(PotionEffectType.getByName("CONFUSION"));
        badEffects.add(PotionEffectType.getByName("HARM"));
        badEffects.add(PotionEffectType.getByName("HUNGER"));
        badEffects.add(PotionEffectType.getByName("SLOW"));
        badEffects.add(PotionEffectType.getByName("WITHER"));

        return badEffects;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            List<IDLItemStack> items = new ArrayList<>();
            List<IDLItemStack> itemsRecords = this.checker.get(player);
            ArrayList<Integer> gotIds = new ArrayList<>();

            if (itemsRecords.size() == 0) {
                player.sendMessage(this.chatFormatter.format("messages.no_rewards"));
            }

            for (IDLItemStack itemRecord : itemsRecords) {
                Item idlItem = itemRecord.getItem();
                //TODO: strategy
                if (idlItem.getType().equals(Item.ITEM)) {
                    Material mat = Material.matchMaterial(idlItem.getValue().toUpperCase());
                    if (null != mat) {
                        itemRecord.setItemStack(new ItemStack(mat, idlItem.getQty()));
                        items.add(itemRecord);
                    } else {
                        Bukkit.getLogger().warning("[ItemDatabaseLink] Item %s not found!".formatted(idlItem.getValue()));
                    }
                }

                if (idlItem.getType().equals(Item.EXPERIENCE)) {
                    player.giveExp(idlItem.getQty());
                    gotIds.add(idlItem.getId());
                    player.sendMessage(this.chatFormatter.format("messages.experience_claimed"));
                }

                if (idlItem.getType().equals(Item.HEAL)) {
                    player.setHealth(20.0);
                    player.setFoodLevel(20);
                    player.setFireTicks(0);
                    boolean effects;
                    try {
                        effects = true;
                        this.getBedEffects().forEach((effect) -> {
                            if (effect == null) {
                                Bukkit.getLogger().warning("[ItemDatabaseLink] null effect");
                            } else {
                                Bukkit.getLogger().info("[ItemDatabaseLink] Ok effect " + effect.getName());
                                player.removePotionEffect(effect);
                            }

                        });
                    } catch (Exception exception) {
                        effects = false;
                        Bukkit.getLogger().warning("[ItemDatabaseLink] " + exception.getMessage());
                    }

                    if (effects) {
                        gotIds.add(idlItem.getId());
                        player.sendMessage(this.chatFormatter.format("messages.heal_claimed"));
                    }
                }

                if (idlItem.getType().equals(Item.MONEY) && null != econ) {
                    int qty = idlItem.getQty();
                    EconomyResponse r = econ.depositPlayer(player, qty);
                    if (r.transactionSuccess()) {
                        gotIds.add(idlItem.getId());

                        player.sendMessage(this.chatFormatter.format("messages.money_claimed").replace("%amount%", econ.format(r.amount)).replace("%balance%", econ.format(r.balance)));
                    } else {
                        Bukkit.getLogger().warning("[ItemDatabaseLink] Vault disabled!");
                    }
                }

                if (idlItem.getType().equals(Item.EFFECT)) {
                    PotionEffectType effectType = PotionEffectType.getByName(idlItem.getValue().toUpperCase());
                    if (effectType != null) {
                        player.addPotionEffect(new PotionEffect(effectType, idlItem.getQty(), 1, true, true, true));
                        gotIds.add(idlItem.getId());
                        player.sendMessage(this.chatFormatter.format("messages.effect_claimed"));
                    } else {
                        Bukkit.getLogger().warning("[ItemDatabaseLink] PotionEffect %s not found!".formatted(idlItem.getValue()));
                    }
                }

                if (idlItem.getType().equals(Item.PERMISSION) && null != luckPermsApi) {
                    if (this.addPermission(player, idlItem.getValue(), idlItem.getQty())) {
                        gotIds.add(idlItem.getId());
                    }
                }
            }

            if (items.size() > 0) {
                boolean freeSlots = this.getFreeSlots(player) >= items.size();
                boolean dropIfInventoryIsFull = this.config.getBoolean("general.dropIfInventoryIsFull");
                String message = "messages.item_claimed";
                boolean transferred = false;
                for (IDLItemStack item : items) {
                    ItemStack value = item.getItemStack();
                    ItemMeta im = value.getItemMeta();
                    assert im != null;
                    if (this.config.getBoolean("general.addDescriptionToItems")) {
                        im.setLore(List.of(this.config.getString("general.description")));
                        value.setItemMeta(im);
                    }
                    if (freeSlots) {
                        transferred = player.getInventory().addItem(value).isEmpty();
                        message = "messages.item_claimed";
                    } else if (dropIfInventoryIsFull) {
                        Location loc = player.getLocation();
                        player.getWorld().dropItem(loc, value);
                        transferred = true;
                        message = "messages.item_claimed";
                    }
                    if (transferred) {
                        gotIds.add(item.getItem().getId());
                    }
                }
                player.sendMessage(this.chatFormatter.format(message));
            }
            if (gotIds.size() > 0) {
                this.checker.updateStatus(gotIds, ItemChecker.DONE); //TODO: Enum statuses?
            }
        }

        return true;
    }

    private boolean addPermission(Player player, String permission, int duration) {
        User user = luckPermsApi.getPlayerAdapter(Player.class).getUser(player);
        Node node = Node.builder(permission)
                .value(true)
                .expiry(Duration.ofSeconds(duration))
                .build();

        user.data().add(node);
        luckPermsApi.getUserManager().saveUser(user);

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
