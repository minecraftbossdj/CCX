package com.awesoft.ccx_drones.api;

import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import com.awesoft.ccx_drones.luaFunctions.CarryUpgrade;
import com.awesoft.ccx_drones.luaFunctions.MiningUpgrade;
import com.awesoft.ccx_drones.luaFunctions.ModemUpgrade;
import com.awesoft.ccx_drones.luaFunctions.SurveryUpgrade;
import dan200.computercraft.api.detail.BlockReference;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DroneAPI implements ILuaAPI {
    DroneEntity drone;
    public DroneAPI(DroneEntity entity)
    {
        this.drone=entity;
    }

    @Override
    public String[] getNames() {
        return new String[] {"drone"};
    }

    @LuaFunction
    public final MethodResult engineOn(boolean on)
    {
        drone.setEngineOn(on);
        if(!on)
        {
            drone.setDeltaMovement(Vec3.ZERO);
        }
        if (on) {
            return MethodResult.of(true, "Turned On Engine!");
        } else {
            return MethodResult.of(true, "Turned Off Engine"); //true since it suceeded turning off
        }
    }
    @LuaFunction
    public final MethodResult hoverOn(boolean on)
    {
        drone.setNoGravity(on);
        if (on) {return MethodResult.of(true, "Hover on!");} else {return MethodResult.of(true, "Hover Off!");}
    }
    @LuaFunction
    public final void right(int deg) {
        drone.turn(deg,0);
    }
    @LuaFunction
    public final void left(int deg) {right(-deg);}

    @LuaFunction
    public final boolean isColliding() {return drone.horizontalCollision;}

    @LuaFunction
    public final void up(int amount)
    {
        drone.addDeltaMovement(Vec3.ZERO.add(0,amount/10D,0));
    }
    @LuaFunction
    public final void down(int amount) {up(-amount);}
    @LuaFunction
    public final MethodResult lookForward()
    {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(3,3,3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        //System.out.println(result.getBlockPos()+" "+drone.level().getBlockState(result.getBlockPos()));
        return MethodResult.of(VanillaDetailRegistries.BLOCK_IN_WORLD.getDetails(new BlockReference(drone.level(),result.getBlockPos())));
    }
    @LuaFunction
    public final float rotation() {return drone.yRotO;}
    @LuaFunction
    public final MethodResult lookBack()
    {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(-3,-3,-3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        return MethodResult.of(VanillaDetailRegistries.BLOCK_IN_WORLD.getDetails(new BlockReference(drone.level(),result.getBlockPos())));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult moveTo(int x, int y, int z) {
        double newPosX = drone.getOnPos().getX() + x;
        double newPosY = drone.getOnPos().getY() + y;
        double newPosZ = drone.getOnPos().getZ() + z;
        drone.setTargetPos(new Vec3(newPosX,newPosY,newPosZ));
        return MethodResult.of(true,"Moving to new pos!");
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getUpgradesFunctions() {
        Map<String, Object> info = new HashMap<>();

        if(drone.hasUpgrade("ccx_drones:mine_upgrade")) {
            info = new MiningUpgrade(drone).getMethods(info);
        }

        if(drone.hasUpgrade("ccx_drones:carry_upgrade")) {
            info = new CarryUpgrade(drone).getMethods(info);
        }

        if (drone.hasUpgrade("ccx_drones:modem_upgrade")) {
            info = new ModemUpgrade(drone).getMethods(info);
        }

        if (drone.hasUpgrade("ccx_drones:survey_upgrade")) {
            info = new SurveryUpgrade(drone).getMethods(info);
        }

        return info;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listUpgrades() {
        ArrayList<String> result = new ArrayList<>();
        var size = drone.getInventory().getContainerSize();
        for (var i = 0; i < size; i++) {
            var stack = drone.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                result.add(id.toString());
            }
        }
        return MethodResult.of(true, result);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult hasUpgrade(String upgradeid) {
        return MethodResult.of(drone.hasUpgrade(upgradeid));
    }
}
