package com.awesoft.ccx_drones.menu.slots;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DroneUpgradeSlot extends Slot {

    private final DroneUpgradeType side;

    public DroneUpgradeSlot(Container container, DroneUpgradeType side, int slot, int xPos, int yPos) {
        super(container, slot, xPos, yPos);
        this.side = side;
    }

    TagKey<Item> FRONT = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccx_drones", "drone_upgrade_front"));
    TagKey<Item> BACK = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccx_drones", "drone_upgrade_back"));
    TagKey<Item> TOP = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccx_drones", "drone_upgrade_top"));
    TagKey<Item> INTERNAL = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccx_drones", "drone_upgrade_internal"));
    TagKey<Item> BOTTOM = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccx_drones", "drone_upgrade_bottom"));
    TagKey<Item> LEFT = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccx_drones", "drone_upgrade_left"));
    TagKey<Item> RIGHT = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccx_drones", "drone_upgrade_right"));

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (side == DroneUpgradeType.FRONT) {
            return stack.is(FRONT);
        } else if (side == DroneUpgradeType.BACK) {
            return stack.is(BACK);
        } else if (side == DroneUpgradeType.TOP) {
            return stack.is(TOP);
        } else if (side == DroneUpgradeType.INTERNAL) {
            return stack.is(INTERNAL);
        } else if (side == DroneUpgradeType.BOTTOM) {
            return stack.is(BOTTOM);
        } else if (side == DroneUpgradeType.LEFT) {
            return stack.is(LEFT);
        } else if (side == DroneUpgradeType.RIGHT) {
            return stack.is(RIGHT);
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    /*
    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, side == TurtleSide.LEFT ? LEFT_UPGRADE : RIGHT_UPGRADE);
    }*/
}
