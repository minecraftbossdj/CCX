package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CCXItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CCX.MODID);

    public static final RegistryObject<ServerPocketItem> SERVER_ADVANCED = ITEMS.register("server_advanced",
            () -> new ServerPocketItem(new Item.Properties().stacksTo(1), ComputerFamily.ADVANCED));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
