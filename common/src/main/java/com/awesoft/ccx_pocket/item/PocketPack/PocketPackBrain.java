package com.awesoft.ccx_pocket.item.PocketPack;

import com.awesoft.ccx_pocket.item.base.BasePocketBrain;
import com.awesoft.ccx_pocket.item.base.BasePocketHolder;
import com.awesoft.ccx_pocket.item.base.BasePocketServerComputer;
import com.awesoft.ccx_pocket.item.hmd.HMDHolder;
import com.awesoft.ccx_pocket.item.hmd.HMDServerComputer;
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


public final class PocketPackBrain extends BasePocketBrain {
    private final PocketPackServerComputer computer;
    private PocketPackHolder holder;

    public PocketPackBrain(PocketPackHolder holder, @Nullable UpgradeData<IPocketUpgrade> upgrade, ServerComputer.Properties properties) {
        super(holder, upgrade, properties);
        this.computer = (PocketPackServerComputer) super.computer();
        this.holder = holder;
        this.position = holder.pos();
        this.upgrade = UpgradeData.copyOf(upgrade);
        this.invalidatePeripheral();
    }

    @Override
    public BasePocketServerComputer createComputer(BasePocketBrain brain, BasePocketHolder holder, ServerComputer.Properties properties) {
        return new PocketPackServerComputer(this, (PocketPackHolder) holder, properties);
    }

    public PocketPackServerComputer computer() {
        return this.computer;
    }

    PocketPackHolder holder() {
        return this.holder;
    }

    public void updateHolder(PocketPackHolder newHolder) {
        this.position = newHolder.pos();
        this.computer.setPosition(newHolder.level(), newHolder.blockPos());
        PocketPackHolder oldHolder = this.holder;
        if (!this.holder.equals(newHolder)) {
            this.holder = newHolder;
            ServerPlayer var10000;
            if (oldHolder instanceof PocketPackHolder.PlayerHolder) {
                PocketPackHolder.PlayerHolder p = (PocketPackHolder.PlayerHolder)oldHolder;
                var10000 = p.entity();
            } else {
                var10000 = null;
            }

            ServerPlayer oldPlayer = var10000;
            if (newHolder instanceof PocketPackHolder.PlayerHolder) {
                PocketPackHolder.PlayerHolder player = (PocketPackHolder.PlayerHolder)newHolder;
            }

        }
    }

    @Override
    @Nullable
    public Entity getEntity() {
        PocketPackHolder var2 = this.holder;
        Entity var10000;
        if (var2 instanceof PocketPackHolder.EntityHolder entity) {
            if (this.holder.isValid(this.computer)) {
                var10000 = entity.entity();
                return var10000;
            }
        }

        var10000 = null;
        return var10000;
    }
}
