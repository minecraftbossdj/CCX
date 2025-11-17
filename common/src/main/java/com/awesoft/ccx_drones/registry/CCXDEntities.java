package com.awesoft.ccx_drones.registry;

import com.awesoft.ccx_drones.CCXDrones;
import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class CCXDEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(CCXDrones.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<DroneEntity>> DRONE_ENTITY = ENTITY_TYPES.register(
            "drone",
            () -> EntityType.Builder.of(DroneEntity::new, MobCategory.MISC)
                    .sized(1F,1F)
                    .build(CCXDrones.MOD_ID+":drone")
    );



    public static void register() {
        ENTITY_TYPES.register();
        EntityAttributeRegistry.register(DRONE_ENTITY, DroneEntity::createMobAttributes);
    }
}
