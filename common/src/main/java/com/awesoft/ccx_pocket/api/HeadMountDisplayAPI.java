package com.awesoft.ccx_pocket.api;

import dan200.computercraft.api.component.ComputerComponents;
import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HeadMountDisplayAPI implements ILuaAPI {
    private final IComputerSystem computer;
    private final IPocketAccess pocket;

    public HeadMountDisplayAPI(IComputerSystem computer, IPocketAccess pocket) {
        this.computer = computer;
        this.pocket = pocket;
    }

    public final Level getLevel() {
        return computer.getLevel();
    }

    public final ServerPlayer getPlayer() {
        if (pocket.getEntity() instanceof ServerPlayer player) {
            return player;
        } else {
            return null;
        }
    }

    public final Boolean isPlayerAlive() {
        return getPlayer() != null;
    }

    public final UUID getOwnerUUID() {
        if (getPlayer() != null) {
            return getPlayer().getUUID();
        }
        return null;
    }

    @Override
    public String[] getNames() {
        return new String[]{"hmd"};
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getInfo() {
        if (!isPlayerAlive()) return null;
        Map<String, Object> info = new HashMap<>();

        Player plr = getPlayer();
        if (plr != null) {
            info.put("name", plr.getGameProfile().getName());
            info.put("dimension", plr.level().dimension().location().toString());
            info.put("health", plr.getHealth());
            info.put("hunger", plr.getFoodData().getFoodLevel());
            info.put("saturation", plr.getFoodData().getSaturationLevel());
            info.put("air", plr.getAirSupply());
            info.put("armor", plr.getArmorValue());
            info.put("speed", plr.getSpeed());
            info.put("yaw", plr.getViewYRot(1));
            info.put("pitch", plr.getViewXRot(1));
            info.put("isInPowderedSnow", plr.isInPowderSnow);
            info.put("isInWater", plr.isInWater());
            info.put("isHungry", plr.getFoodData().needsFood());
        }
        return info;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> raycastBlock(double inputReach, boolean hitFluids) {
        if (!isPlayerAlive()) return null;

        double reach = Math.min(5,Math.max(1,inputReach));

        ClipContext.Fluid pFluidMode = ClipContext.Fluid.ANY;
        Player plr = getPlayer();
        Map<String, Object> info = new HashMap<>();
        if (plr != null) {
            Level lvl = plr.level();
            HitResult hitResult = plr.pick(5.0D, 0.0F, hitFluids);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hitResult;
                BlockPos pos = blockHit.getBlockPos();

                String name = String.valueOf(lvl.getBlockState(pos).getBlock().getName().toString());
                Block blk = lvl.getBlockState(pos).getBlock();

                info.put("id", BuiltInRegistries.BLOCK.getKey(blk).toString());
                info.put("name", new ItemStack(blk).getDisplayName().getString());
                info.put("x", pos.getX());
                info.put("y", pos.getY());
                info.put("z", pos.getZ());
            }
        }
        return info;
    }

    @Nullable
    @LuaFunction(mainThread = true)
    public final Map<String, Object> raycastEntity(double inputReach) {
        if (!isPlayerAlive()) return null;

        double reach = Math.min(5,Math.max(1,inputReach));

        Map<String, Object> info = new HashMap<>();
        Player plr = getPlayer();
        if (plr != null) {
            Optional<Entity> hitEntity = raycastEntity(plr, reach);


            if (hitEntity.isPresent()) {
                Entity entity = hitEntity.get();


                info.put("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
                info.put("uuid", entity.getUUID().toString());
                info.put("dimension", entity.level().dimension().location().toString());
                info.put("x", Math.floor(entity.getX()));
                info.put("y", Math.floor(entity.getY()));
                info.put("z", Math.floor(entity.getZ()));
                info.put("yaw", entity.getViewYRot(1));
                info.put("pitch", entity.getViewXRot(1));
                if (entity instanceof LivingEntity livingEntity) {
                    info.put("health", livingEntity.getHealth());
                    info.put("maxHealth", livingEntity.getMaxHealth());
                    info.put("air", livingEntity.getAirSupply());
                    info.put("maxAir", livingEntity.getMaxAirSupply());
                    info.put("armor", livingEntity.getArmorValue());
                    info.put("speed", livingEntity.getSpeed());
                    info.put("isInPowderedSnow", livingEntity.isInPowderSnow);

                    if (livingEntity instanceof Player hitPlayer) {
                        info.put("hunger", hitPlayer.getFoodData().getFoodLevel());
                        info.put("saturation", hitPlayer.getFoodData().getSaturationLevel());
                        info.put("isHungry", hitPlayer.getFoodData().needsFood());
                        info.put("creative", hitPlayer.isCreative());
                        info.put("spectator", hitPlayer.isSpectator());
                    }
                }
                if (entity instanceof Player hitPlayer) {
                    info.put("name", hitPlayer.getGameProfile().getName().toString());
                } else {
                    info.put("name", entity.getDisplayName().getString());
                }
                return info;
            } else {
                return info;
            }
        } else {
            return info;
        }
    }

    public static Optional<Entity> raycastEntity(Player player, double distance) {
        Level world = player.level();
        Vec3 startVec = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F).scale(distance);
        Vec3 endVec = startVec.add(lookVec);


        BlockHitResult blockHitResult = world.clip(new ClipContext(
                startVec,
                endVec,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));


        double blockHitDistance = blockHitResult.getLocation().distanceTo(startVec);
        if (blockHitDistance < distance) {
            endVec = blockHitResult.getLocation();
        }


        AABB boundingBox = new AABB(startVec, endVec).inflate(1.0D);
        Entity closestEntity = null;
        double closestDistance = distance;

        for (Entity entity : world.getEntities(player, boundingBox)) {
            if (entity == player || !entity.isPickable()) continue;
            AABB entityBoundingBox = entity.getBoundingBox().inflate(0.3D);
            Optional<Vec3> intersection = entityBoundingBox.clip(startVec, endVec);

            if (intersection.isPresent()) {
                double entityDistance = startVec.distanceTo(intersection.get());
                if (entityDistance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = entityDistance;
                }
            }
        }

        return Optional.ofNullable(closestEntity);
    }
}
