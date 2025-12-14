package com.awesoft.ccx.item.rack.server;

import dan200.computercraft.api.component.AdminComputer;
import dan200.computercraft.api.component.ComputerComponents;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.config.ConfigSpec;
import dan200.computercraft.shared.network.client.PocketComputerDeletedClientMessage;
import dan200.computercraft.shared.network.server.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class ServerServerComputer extends ServerComputer {
    private final ServerBrain brain;
    private int oldLightColour = -1;
    @Nullable
    private ComputerState oldComputerState;
    private Set<ServerPlayer> tracking = Set.of();

    ServerServerComputer(ServerBrain brain, ServerHolder holder, Properties properties) {
        super(holder.level(), holder.blockPos(), properties.terminalSize((Integer)ConfigSpec.computerTermWidth.get(), (Integer)ConfigSpec.computerTermHeight.get()));
        this.brain = brain;
        if (brain.computer() != null && brain.computer().getFamily() == ComputerFamily.COMMAND) {
            properties.addComponent(ComputerComponents.ADMIN_COMPUTER, new AdminComputer() {
                @Override
                public int permissionLevel() {
                    return 4;
                }
            });
        }
    }

    public ServerBrain getBrain() {
        return this.brain;
    }


    protected void onRemoved() {
        super.onRemoved();
        ServerNetworking.sendToAllPlayers(new PocketComputerDeletedClientMessage(this.getInstanceUUID()), this.getLevel().getServer());
    }
}

