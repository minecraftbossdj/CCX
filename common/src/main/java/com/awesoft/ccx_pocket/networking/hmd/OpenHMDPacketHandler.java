package com.awesoft.ccx_pocket.networking.hmd;

import com.awesoft.ccx_pocket.item.hmd.HMDHolder;
import com.awesoft.ccx_pocket.item.hmd.HMDPocketItem;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class OpenHMDPacketHandler {
    public static void handle(OpenHMDPacket pkt, Player player) {
        ServerPlayer plr = (ServerPlayer) player;
        if (plr == null) return;
        ItemStack helm = plr.getItemBySlot(EquipmentSlot.HEAD);
        if (helm.isEmpty()) return;
        if (helm.getItem() instanceof HMDPocketItem) {
            ServerComputer comp = HMDPocketItem.getServerComputer(player.getServer(),helm);
            if (comp == null) return;
            openImpl(player, helm, new HMDHolder.HelmetHolder(plr),false, comp);
            if (!comp.isOn()) comp.turnOn();
        }
    }

    private static void openImpl(Player player, ItemStack stack, HMDHolder holder, boolean isTypingOnly, ServerComputer computer) {
        PlatformHelper.get().openMenu(player, stack.getHoverName(), (id, inventory, entity) -> new ComputerMenuWithoutInventory(isTypingOnly ? (MenuType) ModRegistry.Menus.POCKET_COMPUTER_NO_TERM.get() : (MenuType) ModRegistry.Menus.COMPUTER.get(), id, inventory, (p) -> holder.isValid(computer), computer), new ComputerContainerData(computer, stack));
    }
}
