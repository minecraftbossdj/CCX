package com.awesoft.ccx_pocket.item.hmd;

import com.awesoft.ccx_pocket.registry.CCXPComponents;
import dan200.computercraft.api.component.ComputerComponent;
import dan200.computercraft.api.component.ComputerComponents;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.config.ConfigSpec;
import dan200.computercraft.shared.network.client.PocketComputerDeletedClientMessage;
import dan200.computercraft.shared.network.server.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class HMDServerComputer extends ServerComputer {
    private final HMDBrain brain;
    private int oldLightColour = -1;
    @Nullable
    private ComputerState oldComputerState;
    private Set<ServerPlayer> tracking = Set.of();

    HMDServerComputer(HMDBrain brain, HMDHolder holder, Properties properties) {
        super(holder.level(), holder.blockPos(), properties
                .terminalSize((Integer)ConfigSpec.computerTermWidth.get(), (Integer)ConfigSpec.computerTermHeight.get())
                .addComponent(ComputerComponents.POCKET, brain)
                .addComponent(CCXPComponents.HMDAPI, brain)
        );
        this.brain = brain;
    }

    public HMDBrain getBrain() {
        return this.brain;
    }


    protected void onRemoved() {
        super.onRemoved();
        ServerNetworking.sendToAllPlayers(new PocketComputerDeletedClientMessage(this.getInstanceUUID()), this.getLevel().getServer());
    }
}

