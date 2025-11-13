package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CCXItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(CCX.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<ServerPocketItem> SERVER_ADVANCED = ITEMS.register("server_advanced",
            () -> new ServerPocketItem(new Item.Properties().stacksTo(1), ComputerFamily.ADVANCED));


    public static void register() {
        ITEMS.register();
    }
}
