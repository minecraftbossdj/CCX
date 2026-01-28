package com.awesoft.ccx_pocket.item.hmd;

import com.awesoft.ccx_pocket.item.base.BasePocketServerComputer;
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

public final class HMDServerComputer extends BasePocketServerComputer {
    private final HMDBrain brain;

    HMDServerComputer(HMDBrain brain, HMDHolder holder, Properties properties) {
        super(brain, holder, properties
                .terminalSize((Integer)ConfigSpec.computerTermWidth.get(), (Integer)ConfigSpec.computerTermHeight.get())
                .addComponent(ComputerComponents.POCKET, brain)
                .addComponent(CCXPComponents.HMDAPI, brain)
        );
        this.brain = brain;
    }

    public HMDBrain getBrain() {
        return this.brain;
    }
}

