package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CCXTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(CCX.MOD_ID, Registries.CREATIVE_MODE_TAB);


    public static final RegistrySupplier<CreativeModeTab> CCX_MAIN = CREATIVE_MODE_TABS.register("ccx.main",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP,6)
                    .title(Component.translatable("itemGroup.ccx.main"))
                    .icon(() -> new ItemStack(CCXBlocks.RACK.get()))
                    .displayItems((displayParms,output) ->{
                        output.accept(CCXBlocks.RACK.get());
                        output.accept(CCXItems.SERVER_ADVANCED.get());
                        output.accept(CCXItems.SERVER_REMOTE.get());
                        output.accept(CCXItems.REMOTE_TERMINAL.get());
                        output.accept(CCXItems.REMOTE_TERMINAL_ADVANCED.get());
                        output.accept(CCXBlocks.PCIE_HUB.get());
                        //output.accept(CCXItems.PERIPHERAL_CARD.get());
                        output.accept(CCXItems.INVENTORY_CARD.get());
                        output.accept(CCXItems.FAKE_CARD.get());
                        output.accept(CCXBlocks.PC_READER.get());
                    })
                    .build()
    );

    public static void register() {
        CREATIVE_MODE_TABS.register();
    }
}
