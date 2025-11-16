package com.awesoft.ccx.item.pciecards;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ChestCardItem extends BaseCardItem {

    public ChestCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        if (useOnContext.getPlayer() == null) return InteractionResult.FAIL;
        if (useOnContext.getPlayer().isCrouching()) {
            CompoundTag posTag = new CompoundTag();
            BlockPos pos = useOnContext.getClickedPos();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());

            useOnContext.getItemInHand().getOrCreateTag().put("invPos", posTag);

            useOnContext.getPlayer().displayClientMessage(Component.literal("Set inventory pos!"),true);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide) return InteractionResultHolder.fail(stack);
        if (player.isCrouching()) {
            Vec3 start = player.getEyePosition(1.0f);
            Vec3 look = player.getLookAngle();
            Vec3 end = start.add(look.scale(5));

            BlockHitResult result = level.clip(new ClipContext(
                    start,
                    end,
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    player
            ));

            if (result.getType() == HitResult.Type.BLOCK || stack.getTag() == null || stack.getTag().get("invPos") == null) return InteractionResultHolder.fail(stack);
            stack.getTag().remove("invPos");
            player.displayClientMessage(Component.literal("Cleared inventory pos!"),true);
        }
        return InteractionResultHolder.fail(player.getItemInHand(interactionHand));
    }

}
