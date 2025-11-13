package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.rack.RackBlockEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CCXBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(CCX.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<RackBlockEntity>> RACK_ENTITY =
            BLOCK_ENTITIES.register("rack", () ->
                    BlockEntityType.Builder.of(RackBlockEntity::new, CCXBlocks.RACK.get()).build(null));

    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
