package com.awesoft.ccx_pocket.item.PocketPack;

import com.awesoft.ccx_pocket.registry.CCXPComponents;
import dan200.computercraft.api.component.ComputerComponents;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.config.ConfigSpec;
import dan200.computercraft.shared.network.client.PocketComputerDeletedClientMessage;
import dan200.computercraft.shared.network.server.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class PocketPackServerComputer extends ServerComputer {
    private final PocketPackBrain brain;
    private int oldLightColour = -1;
    @Nullable
    private ComputerState oldComputerState;
    private Set<ServerPlayer> tracking = Set.of();

    PocketPackServerComputer(PocketPackBrain brain, PocketPackHolder holder, Properties properties) {
        super(holder.level(), holder.blockPos(), properties
                .terminalSize((Integer)ConfigSpec.computerTermWidth.get(), (Integer)ConfigSpec.computerTermHeight.get())
                .addComponent(ComputerComponents.POCKET, brain)
                .addComponent(CCXPComponents.POCKETPACKAPI, brain)
        );
        this.brain = brain;
    }

    public PocketPackBrain getBrain() {
        return this.brain;
    }


    protected void onRemoved() {
        super.onRemoved();
        ServerNetworking.sendToAllPlayers(new PocketComputerDeletedClientMessage(this.getInstanceUUID()), this.getLevel().getServer());
    }
}

