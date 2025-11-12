package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class TurtleUpgradeRegistry {

    public static final DeferredRegister<TurtleUpgradeSerialiser<?>> TURTLE_SERIALIZER = DeferredRegister.create(TurtleUpgradeSerialiser.registryId(), CCX.MODID);

    //future stuff

    public static class ID {

    }

    public static void register(IEventBus bus) {
        TURTLE_SERIALIZER.register(bus);
    }
}
