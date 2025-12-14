package com.awesoft.ccx.item;

import com.awesoft.ccx.block.rack.RackBlockEntity;
import com.awesoft.ccx.registry.CCXItems;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class AdvancedRemoteTerminalItem extends RemoteTerminalItem {
    public AdvancedRemoteTerminalItem(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack held = player.getItemInHand(interactionHand);
        CompoundTag tag = held.getTag();
        if (level.isClientSide || tag == null) return InteractionResultHolder.fail(held);

        if (tag.get("rackNum") == null) tag.putInt("rackNum", 1);

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

            //fuicking stupid fuckinp iece of shit code i hate his grrrrrrrrr just kidding idk i just wanted to comment im weird let me be ok
            if (!(result.getType() == HitResult.Type.BLOCK)) {
               int rackNum = tag.getInt("rackNum");
               rackNum += 1;
               if (rackNum > 4) rackNum = 1;
               player.displayClientMessage(Component.literal("Selected rack: ").append(String.valueOf(rackNum)), true);
               tag.putInt("rackNum",rackNum);
               if (tag.getCompound("rack_"+rackNum) != null && tag.getCompound("rack_"+rackNum).get("label") != null) {
                   held.setHoverName(Component.literal(tag.getCompound("rack_"+rackNum).getString("label")));
               } else held.resetHoverName();
            } else {
                int rackNum = tag.getInt("rackNum");
                CompoundTag rackTag = tag.getCompound("rack_"+rackNum);
                if (rackTag == null || rackTag.get("rackPos") == null) return InteractionResultHolder.success(held);
                rackTag.remove("rackPos");
                rackTag.remove("slot");
                rackTag.remove("label");
                player.displayClientMessage(Component.literal("Unlinked rack!"),true);
            }
            return InteractionResultHolder.success(held);
        } else {
            int rackNum = tag.getInt("rackNum");
            CompoundTag rackTag = tag.getCompound("rack_"+rackNum);
            if (rackTag == null || rackTag.getCompound("rackPos") == null || rackTag.getCompound("rackPos").get("x") == null) return InteractionResultHolder.fail(held);

            CompoundTag posTag = rackTag.getCompound("rackPos");
            BlockEntity blkEnt = level.getBlockEntity(new BlockPos(posTag.getInt("x"),posTag.getInt("y"),posTag.getInt("z")));
            if (blkEnt instanceof RackBlockEntity rackEnt) {
                int serverSlot = rackTag.getInt("slot") - 1;
                if (serverSlot < 0) serverSlot = 3;
                ItemStack serverItem = rackEnt.getInventory().getItem(serverSlot);
                if (!serverItem.is(CCXItems.SERVER_ADVANCED.get()) && !serverItem.is(CCXItems.SERVER_COMMAND.get())) {
                    player.displayClientMessage(Component.literal("No server above Remote Server!"), true);
                    return InteractionResultHolder.fail(held);
                }
                ServerComputer comp = getServerComputer((MinecraftServer) level.getServer(),serverItem);
                String label = comp.getLabel();

                if (label != null) {
                    rackTag.putString("label",label);
                    held.setHoverName(Component.literal(label));
                }

                openImpl(player,serverItem,false,comp);
                return InteractionResultHolder.success(held);
            }
        }
        return InteractionResultHolder.pass(held);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.getTag() == null || itemStack.getTag().get("rackPos") == null) return;
        CompoundTag pos = itemStack.getTag().getCompound("rackPos");
        String posString = ": "+pos.getInt("x")+", "+pos.getInt("y")+", "+pos.getInt("z");
        list.add(Component.translatable("item.tooltip.terminal_blockpos").append(posString));
    }

}
