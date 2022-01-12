package idl.Data;

public class IDLItemStack {
    private Integer id;
    private org.bukkit.inventory.ItemStack itemStack;

    public IDLItemStack(Integer id, org.bukkit.inventory.ItemStack itemStack) {
        this.id = id;

        this.itemStack = itemStack;
    }

    public org.bukkit.inventory.ItemStack getItemStack() {
        return itemStack;
    }

    public Integer getId() {
        return id;
    }
}
