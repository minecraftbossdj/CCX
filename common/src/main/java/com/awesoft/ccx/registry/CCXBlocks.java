package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.pcReader.PCReaderBlock;
import com.awesoft.ccx.block.pcie.PCIeBlock;
import com.awesoft.ccx.block.rack.RackBlock;
import com.awesoft.ccx.block.usb.USBBlock;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.platform.RegistrationHelper;
import dan200.computercraft.shared.platform.RegistryEntry;
import dan200.computercraft.shared.turtle.blocks.TurtleBlock;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import dan200.computercraft.shared.turtle.items.TurtleItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
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

    public static final RegistrySupplier<Block> USB_PORT =
            register("usb_port", () -> new USBBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)));


    static final RegistrationHelper<Block> CCBLOCKS = PlatformHelper.get().createRegistrationHelper(Registries.BLOCK);

    static final RegistrationHelper<BlockEntityType<?>> CCBLOCKENTITIES = PlatformHelper.get().createRegistrationHelper(Registries.BLOCK_ENTITY_TYPE);

    private static BlockBehaviour.Properties turtleProperties() {
        return BlockBehaviour.Properties.of().strength(2.5f);
    }

    private static <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> ofBlock(RegistryEntry<? extends Block> block, BiFunction<BlockPos, BlockState, T> factory) {
        return CCBLOCKENTITIES.register(block.id().getPath(), () -> PlatformHelper.get().createBlockEntityType(factory, block.get()));
    }

    public static final RegistryEntry<TurtleBlock> TURTLE_COMMAND = CCBLOCKS.register("turtle_command",
            () -> new TurtleBlock(turtleProperties().mapColor(MapColor.GOLD).explosionResistance(TurtleBlock.IMMUNE_EXPLOSION_RESISTANCE), CCXBlocks.TURTLE_COMMAND_ENTITY));

    public static final RegistryEntry<BlockEntityType<TurtleBlockEntity>> TURTLE_COMMAND_ENTITY =
            ofBlock(CCXBlocks.TURTLE_COMMAND, (p, s) -> new TurtleBlockEntity(CCXBlocks.TURTLE_COMMAND_ENTITY.get(), p, s, () -> Config.advancedTurtleFuelLimit, ComputerFamily.COMMAND));

    static final RegistrationHelper<Item> CCITEM = PlatformHelper.get().createRegistrationHelper(Registries.ITEM);

    private static Item.Properties properties() {
        return new Item.Properties();
    }

    private static <B extends Block, I extends Item> RegistryEntry<I> ofBlock(BiFunction<B, Item.Properties, I> supplier) {
        return CCITEM.register(CCXBlocks.TURTLE_COMMAND.id().getPath(), () -> supplier.apply(((RegistryEntry<B>) CCXBlocks.TURTLE_COMMAND).get(), properties()));
    }

    public static final RegistryEntry<TurtleItem> TURTLE_COMMAND_ITEM = ofBlock(TurtleItem::new);


    public static void register() {
        BLOCKS.register();
        CCBLOCKS.register();
        CCBLOCKENTITIES.register();
        CCITEM.register();
    }
}
