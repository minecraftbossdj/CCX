package com.awesoft.ccx_upgrades.block.upgradeStation

import com.awesoft.ccx.registry.CCXItems
import com.awesoft.ccx_upgrades.CCXUpgrades.LOGGER
import com.awesoft.ccx_upgrades.knockoffRecipes.RECIPES
import com.awesoft.ccx_upgrades.lib.rotate
import com.awesoft.ccx_upgrades.registry.CCXUBlockEntities
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.joml.Vector3f


class UpgradeStationEntity(blockPos: BlockPos?, blockState: BlockState?): BlockEntity(
    CCXUBlockEntities.UPGRADE_STATION_ENTITY?.get(),
    blockPos,
    blockState
), Container {
    private val inventory = UpgradeStationInventory(this, 3)

    var suppressUpdates: Boolean = false

    fun onChange() {
        setChanged()
        if (level == null || level!!.isClientSide) return
        if (suppressUpdates) return
        level!!.sendBlockUpdated(worldPosition, blockState, blockState, 3)
    }

    var tick: Int = 0

    fun tick(level: Level, pos: BlockPos, state: BlockState, be: UpgradeStationEntity) {
        tick++
        for (recipe in RECIPES) {
            if (inventory.getItem(0).item == recipe[0] && inventory.getItem(1).item == recipe[1]) {
                inventory.removeItem(0, 1)
                inventory.removeItem(1, 1)
                inventory.setItem(2, ItemStack(recipe[2],1))
            }
        }
    }

    fun onRemove() {
        for (i in 0 until getContainerSize()) {
            Block.popResource(level!!, blockPos, getItem(i) ?: ItemStack(Items.AIR))
        }
    }

    fun onUse(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult) {
        if (level.isClientSide) return
        val slot = getSlot(blockPos, player as ServerPlayer)
        if (slot != -1) {
            val item = player.getItemInHand(hand)
            if (item.isEmpty) {
                val invItem = inventory.getItem(slot-1)
                if (!invItem.isEmpty) {
                    val itemCopy = invItem.copy()
                    inventory.removeItem(slot-1,64)
                    player.inventory.add(itemCopy)
                }
            } else {
                LOGGER.info("hand item is NOT empty")
                LOGGER.info(slot)
                if (inventory.getItem(slot-1).isEmpty && slot != 3) {
                    inventory.setItem(slot-1, item.copy())
                    player.inventory.removeItem(item)
                }
            }
        }
    }

    fun getSlot(blockPos: BlockPos, player: ServerPlayer): Int {

        val facing = this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        val axis = Vector3f(0f, 1f, 0f)
        var rotationDegrees: Double = 0.0

        rotationDegrees = when (facing) {
            Direction.DOWN -> 0.0
            Direction.UP -> 0.0
            Direction.NORTH -> 0.0
            Direction.SOUTH -> 180.0
            Direction.WEST -> 90.0
            Direction.EAST -> -90.0
        }

        val angle = Math.toRadians(rotationDegrees).toFloat()

        val rotation = Quaternionf(AxisAngle4f(angle, axis.x(), axis.y(), axis.z()))

        val eyePos = player.eyePosition
        val lookVec = player.lookAngle
        val maxDistance = 5.0

        val slots = listOf(
            Triple( // slot 1
                Vec3(11 * 0.0625, 13 * 0.0625, 7 * 0.0625),
                Vec3(15 * 0.0625, 17 * 0.0625, 11 * 0.0625),
                1
            ),
            Triple( // slot 2
                Vec3(5 * 0.0625, 13 * 0.0625, 11 * 0.0625),
                Vec3(1 * 0.0625, 17 * 0.0625, 7 * 0.0625),
                2
            ),
            Triple( // slot 3
                Vec3(10 * 0.0625, 13 * 0.0625, 6 * 0.0625),
                Vec3(6 * 0.0625, 17 * 0.0625, 2 * 0.0625),
                3
            )
        )

        val blockVec = Vec3(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())

        var closestSlot: Triple<Vec3, Vec3, Int>? = null
        var closestDistance = Double.MAX_VALUE

        val rayEnd = eyePos.add(lookVec.scale(maxDistance))

        val slotId = 0

        for ((minLocal, maxLocal, slotId) in slots) {
            val rotatedMin = minLocal.rotate(rotation)
            val rotatedMax = maxLocal.rotate(rotation)

            val minWorld = blockVec.add(rotatedMin)
            val maxWorld = blockVec.add(rotatedMax)

            val aabb = AABB(minWorld, maxWorld)

            val hit = aabb.clip(eyePos, rayEnd)
            if (hit.isPresent) {
                val distance = eyePos.distanceToSqr(hit.get())
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestSlot = Triple(minLocal, maxLocal, slotId)

                }
            }
        }

        closestSlot?.let { (minLocal, maxLocal, slot) ->
            return slot
        }
        return -1
    }

    //inv stuff
    override fun getContainerSize(): Int {
        return inventory.containerSize
    }

    fun getInventory(): SimpleContainer {
        return inventory
    }

    override fun isEmpty(): Boolean {
        return inventory.isEmpty
    }

    override fun getItem(i: Int): ItemStack? {
        return inventory.getItem(i)
    }

    override fun removeItem(slot: Int, count: Int): ItemStack? {
        return inventory.removeItem(slot, count)
    }

    override fun removeItemNoUpdate(i: Int): ItemStack? {
        return inventory.removeItemNoUpdate(i)
    }

    override fun setItem(slot: Int, itemStack: ItemStack) {
        return inventory.setItem(slot, itemStack)
    }

    override fun stillValid(player: Player): Boolean {
        return inventory.stillValid(player)
    }

    override fun clearContent() {
        return inventory.clearContent()
    }
    //end of inv stuff

    //save/load stuff
    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Inventory", inventory.save(CompoundTag()))
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        suppressUpdates = true
        try {
            if (tag.contains("Inventory")) {
                inventory.load(tag.getCompound("Inventory"))
            }
        } finally {
            suppressUpdates = false
        }
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = CompoundTag()
        this.saveAdditional(tag)
        return tag
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener?>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }
}
