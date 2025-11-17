package com.awesoft.ccx_drones.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.registry.CCXBlocks;
import com.awesoft.ccx.registry.CCXItems;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CCXDTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(CCX.MOD_ID, Registries.CREATIVE_MODE_TAB);


    public static final RegistrySupplier<CreativeModeTab> CCX_DRONES_MAIN = CREATIVE_MODE_TABS.register("ccx_drones.main",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP,6)
                    .title(Component.translatable("itemGroup.ccx_drones.main"))
                    .icon(() -> new ItemStack(CCXDItems.DRONE_ITEM.get()))
                    .displayItems((displayParms,output) ->{
                        output.accept(CCXDItems.DRONE_ITEM.get());
                        output.accept(CCXDItems.CROWBAR.get());
                        output.accept(CCXDItems.MINE_UPGRADE.get());
                        output.accept(CCXDItems.CARRY_UPGRADE.get());
                        output.accept(CCXDItems.SURVEY_UPGRADE.get());
                        output.accept(CCXDItems.MODEM_UPGRADE.get());
                    })
                    .build()
    );

    public static void register() {
        CREATIVE_MODE_TABS.register();
    }
}
