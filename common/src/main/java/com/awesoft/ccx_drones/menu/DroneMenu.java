package com.awesoft.ccx_drones.menu;

import com.awesoft.ccx_drones.entity.drone.DroneBrain;
import com.awesoft.ccx_drones.menu.slots.DroneUpgradeSlot;
import com.awesoft.ccx_drones.menu.slots.DroneUpgradeType;
import com.awesoft.ccx_drones.registry.CCXDMenu;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.container.SingleContainerData;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Predicate;

public class DroneMenu extends AbstractComputerMenu {
    public static final int BORDER = 8;
    public static final int PLAYER_START_Y = 134;
    public static final int TURTLE_START_X = SIDEBAR_WIDTH + 175;
    public static final int PLAYER_START_X = SIDEBAR_WIDTH + BORDER;
    public static final int UPGRADE_START_X = 211;


    int PLAYER_INV_START = 0;
    int PLAYER_HOTBAR_START = 27;
    int DRONE_UPGRADE_SLOT = 36;


    private final ContainerData data;

    public DroneMenu(
            int id, Predicate<Player> canUse, ComputerFamily family, @Nullable ServerComputer computer, @Nullable ComputerContainerData menuData,
            Inventory playerInventory, Container droneUpgrades, ContainerData data
    ) {
        super(CCXDMenu.DRONE_MENU.get(), id, canUse, family, computer, menuData);
        this.data = data;
        addDataSlots(data);

        // Player inventory
        for (var y = 0; y < 3; y++) {
            for (var x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, PLAYER_START_X + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Player hotbar
        for (var x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, PLAYER_START_X + x * 18, PLAYER_START_Y + 3 * 18 + 5));
        }

        // Turtle upgrades
        addSlot(new DroneUpgradeSlot(droneUpgrades, DroneUpgradeType.TOP, 0, UPGRADE_START_X, PLAYER_START_Y+1));
        addSlot(new DroneUpgradeSlot(droneUpgrades, DroneUpgradeType.INTERNAL, 1, UPGRADE_START_X, PLAYER_START_Y+19));
        addSlot(new DroneUpgradeSlot(droneUpgrades, DroneUpgradeType.LEFT, 2, UPGRADE_START_X-18, PLAYER_START_Y+19));
        addSlot(new DroneUpgradeSlot(droneUpgrades, DroneUpgradeType.RIGHT, 3, UPGRADE_START_X+18, PLAYER_START_Y+19));
        addSlot(new DroneUpgradeSlot(droneUpgrades, DroneUpgradeType.BOTTOM, 4, UPGRADE_START_X, PLAYER_START_Y+37));

        addSlot(new DroneUpgradeSlot(droneUpgrades, DroneUpgradeType.FRONT, 5, UPGRADE_START_X+36, PLAYER_START_Y+1));
        addSlot(new DroneUpgradeSlot(droneUpgrades, DroneUpgradeType.BACK, 6, UPGRADE_START_X+36, PLAYER_START_Y+37));
        //addSlot(new UpgradeSlot(turtleUpgrades, TurtleSide.RIGHT, 1, UPGRADE_START_X, PLAYER_START_Y + 1 + 18));
    }

    public static DroneMenu ofBrain(int id, Inventory player, DroneBrain drone) {
        return new DroneMenu(
                // Laziness in turtle.getOwner() is important here!
                id,
                p -> drone.getOwner().canPlayerUse(p),
                ComputerFamily.ADVANCED,
                drone.getOwner().createOrUpkeepComputer(),
                null,
                player,
                drone.getOwner().getInventory(),
                (SingleContainerData) drone::getSelectedSlot
        );
    }

    public static DroneMenu ofMenuData(int id, Inventory player, ComputerContainerData data) {
        return new DroneMenu(
                id, x -> true, data.family(), null, data,
                player, new SimpleContainer(7), new SimpleContainerData(1)
        );
    }

    private ItemStack tryItemMerge(Player player, int slotNum, int firstSlot, int lastSlot, boolean reverse) {
        var slot = slots.get(slotNum);
        var originalStack = ItemStack.EMPTY;
        if (slot != null && slot.hasItem()) {
            var clickedStack = slot.getItem();
            originalStack = clickedStack.copy();
            if (!moveItemStackTo(clickedStack, firstSlot, lastSlot, reverse)) {
                return ItemStack.EMPTY;
            }

            if (clickedStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (clickedStack.getCount() != originalStack.getCount()) {
                slot.onTake(player, clickedStack);
            } else {
                return ItemStack.EMPTY;
            }
        }
        return originalStack;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotNum) {
        int playerInvStart = 0;
        int playerInvEnd = 27;
        int hotbarStart = playerInvEnd;
        int hotbarEnd = hotbarStart + 9;
        int droneStart = hotbarEnd;
        int droneEnd = droneStart + 7;

        if (slotNum >= droneStart && slotNum < droneEnd) {
            return tryItemMerge(player, slotNum, playerInvStart, hotbarEnd, true);
        } else if (slotNum >= playerInvStart && slotNum < hotbarEnd) {
            return tryItemMerge(player, slotNum, droneStart, droneEnd, false);
        }
        return ItemStack.EMPTY;
    }


}
