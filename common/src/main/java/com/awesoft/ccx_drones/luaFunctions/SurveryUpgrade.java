package com.awesoft.ccx_drones.luaFunctions;

import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class SurveryUpgrade {
    private DroneEntity drone;

    public SurveryUpgrade(DroneEntity entity) {
        drone = entity;
    }

    ILuaFunction raycast = args -> {
        Vec3 start = drone.getEyePosition(0f);

        Vec3 end = start.add(drone.getForward().scale(5));

        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, drone);

        BlockHitResult result = drone.level().clip(context);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            Block block = drone.level().getBlockState(pos).getBlock();

            Map<String, Object> blockinfo = new HashMap<>();
            blockinfo.put("id",(BuiltInRegistries.BLOCK.getKey(block)).toString());
            blockinfo.put("distance",start.distanceTo(result.getLocation()));

            return MethodResult.of(true,blockinfo);
        } else {
            return MethodResult.of(false, "Raycast hit non block!");
        }
    };

    public Map<String, Object> getMethods(Map<String, Object> info) {
        info.put("raycast",raycast);
        return info;
    }
}
