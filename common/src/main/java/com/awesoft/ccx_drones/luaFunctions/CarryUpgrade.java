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

public class CarryUpgrade {

    private DroneEntity drone;

    public CarryUpgrade(DroneEntity entity) {
        drone = entity;
    }

    ILuaFunction pickupBlock = args -> {
        if (drone.hasUpgrade("ccx_drones:carry_upgrade")) {
            ClipContext context = new ClipContext(drone.getOnPos().getCenter(), drone.getOnPos().getCenter().add(0, -2, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, drone);
            BlockHitResult result = drone.level().clip(context);

            drone.setCarrying(result.getBlockPos());
            return MethodResult.of(true, "Picked Up Block!");
        } else {
            return MethodResult.of(false, "Carry Upgrade not installed!");
        }
    };

    ILuaFunction dropBlock = args -> {
        if (drone.hasUpgrade("ccx_drones:carry_upgrade")) {
            ClipContext context = new ClipContext(drone.getOnPos().getCenter(), drone.getOnPos().getCenter().add(0, -2, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, drone);
            BlockHitResult result = drone.level().clip(context);

            drone.dropCarrying(result.getBlockPos().above());
            return MethodResult.of(true, "Dropped Block!");
        } else {
            return MethodResult.of(false, "Carry Upgrade not installed!");
        }
    };

    ILuaFunction pickUpEntity = args -> {
        if(drone.hasUpgrade("ccx_drones:carry_upgrade")) {
            List<Entity> targets = drone.level().getEntitiesOfClass(Entity.class,new AABB(drone.getOnPos().offset(-2,-2,-2),drone.getOnPos().offset(2,0,2)));
            if(!targets.isEmpty()) {
                targets.get(drone.getRandom().nextInt(targets.size())).startRiding(drone);
                return MethodResult.of(true, "Picked up Entity!");
            } else {
                return MethodResult.of(false, "No Entities Nearby!");
            }

        } else {
            return MethodResult.of(false, "Carry Upgrade Not Installed!");
        }
    };

    ILuaFunction dropEntity = args -> {
        if(drone.hasUpgrade("ccx_drones:carry_upgrade")) {
            if(!drone.getPassengers().isEmpty()) {
                drone.ejectPassengers();
                return MethodResult.of(true,"Ejected Passenger!");
            } else {
                return MethodResult.of(false, "No Passengers!");
            }
        } else {
            return MethodResult.of(false, "Carry Upgrade Not Installed!");
        }
    };

    public Map<String, Object> getMethods(Map<String, Object> info) {
        //info.put("pickupBlock", pickupBlock);
        //info.put("dropBlock", dropBlock);
        info.put("pickupEntity",pickUpEntity);
        info.put("dropEntity",dropEntity);
        //TODO: Fix entity pickup/drop
        return info;
    }
}
