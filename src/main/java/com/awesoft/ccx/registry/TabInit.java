package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = CCX.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TabInit {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CCX.MODID);


    public static final RegistryObject<CreativeModeTab> CCX_MAIN = TabInit.CREATIVE_MODE_TABS.register("ccx.main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ccx.main"))
                    .icon(() -> new ItemStack(CCXBlocks.RACK.get()))
                    .displayItems((displayParms,output) ->{
                        output.accept(CCXBlocks.RACK.get());
                        output.accept(CCXItems.SERVER_ADVANCED.get());
                    })
                    .build()
    );


}
