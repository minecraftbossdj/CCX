package com.awesoft.ccx_drones.luaFunctions;

import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;

import java.util.HashMap;
import java.util.Map;

public class ModemUpgrade {
    private DroneEntity drone;

    public ModemUpgrade(DroneEntity entity) {
        drone = entity;
    }

    ILuaFunction getPos = args -> {
        if (drone.hasUpgrade("ccx_drones:modem_upgrade")) {
            Map<String, Object> posinfo = new HashMap<>();
            posinfo.put("x", drone.position().x);
            posinfo.put("y", drone.position().y);
            posinfo.put("z", drone.position().z);
            return MethodResult.of(true,posinfo);
        }
        return MethodResult.of(false,"Modem Upgrade Not Installed!");
    };

    public Map<String, Object> getMethods(Map<String, Object> info) {
        info.put("getPos",getPos);
        return info;
    }
}
