package com.awesoft.ccx.item;

import com.awesoft.ccx.CCX;
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

public class RemoteTerminalItem extends Item {
    public RemoteTerminalItem(Properties properties) {
        super(properties);
    }

    private static int getSessionID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains("SessionId") ? nbt.getInt("SessionId") : -1;
    }

    @javax.annotation.Nullable
    public static UUID getInstanceID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.hasUUID("InstanceId") ? nbt.getUUID("InstanceId") : null;
    }

    @javax.annotation.Nullable
    public static ServerComputer getServerComputer(ServerComputerRegistry registry, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        try {
            int sessionId = tag.getInt("SessionId");
            UUID instanceId = tag.getUUID("InstanceId");
            if (instanceId != null) {
                return registry.get(sessionId, instanceId);
            }
        } catch (RuntimeException ignored) {}
        return null;
    }

    @javax.annotation.Nullable
    public static ServerComputer getServerComputer(MinecraftServer server, ItemStack stack) {
        try {ServerContext.get(server);} catch (Exception e) {return null;}
        if (server != null) {
            return getServerComputer(ServerContext.get(server).registry(), stack);
        } else {
            return null;
        }
    }

    static void openImpl(Player player, ItemStack stack, boolean isTypingOnly, ServerComputer computer) {
        PlatformHelper.get().openMenu(player, stack.getHoverName(), (id, inventory, entity) -> new ComputerMenuWithoutInventory((MenuType) ModRegistry.Menus.COMPUTER.get(), id, inventory, (p) -> true, computer), new ComputerContainerData(computer, stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack held = player.getItemInHand(interactionHand);
        CompoundTag tag = held.getTag();

        if (level.isClientSide || tag == null) return InteractionResultHolder.fail(held);

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

            if (result.getType() == HitResult.Type.BLOCK || held.getTag() == null || held.getTag().get("rackPos") == null) return InteractionResultHolder.fail(held);
            held.getTag().remove("rackPos");
            held.getTag().remove("slot");
            player.displayClientMessage(Component.literal("Unlinked rack!"),true);
            return InteractionResultHolder.success(held);
        }

        CompoundTag posTag = tag.getCompound("rackPos");
        BlockEntity blkEnt = level.getBlockEntity(new BlockPos(posTag.getInt("x"),posTag.getInt("y"),posTag.getInt("z")));
        if (blkEnt instanceof RackBlockEntity rackEnt) {
            int serverSlot = tag.getInt("slot") - 1;
            if (serverSlot < 0) serverSlot = 3;
            ItemStack serverItem = rackEnt.getInventory().getItem(serverSlot);
            if (!serverItem.is(CCXItems.SERVER_ADVANCED.get()) && !serverItem.is(CCXItems.SERVER_COMMAND.get())) {
                player.displayClientMessage(Component.literal("No server above Remote Server!"), true);
                return InteractionResultHolder.fail(held);
            }
            ServerComputer comp = getServerComputer((MinecraftServer) level.getServer(),serverItem);
            String label = comp.getLabel();
            if (label != null) held.setHoverName(Component.literal(label));
            openImpl(player,serverItem,false,comp);
            return InteractionResultHolder.success(held);
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
