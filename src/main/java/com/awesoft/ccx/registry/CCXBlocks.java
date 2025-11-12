package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.rack.RackBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CCXBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, CCX.MODID);

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> registryObject = registerNoItem(name, block);
        CCXItems.ITEMS.register(name, () -> new BlockItem(registryObject.get(), new Item.Properties()));
        return registryObject;
    }

    public static final RegistryObject<Block> RACK = register("rack", () -> new RackBlock(BlockBehaviour.Properties.of()));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
