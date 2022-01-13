package idl.DataSource;

import idl.Data.Item;

import java.util.ArrayList;
import java.util.List;

public interface ItemDataSource {
    List<Item> getItemForUUID(String uuid, int status);

    void migrate();

    boolean updateStatus(ArrayList<Integer> gotIds, int newStatus);
}
