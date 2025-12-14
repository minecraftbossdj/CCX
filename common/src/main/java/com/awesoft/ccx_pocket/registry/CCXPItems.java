package com.awesoft.ccx_pocket.registry;

import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import com.awesoft.ccx_drones.CCXDrones;
import com.awesoft.ccx_drones.item.CrowbarItem;
import com.awesoft.ccx_drones.item.DroneItem;
import com.awesoft.ccx_pocket.CCXPocket;
import com.awesoft.ccx_pocket.item.PocketPack.PocketPackItem;
import com.awesoft.ccx_pocket.item.hmd.HMDPocketItem;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class CCXPItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(CCXPocket.MOD_ID, Registries.ITEM);
/*
    public static final RegistrySupplier<Item> DRONE_ITEM = ITEMS.register("drone",
            () -> new DroneItem(new Item.Properties().stacksTo(1))
    );*/

    public static final RegistrySupplier<HMDPocketItem> HMD_ADVANCED = ITEMS.register("hmd_advanced",
            () -> new HMDPocketItem(new Item.Properties().stacksTo(1), ComputerFamily.ADVANCED));

    public static final RegistrySupplier<PocketPackItem> POCKET_PACK_ADVANCED = ITEMS.register("pocket_pack_advanced",
            () -> new PocketPackItem(new Item.Properties().stacksTo(1), ComputerFamily.ADVANCED));

    public static void register() {
        ITEMS.register();
    }
}
