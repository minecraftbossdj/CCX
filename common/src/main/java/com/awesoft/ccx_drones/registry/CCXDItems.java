package com.awesoft.ccx_drones.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.item.AdvancedRemoteTerminalItem;
import com.awesoft.ccx_drones.CCXDrones;
import com.awesoft.ccx_drones.item.CrowbarItem;
import com.awesoft.ccx_drones.item.DroneItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CCXDItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(CCXDrones.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> DRONE_ITEM = ITEMS.register("drone",
            () -> new DroneItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistrySupplier<Item> CROWBAR = ITEMS.register("crowbar",
            CrowbarItem::new
    );

    public static final RegistrySupplier<Item> MINE_UPGRADE = ITEMS.register("mine_upgrade",
            () -> new Item(new Item.Properties().stacksTo(1))
    );
    public static final RegistrySupplier<Item> CARRY_UPGRADE = ITEMS.register("carry_upgrade",
            () -> new Item(new Item.Properties().stacksTo(1))
    );
    public static final RegistrySupplier<Item> SURVEY_UPGRADE = ITEMS.register("survey_upgrade",
            () -> new Item(new Item.Properties().stacksTo(1))
    );
    public static final RegistrySupplier<Item> MODEM_UPGRADE = ITEMS.register("modem_upgrade",
            () -> new Item(new Item.Properties().stacksTo(1))
    );



    public static void register() {
        ITEMS.register();
    }
}
