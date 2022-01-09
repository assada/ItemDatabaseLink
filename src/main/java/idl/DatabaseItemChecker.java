package idl;

import idl.Data.Item;
import idl.DataSource.ItemDataSource;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DatabaseItemChecker implements ItemChecker{

    private final ItemDataSource dataSource;

    public DatabaseItemChecker(ItemDataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public int check(Player player) {
        if(!player.hasPermission("idl.command.get")) {
            return 0;
        }
        List<Item> items = this.dataSource.getItemForUUID(player.getUniqueId().toString(), 0); //TODO: Enum status?

        return items.size();
    }

    public List<Item> get(Player player) {
        return this.dataSource.getItemForUUID(player.getUniqueId().toString(), 0); //TODO: Enum status?
    }

    @Override
    public void updateStatus(ArrayList<Integer> gotIds, int newStatus) {
        if(gotIds.size() > 0) {
            this.dataSource.updateStatus(gotIds, newStatus);
        }
    }
}
