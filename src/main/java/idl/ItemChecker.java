package idl;

import idl.Data.IDLItemStack;
import idl.Data.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public interface ItemChecker {
    int check(Player player);

    List<IDLItemStack> get(Player player);

    boolean updateStatus(ArrayList<Integer> gotIds, int newStatus);
}
