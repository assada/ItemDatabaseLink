package idl.Data;

import org.bukkit.inventory.ItemStack;

public class IDLItemStack {
    private Item item;
    private ItemStack itemStack;

    public IDLItemStack(Item item, ItemStack itemStack) {
        this.item = item;

        this.itemStack = itemStack;
    }

    public IDLItemStack(Item item) {
        this.item = item;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
