package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.pcReader.PCReaderBlock;
import com.awesoft.ccx.block.pcie.PCIeBlock;
import com.awesoft.ccx.block.rack.RackBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class CCXBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(CCX.MOD_ID, Registries.BLOCK);

    private static <T extends Block> RegistrySupplier<T> registerNoItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistrySupplier<T> register(String name, Supplier<T> block) {
        RegistrySupplier<T> registryObject = registerNoItem(name, block);
        CCXItems.ITEMS.register(name, () -> new BlockItem(registryObject.get(), new Item.Properties()));
        return registryObject;
    }

    public static final RegistrySupplier<Block> RACK =
            register("rack", () -> new RackBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistrySupplier<Block> PCIE_HUB =
            register("pcie_hub", () -> new PCIeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistrySupplier<Block> PC_READER =
            register("pc_reader", () -> new PCReaderBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)));

    public static void register() {
        BLOCKS.register();
    }
}
