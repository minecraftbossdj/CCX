package com.awesoft.ccx_pocket.item.hmd;

import com.awesoft.ccx_pocket.item.base.BasePocketBrain;
import com.awesoft.ccx_pocket.item.base.BasePocketHolder;
import com.awesoft.ccx_pocket.item.base.BasePocketServerComputer;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;


public final class HMDBrain extends BasePocketBrain {
    private final HMDServerComputer computer;
    private HMDHolder holder;

    public HMDBrain(HMDHolder holder, @Nullable UpgradeData<IPocketUpgrade> upgrade, ServerComputer.Properties properties) {
        super(holder, upgrade, properties);
        this.computer = (HMDServerComputer) super.computer(); //this feels jank as im not sure if its ALWAYS gonna be HMDServerComputer but eh whatever
        this.holder = holder;
        this.position = holder.pos();
        this.upgrade = UpgradeData.copyOf(upgrade);
        this.invalidatePeripheral();
    }

    @Override
    public BasePocketServerComputer createComputer(BasePocketBrain brain, BasePocketHolder holder, ServerComputer.Properties properties) {
        return new HMDServerComputer(this, (HMDHolder) holder, properties);
    }

    public HMDServerComputer computer() {
        return this.computer;
    }

    HMDHolder holder() {
        return this.holder;
    }

    public void updateHolder(HMDHolder newHolder) {
        this.position = newHolder.pos();
        this.computer.setPosition(newHolder.level(), newHolder.blockPos());
        HMDHolder oldHolder = this.holder;
        if (!this.holder.equals(newHolder)) {
            this.holder = newHolder;
            ServerPlayer var10000;
            if (oldHolder instanceof HMDHolder.PlayerHolder) {
                HMDHolder.PlayerHolder p = (HMDHolder.PlayerHolder)oldHolder;
                var10000 = p.entity();
            } else {
                var10000 = null;
            }

            ServerPlayer oldPlayer = var10000;
            if (newHolder instanceof HMDHolder.PlayerHolder) {
                HMDHolder.PlayerHolder player = (HMDHolder.PlayerHolder)newHolder;
            }

        }
    }

    @Nullable
    @Override
    public Entity getEntity() {
        HMDHolder var2 = this.holder;
        Entity var10000;
        if (var2 instanceof HMDHolder.EntityHolder entity) {
            if (this.holder.isValid(this.computer)) {
                var10000 = entity.entity();
                return var10000;
            }
        }

        var10000 = null;
        return var10000;
    }
}
