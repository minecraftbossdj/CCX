package com.awesoft.ccx_drones.registry;

import com.awesoft.ccx_drones.api.DroneAPI;
import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.component.ComputerComponent;

public class CCXDComponents {
    public static final ComputerComponent<DroneEntity> DRONEAPI = ComputerComponent.create("ccx_drones", "drone");

    public static void register() {
        ComputerCraftAPI.registerAPIFactory(computer -> {
            var entity = computer.getComponent(DRONEAPI);
            return entity == null ? null : new DroneAPI(entity);
        });
    }
}
