package com.awesoft.ccx_pocket.item.hmd;

import com.awesoft.ccx.block.rack.RackBlockEntity;
import com.awesoft.ccx_pocket.CCXPocket;
import com.awesoft.ccx_pocket.item.base.BasePocketHolder;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.lectern.CustomLecternBlockEntity;
import dan200.computercraft.shared.util.BlockEntityHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public interface HMDHolder extends BasePocketHolder {
    ServerLevel level();

    Vec3 pos();

    BlockPos blockPos();

    boolean isValid(ServerComputer var1);

    void setChanged();

    default boolean isTerminalAlwaysVisible() {
        return false;
    }

    public sealed interface EntityHolder extends HMDHolder permits HelmetHolder, ItemEntityHolder, PlayerHolder {
        Entity entity();

        default ServerLevel level() {
            return (ServerLevel)this.entity().level();
        }

        default Vec3 pos() {
            return this.entity().getEyePosition();
        }

        default BlockPos blockPos() {
            return this.entity().blockPosition();
        }
    }

    public static record PlayerHolder(ServerPlayer entity, int slot) implements EntityHolder {
        public boolean isValid(ServerComputer computer) {
            return this.entity().isAlive() && HMDPocketItem.isServerComputer(computer, this.entity().getInventory().getItem(this.slot()));
        }

        public void setChanged() {
            this.entity.getInventory().setChanged();
        }
    }

    public static record HelmetHolder(ServerPlayer entity) implements EntityHolder {
        public boolean isValid(ServerComputer computer) {
            //CCXPocket.LOGGER.info(HMDPocketItem.isServerComputer(computer, this.entity().getItemBySlot(EquipmentSlot.HEAD)));
            return this.entity().isAlive() && HMDPocketItem.isServerComputer(computer, this.entity().getItemBySlot(EquipmentSlot.HEAD));
        }

        public void setChanged() {
            this.entity.getInventory().setChanged();
        }
    }

    public static record ItemEntityHolder(ItemEntity entity) implements EntityHolder {
        public boolean isValid(ServerComputer computer) {
            return this.entity().isAlive() && HMDPocketItem.isServerComputer(computer, this.entity().getItem());
        }

        public void setChanged() {
            this.entity.setItem(this.entity.getItem().copy());
        }
    }

    public static record LecternHolder(CustomLecternBlockEntity lectern) implements HMDHolder {
        public ServerLevel level() {
            return (ServerLevel)this.lectern.getLevel();
        }

        public Vec3 pos() {
            return Vec3.atCenterOf(this.lectern.getBlockPos());
        }

        public BlockPos blockPos() {
            return this.lectern.getBlockPos();
        }

        public boolean isValid(ServerComputer computer) {
            return !this.lectern().isRemoved() && HMDPocketItem.isServerComputer(computer, this.lectern.getItem());
        }

        public void setChanged() {
            BlockEntityHelpers.updateBlock(this.lectern());
        }

        public boolean isTerminalAlwaysVisible() {
            return true;
        }
    }
}

