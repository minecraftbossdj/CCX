package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.rack.RackBlockEntity;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CCXBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CCX.MODID);

    public static final RegistryObject<BlockEntityType<RackBlockEntity>> RACK_ENTITY = BLOCK_ENTITIES.register("rack",
            () -> BlockEntityType.Builder.of(
                    RackBlockEntity::new,
                    CCXBlocks.RACK.get()
            ).build(null)
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
