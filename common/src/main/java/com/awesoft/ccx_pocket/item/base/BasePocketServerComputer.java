package com.awesoft.ccx_pocket.item.base;

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

public class BasePocketServerComputer extends ServerComputer {
    private final BasePocketBrain brain;
    private int oldLightColour = -1;
    @Nullable
    private ComputerState oldComputerState;
    private Set<ServerPlayer> tracking = Set.of();

    public BasePocketServerComputer(BasePocketBrain brain, BasePocketHolder holder, Properties properties) {
        super(holder.level(), holder.blockPos(), properties
                //.terminalSize((Integer)ConfigSpec.computerTermWidth.get(), (Integer)ConfigSpec.computerTermHeight.get()) - keep for reference, some pockets might be different size
                //.addComponent(ComputerComponents.POCKET, brain) - keep for reference, also some pocket computer might not add pocket api
                //add apis/components here
        );
        this.brain = brain;
    }

    public BasePocketBrain getBrain() {
        return this.brain;
    }


    protected void onRemoved() {
        super.onRemoved();
        ServerNetworking.sendToAllPlayers(new PocketComputerDeletedClientMessage(this.getInstanceUUID()), this.getLevel().getServer());
    }
}

