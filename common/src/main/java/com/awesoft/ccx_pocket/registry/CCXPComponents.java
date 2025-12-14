package com.awesoft.ccx_pocket.registry;

import com.awesoft.ccx_drones.api.DroneAPI;
import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import com.awesoft.ccx_pocket.api.HeadMountDisplayAPI;
import com.awesoft.ccx_pocket.api.PocketPackAPI;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.component.ComputerComponent;
import dan200.computercraft.api.pocket.IPocketAccess;

public class CCXPComponents {
    public static final ComputerComponent<IPocketAccess> HMDAPI = ComputerComponent.create("ccx_pocket", "hmd");
    public static final ComputerComponent<IPocketAccess> POCKETPACKAPI = ComputerComponent.create("ccx_pocket", "pocket_pack");

    public static void register() {
        ComputerCraftAPI.registerAPIFactory(computer -> {
            var entity = computer.getComponent(HMDAPI);
            return entity == null ? null : new HeadMountDisplayAPI(computer,entity);
        });
        ComputerCraftAPI.registerAPIFactory(computer -> {
            var entity = computer.getComponent(POCKETPACKAPI);
            return entity == null ? null : new PocketPackAPI(computer,entity);
        });
    }
}
