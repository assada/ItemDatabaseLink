package idl;

import idl.Data.IDLItemStack;
import idl.Data.Item;
import idl.DataSource.ItemDataSource;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DatabaseItemChecker implements ItemChecker {

    private final ItemDataSource dataSource;

    public DatabaseItemChecker(ItemDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int check(Player player) {
        if (!player.hasPermission("idl.command.get")) {
            return 0;
        }
        List<Item> items = this.dataSource.getItemForUUID(player.getUniqueId().toString(), 0); //TODO: Enum status?

        return items.size();
    }

    public List<IDLItemStack> get(Player player) {
        List<IDLItemStack> idlItemStacks = new ArrayList<>();

        List<Item> items = this.dataSource.getItemForUUID(player.getUniqueId().toString(), 0);  //TODO: Enum status?
        for (Item item : items) {
            idlItemStacks.add(new IDLItemStack(item));
        }
        return idlItemStacks;
    }

    @Override
    public boolean updateStatus(ArrayList<Integer> gotIds, int newStatus) {
        if (gotIds.size() > 0) {
            return this.dataSource.updateStatus(gotIds, newStatus);
        }

        return false;
    }
}
