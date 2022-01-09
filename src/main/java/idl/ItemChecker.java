package idl;

import idl.Data.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public interface ItemChecker {
    public int check(Player player);

    public List<Item> get(Player player);

    void updateStatus(ArrayList<Integer> gotIds, int newStatus);
}
