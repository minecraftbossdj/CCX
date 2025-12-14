package com.awesoft.ccx_pocket.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx_pocket.CCXPocket;
import dan200.computercraft.shared.ModRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CCXPTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(CCXPocket.MOD_ID, Registries.CREATIVE_MODE_TAB);


    public static final RegistrySupplier<CreativeModeTab> CCX_POCKET_MAIN = CREATIVE_MODE_TABS.register("ccx_pocket.main",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP,6)
                    .title(Component.translatable("itemGroup.ccx_pocket.main"))
                    .icon(() -> new ItemStack(ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get()))
                    .displayItems((displayParms,output) ->{
                        output.accept(CCXPItems.HMD_ADVANCED.get());
                        output.accept(CCXPItems.POCKET_PACK_ADVANCED.get());
                    })
                    .build()
    );

    public static void register() {
        CREATIVE_MODE_TABS.register();
    }
}
