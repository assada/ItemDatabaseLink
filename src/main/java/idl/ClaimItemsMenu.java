package idl;

import idl.Data.IDLItemStack;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import xyz.janboerman.guilib.api.menu.ClaimButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;
import xyz.janboerman.guilib.api.menu.PageMenu;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ClaimItemsMenu extends PageMenu<Main> {
    /**
     * rewards list
     */
    private List<IDLItemStack> rewards;
    /**
     * list indices
     */
    private int rewardStartIndex /*inclusive*/, rewardEndIndex /*exclusive*/;

    /**
     * Creates the ClaimItemsMenu
     *
     * @param plugin   the plugin
     * @param pageSize the size of the embedded page (9 - 45)
     * @param rewards  a mutable list of reward items
     */
    public ClaimItemsMenu(Main plugin, int pageSize, List<IDLItemStack> rewards) {
        this(plugin, pageSize, rewards, 0, Math.min(rewards.size(), pageSize));
    }

    /**
     * Creates the ClaimItemsMenu
     *
     * @param plugin           the plugin
     * @param pageSize         the size of the embedded page (9 - 45)
     * @param rewards          a mutable list of reward items
     * @param rewardStartIndex the lowerbound of the sublist we are displaying (inclusive)
     * @param rewardEndIndex   the upperbound of the sublist we are displaying (exclusive)
     */
    private ClaimItemsMenu(Main plugin, int pageSize, List<IDLItemStack> rewards, int rewardStartIndex, int rewardEndIndex) {
        super(plugin.getGuiListener(), plugin, new MenuHolder<>(plugin, pageSize), "Claim your items", null, null);
        this.rewards = rewards;
        this.rewardStartIndex = rewardStartIndex;
        this.rewardEndIndex = rewardEndIndex;
    }

    @Override
    public MenuHolder<Main> getPage() {
        //we know the GuiInventoryHolder of the page is always a MenuHolder since we always create it ourselves
        return (MenuHolder<Main>) super.getPage();
    }

    //shifts all buttons in the page after the buttons that was transferred
    //actually creates new buttons
    private void shiftButtons(int slotIndex) {
        var page = getPage();

        int listIndex = rewardStartIndex + slotIndex;
        rewards.remove(listIndex);

        while (slotIndex < page.getInventory().getSize()) {
            if (listIndex < rewards.size()) {
                page.setButton(slotIndex, new ShiftingClaimButton(rewards.get(listIndex)));
            } else {
                page.unsetButton(slotIndex);
            }

            slotIndex++;
            listIndex++;
        }

        resetButtons(); //removes the next-page button if there are no items after the current page
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        //setup rewards
        for (int slot = 0; slot < getPageSize() && rewardStartIndex + slot < rewardEndIndex; slot++) {
            getPage().setButton(slot, new ShiftingClaimButton(rewards.get(rewardStartIndex + slot)));
        }

        //required for the page to even work
        super.onOpen(event);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        getPage().clearButtons(); //help gc

        //required
        super.onClose(event);
    }

    @Override
    public Optional<Supplier<ClaimItemsMenu>> getNextPageMenu() {
        //there is a next page if the current range upper bound is smaller than the end of the list
        if (rewardEndIndex < rewards.size()) {
            return Optional.of(() -> new ClaimItemsMenu(getPlugin(), getPageSize(), rewards, rewardEndIndex, Math.min(rewards.size(), rewardEndIndex + getPageSize())));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Supplier<ClaimItemsMenu>> getPreviousPageMenu() {
        //there is a previous page if we didn't start 0
        if (rewardStartIndex > 0) {
            return Optional.of(() -> new ClaimItemsMenu(getPlugin(), getPageSize(), rewards, Math.max(0, rewardStartIndex - getPageSize()), Math.min(rewardStartIndex, rewards.size())));
        } else {
            return Optional.empty();
        }
    }

    public class AfterFulTransferCallback implements ClaimButton.SuccessFulTransferCallback {
        private IDLItemStack idlItemStack;

        public AfterFulTransferCallback(IDLItemStack idlItemStack) {
            this.idlItemStack = idlItemStack;
        }

        @Override
        public void afterTransfer(MenuHolder menuHolder, InventoryClickEvent inventoryClickEvent, ItemStack itemStack) {
            int clickedAmount = 99999;
            String clickedMaterialName = "unknown";
            int slot = inventoryClickEvent.getSlot();
            if (inventoryClickEvent.getCurrentItem() != null) {
                clickedAmount = inventoryClickEvent.getCurrentItem().getAmount();
                clickedMaterialName = inventoryClickEvent.getCurrentItem().getType().getKey().getKey();
            }

            Bukkit.getLogger().warning("[ItemDatabaseLink] Name: %s | Slot: %d | clickedAmount: %d | clickedMaterialName: %s items transferred from DB id %d!".formatted(
                            inventoryClickEvent.getAction().name(),
                            slot,
                            clickedAmount,
                            clickedMaterialName,
                            idlItemStack.getId()
                    )
            );
            ClaimItemsMenu.this.shiftButtons(inventoryClickEvent.getSlot());
        }
    }

    public class ShiftingClaimButton extends ClaimButton<MenuHolder<Main>> {
        public ShiftingClaimButton(IDLItemStack idlItemStack) {
            super(idlItemStack.getItemStack(), new AfterFulTransferCallback(idlItemStack));
        }
    }
}
