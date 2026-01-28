package com.awesoft.ccx_upgrades.block.upgradeStation

import com.awesoft.ccx.CCX
import com.awesoft.ccx.block.pcie.PCIeBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Containers
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class UpgradeStationInventory(var parent: UpgradeStationEntity, size: Int): SimpleContainer(size) {

    override fun setChanged() {
        super.setChanged()
        if (parent.level?.isClientSide() == false) {
            parent.level?.sendBlockUpdated(
                parent.getBlockPos(),
                parent.getBlockState(),
                parent.getBlockState(),
                3
            )
        }
        if (!parent.suppressUpdates) {
            parent.onChange()
        }
    }

    private fun getItems(): NonNullList<ItemStack> {
        val list = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY)
        for (i in 0 until getContainerSize()) {
            list[i] = getItem(i)
        }
        return list
    }

    fun save(tag: CompoundTag): CompoundTag {
        ContainerHelper.saveAllItems(tag, getItems())
        return tag
    }

    fun load(tag: CompoundTag) {
        val items = NonNullList.withSize(containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, items)
        for (i in 0 until minOf(items.size, containerSize)) {
            super.setItem(i, items.get(i))
        }
    }


    override fun setItem(slot: Int, stack: ItemStack) {
        if (parent.level == null) {
            CCX.LOGGER.info("how what")
            return
        }
        if (canPlaceItem(slot, stack)) {
            super.setItem(slot, stack)
        } else {
            val pos: BlockPos = parent.getBlockPos()
            Containers.dropItemStack(
                parent.level!!,
                pos.x.toDouble(),
                pos.y + 0.75,
                pos.z.toDouble(),
                stack
            )
        }
        parent.onChange()
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val result = super.removeItem(slot, amount)
        parent.onChange()
        return result
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        val result = super.removeItemNoUpdate(slot)
        parent.onChange()
        return result
    }

    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean {
        return true
    }
}