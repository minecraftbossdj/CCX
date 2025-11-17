package com.awesoft.ccx_drones.registry;

import com.awesoft.ccx_drones.CCXDrones;
import com.awesoft.ccx_drones.menu.DroneMenu;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import dan200.computercraft.shared.platform.RegistryEntry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

public class CCXDMenu {
    public static final DeferredRegister<MenuType<?>> MENU =
            DeferredRegister.create(CCXDrones.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<DroneMenu>> DRONE_MENU = MENU.register("drone", () -> ContainerData.toType(ComputerContainerData::new, DroneMenu::ofMenuData));

    public static void register() {
        MENU.register();
    }
}
