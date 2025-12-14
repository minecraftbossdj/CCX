package com.awesoft.ccx_pocket.item.PocketPack;

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


public final class PocketPackBrain implements IPocketAccess {
    private final PocketPackServerComputer computer;
    private PocketPackHolder holder;
    private Vec3 position;
    private boolean dirty = false;
    @Nullable
    private UpgradeData<IPocketUpgrade> upgrade;
    private int colour = -1;
    private int lightColour = -1;

    public PocketPackBrain(PocketPackHolder holder, @Nullable UpgradeData<IPocketUpgrade> upgrade, ServerComputer.Properties properties) {
        this.computer = new PocketPackServerComputer(this, holder, properties);
        this.holder = holder;
        this.position = holder.pos();
        this.upgrade = UpgradeData.copyOf(upgrade);
        this.invalidatePeripheral();
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

    public boolean updateItem(ItemStack stack) {
        if (!this.dirty) {
            return false;
        } else {
            this.dirty = false;
            IColouredItem.setColourBasic(stack, this.colour);
            PocketPackItem.setUpgrade(stack, UpgradeData.copyOf(this.upgrade));
            return true;
        }
    }

    public ServerLevel getLevel() {
        return this.computer.getLevel();
    }

    public Vec3 getPosition() {
        return this.position;
    }

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

    public int getColour() {
        return this.colour;
    }

    public void setColour(int colour) {
        if (this.colour != colour) {
            this.dirty = true;
            this.colour = colour;
        }
    }

    public int getLight() {
        return this.lightColour;
    }

    public void setLight(int colour) {
        if (colour < 0 || colour > 16777215) {
            colour = -1;
        }

        this.lightColour = colour;
    }

    public CompoundTag getUpgradeNBTData() {
        UpgradeData<IPocketUpgrade> upgrade = this.upgrade;
        return upgrade == null ? new CompoundTag() : upgrade.data();
    }

    public void updateUpgradeNBTData() {
        this.dirty = true;
    }

    public void invalidatePeripheral() {
        IPeripheral peripheral = this.upgrade == null ? null : ((IPocketUpgrade)this.upgrade.upgrade()).createPeripheral(this);
        this.computer.setPeripheral(ComputerSide.BACK, peripheral);
    }

    /** @deprecated */
    @Deprecated(
            forRemoval = true
    )
    public Map<ResourceLocation, IPeripheral> getUpgrades() {
        UpgradeData<IPocketUpgrade> upgrade = this.upgrade;
        return upgrade == null ? Map.of() : Collections.singletonMap(((IPocketUpgrade)upgrade.upgrade()).getUpgradeID(), this.computer.getPeripheral(ComputerSide.BACK));
    }

    @Nullable
    public UpgradeData<IPocketUpgrade> getUpgrade() {
        return this.upgrade;
    }

    public void setUpgrade(@Nullable UpgradeData<IPocketUpgrade> upgrade) {
        if (!Objects.equals(this.upgrade, upgrade)) {
            this.upgrade = UpgradeData.copyOf(upgrade);
            this.dirty = true;
            this.invalidatePeripheral();
        }
    }
}
