package com.awesoft.ccx_drones.item;

import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class CrowbarItem extends SwordItem {
    public CrowbarItem() {
        super(Tiers.IRON, 3, -2.4F, new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if(livingEntity instanceof DroneEntity drone && !player.level().isClientSide && player.isCrouching()) {
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof DroneEntity drone) {
            if (attacker instanceof Player player) {
                DamageSource dmgSource = attacker.level().damageSources().playerAttack(player);
                drone.hurt(dmgSource,drone.getMaxHealth());
            }
            return true;
        } else {
            return super.hurtEnemy(itemStack,target,attacker);
        }
    }
}

