package com.awesoft.ccx_drones.luaFunctions;

import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.Map;

public class MiningUpgrade {

    private DroneEntity drone;

    public MiningUpgrade(DroneEntity entity) {
        drone = entity;
    }

    ILuaFunction breakForward = args -> {
        if(drone.hasUpgrade("ccx_drones:mine_upgrade")) {
            ClipContext context = new ClipContext(drone.getOnPos().getCenter(), drone.getOnPos().getCenter().add(drone.getForward().multiply(3, 3, 3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, drone);
            BlockHitResult result = drone.level().clip(context);

            drone.level().destroyBlock(result.getBlockPos(), true, drone);
            return MethodResult.of(true, "Broke Block!");
        } else {
            return MethodResult.of(false, "Mining Upgrade not installed!");
        }
    };

    public Map<String, Object> getMethods(Map<String, Object> info) {
        info.put("breakForward", breakForward);
        return info;
    }
}
