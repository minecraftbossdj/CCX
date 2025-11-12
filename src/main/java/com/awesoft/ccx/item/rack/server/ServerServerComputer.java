package com.awesoft.ccx.item.rack.server;

import dan200.computercraft.api.component.ComputerComponents;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.config.ConfigSpec;
import dan200.computercraft.shared.network.client.PocketComputerDataMessage;
import dan200.computercraft.shared.network.client.PocketComputerDeletedClientMessage;
import dan200.computercraft.shared.network.server.ServerNetworking;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class ServerServerComputer extends ServerComputer {
    private final ServerBrain brain;
    private int oldLightColour = -1;
    @Nullable
    private ComputerState oldComputerState;
    private Set<ServerPlayer> tracking = Set.of();

    ServerServerComputer(ServerBrain brain, ServerHolder holder, ServerComputer.Properties properties) {
        super(holder.level(), holder.blockPos(), properties.terminalSize((Integer)ConfigSpec.computerTermWidth.get(), (Integer)ConfigSpec.computerTermHeight.get()));
        this.brain = brain;
    }

    public ServerBrain getBrain() {
        return this.brain;
    }


    protected void onRemoved() {
        super.onRemoved();
        ServerNetworking.sendToAllPlayers(new PocketComputerDeletedClientMessage(this.getInstanceUUID()), this.getLevel().getServer());
    }
}

