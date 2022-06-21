package idl;

import idl.Data.IDLItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public interface ItemChecker {
    int PENDING = 0;
    int DONE = 1;

    int check(Player player);

    List<IDLItemStack> get(Player player);

    boolean updateStatus(ArrayList<Integer> gotIds, int newStatus);
}
