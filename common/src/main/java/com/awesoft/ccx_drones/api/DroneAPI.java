package com.awesoft.ccx_drones.api;

import com.awesoft.ccx_drones.entity.drone.DroneEntity;
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
        if(drone.hasUpgrade("ccx_drones:mine_upgrade")) {
            info.put("breakForward", breakForward);
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

        if(drone.hasUpgrade("ccx_drones:carry_upgrade")) {
            info.put("pickupBlock", pickupBlock);
            info.put("dropBlock", dropBlock);
            info.put("pickupEntity",pickUpEntity);
            info.put("dropEntity",dropEntity);
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

        if (drone.hasUpgrade("ccx_drones:modem_upgrade")) {
            info.put("getPos",getPos);
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

        if (drone.hasUpgrade("ccx_drones:survey_upgrade")) {
            info.put("raycast",raycast);
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
