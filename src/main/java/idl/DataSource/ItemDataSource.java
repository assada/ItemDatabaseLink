package idl.DataSource;

import idl.Data.Item;

import java.util.ArrayList;
import java.util.List;

public interface ItemDataSource {
    public List<Item> getItemForUUID(String uuid, int status);

    public void migrate();

    void updateStatus(ArrayList<Integer> gotIds, int newStatus);
}
