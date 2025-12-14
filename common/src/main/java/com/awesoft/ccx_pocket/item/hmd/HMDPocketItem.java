package com.awesoft.ccx_pocket.item.hmd;


import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.impl.PocketUpgrades;
import dan200.computercraft.shared.ModRegistry.Menus;
import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.computer.items.IComputerItem;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.pocket.core.PocketHolder;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.NBTUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HMDPocketItem extends ArmorItem implements IComputerItem, IMedia, IColouredItem {
    private static final String NBT_UPGRADE = "Upgrade";
    private static final String NBT_UPGRADE_INFO = "UpgradeInfo";
    public static final String NBT_ON = "On";
    private static final String NBT_INSTANCE = "InstanceId";
    private static final String NBT_SESSION = "SessionId";
    private final ComputerFamily family;

    public HMDPocketItem(Properties settings, ComputerFamily family) {
        super(ArmorMaterials.IRON, Type.HELMET, settings);
        this.family = family;
    }

    public ItemStack create(int id, @Nullable String label, int colour, @Nullable UpgradeData<IPocketUpgrade> upgrade) {
        ItemStack result = new ItemStack(this);
        if (id >= 0) {
            result.getOrCreateTag().putInt("ComputerId", id);
        }

        if (label != null) {
            result.setHoverName(Component.literal(label));
        }

        if (upgrade != null) {
            result.getOrCreateTag().putString("Upgrade", ((IPocketUpgrade)upgrade.upgrade()).getUpgradeID().toString());
            if (!upgrade.data().isEmpty()) {
                result.getOrCreateTag().put("UpgradeInfo", upgrade.data().copy());
            }
        }

        if (colour != -1) {
            result.getOrCreateTag().putInt("Color", colour);
        }

        return result;
    }

    public void tick(ItemStack stack, HMDHolder holder, boolean passive) {
        HMDBrain brain;
        if (passive) {
            HMDServerComputer computer = getServerComputer(holder.level().getServer(), stack);
            if (computer == null) {
                return;
            }

            brain = computer.getBrain();
        } else {
            brain = this.getOrCreateBrain(holder.level(), holder, stack);
            brain.computer().keepAlive();
        }

        UpgradeData<IPocketUpgrade> upgrade = brain.getUpgrade();
        if (upgrade != null) {
            ((IPocketUpgrade)upgrade.upgrade()).update(brain, brain.computer().getPeripheral(ComputerSide.BACK));
        }

        if (this.updateItem(stack, brain)) {
            holder.setChanged();
        }

    }

    private boolean updateItem(ItemStack stack, HMDBrain brain) {
        boolean changed = brain.updateItem(stack);
        HMDServerComputer computer = brain.computer();
        int id = computer.getID();
        if (id != this.getComputerID(stack)) {
            changed = true;
            setComputerID(stack, id);
        }

        String label = computer.getLabel();
        if (!Objects.equals(label, this.getLabel(stack))) {
            changed = true;
            this.setLabel(stack, label);
        }

        boolean on = computer.isOn();
        if (on != isMarkedOn(stack)) {
            changed = true;
            stack.getOrCreateTag().putBoolean("On", on);
        }

        return changed;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int compartmentSlot, boolean selected) {
        if (!world.isClientSide && entity instanceof ServerPlayer player) {
            int slot = InventoryUtil.getInventorySlotFromCompartment(player, compartmentSlot, stack);
            if (player.getItemBySlot(EquipmentSlot.HEAD) == stack) {
                this.tick(stack, new HMDHolder.HelmetHolder(player), false);
            }
            if (slot >= 0) {
                this.tick(stack, new HMDHolder.PlayerHolder(player, slot), false);
            }
        }
    }

    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Level level = entity.level();
        if (!level.isClientSide && level.getServer() != null) {
            this.tick(stack, new HMDHolder.ItemEntityHolder(entity), true);
            return false;
        } else {
            return false;
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        var stack = player.getItemInHand(hand);/*
        if (!world.isClientSide) {
            var holder = new HMDHolder.PlayerHolder((ServerPlayer) player, InventoryUtil.getHandSlot(player, hand));
            var brain = getOrCreateBrain((ServerLevel) world, holder, stack);
            var computer = brain.computer();
            computer.turnOn();

            var stop = false;
            var upgrade = getUpgrade(stack);
            if (upgrade != null) {
                stop = upgrade.onRightClick(world, brain, computer.getPeripheral(ComputerSide.BACK));
                updateItem(stack, brain);
            }

            if (!stop) openImpl(player, stack, holder, hand == InteractionHand.OFF_HAND, computer);
        }*/
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(world.isClientSide), stack);
    }

    public void open(Player player, ItemStack stack, HMDHolder holder, boolean isTypingOnly) {
        HMDBrain brain = this.getOrCreateBrain(holder.level(), holder, stack);
        HMDServerComputer computer = brain.computer();
        computer.turnOn();
        openImpl(player, stack, holder, isTypingOnly, computer);
    }

    private static void openImpl(Player player, ItemStack stack, HMDHolder holder, boolean isTypingOnly, ServerComputer computer) {
        PlatformHelper.get().openMenu(player, stack.getHoverName(), (id, inventory, entity) -> new ComputerMenuWithoutInventory(isTypingOnly ? (MenuType)Menus.POCKET_COMPUTER_NO_TERM.get() : (MenuType)Menus.COMPUTER.get(), id, inventory, (p) -> holder.isValid(computer), computer), new ComputerContainerData(computer, stack));
    }

    public Component getName(ItemStack stack) {
        String baseString = this.getDescriptionId(stack);
        IPocketUpgrade upgrade = getUpgrade(stack);
        return (Component)(upgrade != null ? Component.translatable(baseString + ".upgraded", new Object[]{Component.translatable(upgrade.getUnlocalisedAdjective())}) : super.getName(stack));
    }

    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        if (flag.isAdvanced() || this.getLabel(stack) == null) {
            int id = this.getComputerID(stack);
            if (id >= 0) {
                list.add(Component.translatable("gui.computercraft.tooltip.computer_id", new Object[]{id}).withStyle(ChatFormatting.GRAY));
            }
        }

    }

    @Nullable
    public String getCreatorModId(ItemStack stack) {
        IPocketUpgrade upgrade = getUpgrade(stack);
        if (upgrade != null) {
            String mod = PocketUpgrades.instance().getOwner(upgrade);
            if (mod != null && !mod.equals("computercraft")) {
                return mod;
            }
        }

        return "computercraft";
    }

    public HMDBrain getOrCreateBrain(ServerLevel level, HMDHolder holder, ItemStack stack) {
        try {ServerContext.get(level.getServer());} catch (Exception e) {return null;}
        ServerComputerRegistry registry = ServerContext.get(level.getServer()).registry();
        HMDServerComputer computer = getServerComputer(registry, stack);
        if (computer != null) {
            HMDBrain brain = computer.getBrain();
            brain.updateHolder(holder);
            return brain;
        } else {
            int computerID = this.getComputerID(stack);
            if (computerID < 0) {
                computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(level.getServer(), "computer");
                setComputerID(stack, computerID);
            }

            HMDBrain brain = new HMDBrain(holder, getUpgradeWithData(stack), ServerComputer.properties(this.getComputerID(stack), this.getFamily()).label(this.getLabel(stack)));
            HMDServerComputer computer2 = brain.computer();
            CompoundTag tag = stack.getOrCreateTag();
            tag.putInt("SessionId", registry.getSessionID());
            tag.putUUID("InstanceId", computer2.register());
            if (holder instanceof HMDHolder.PlayerHolder && isMarkedOn(stack)) {
                computer2.shutdown();
            } else if (isMarkedOn(stack)) {
                computer2.turnOn();
            }

            this.updateItem(stack, brain);
            holder.setChanged();

            return brain;
        }
    }

    public static boolean isServerComputer(ServerComputer computer, ItemStack stack) {
        return stack.getItem() instanceof HMDPocketItem && getServerComputer(computer.getLevel().getServer(), stack) == computer;
    }

    @Nullable
    public static HMDServerComputer getServerComputer(ServerComputerRegistry registry, ItemStack stack) {
        return (HMDServerComputer)registry.get(getSessionID(stack), getInstanceID(stack));
    }

    @Nullable
    public static HMDServerComputer getServerComputer(MinecraftServer server, ItemStack stack) {
        try {ServerContext.get(server);} catch (Exception e) {
            return null;
        }
        return getServerComputer(ServerContext.get(server).registry(), stack);
    }

    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            MinecraftServer server = level.getServer();
            if (server != null) {
                HMDServerComputer computer = getServerComputer(server, stack);
                if (computer != null) {
                    computer.getBrain().setUpgrade(getUpgradeWithData(stack));
                }
            }

        }
    }

    private static void setComputerID(ItemStack stack, int computerID) {
        stack.getOrCreateTag().putInt("ComputerId", computerID);
    }

    @Nullable
    public String getLabel(ItemStack stack) {
        return IComputerItem.super.getLabel(stack);
    }

    public ComputerFamily getFamily() {
        return this.family;
    }

    public ItemStack changeItem(ItemStack stack, Item newItem) {
        ItemStack var10000;
        if (newItem instanceof HMDPocketItem pocket) {
            var10000 = pocket.create(this.getComputerID(stack), this.getLabel(stack), this.getColour(stack), getUpgradeWithData(stack));
        } else {
            var10000 = ItemStack.EMPTY;
        }

        return var10000;
    }

    public boolean setLabel(ItemStack stack, @Nullable String label) {
        if (label != null) {
            stack.setHoverName(Component.literal(label));
        } else {
            stack.resetHoverName();
        }

        return true;
    }

    @Nullable
    public Mount createDataMount(ItemStack stack, ServerLevel level) {
        int id = this.getComputerID(stack);
        return id >= 0 ? ComputerCraftAPI.createSaveDirMount(level.getServer(), "computer/" + id, (long)Config.computerSpaceLimit) : null;
    }

    @Nullable
    public static UUID getInstanceID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.hasUUID("InstanceId") ? nbt.getUUID("InstanceId") : null;
    }

    private static int getSessionID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains("SessionId") ? nbt.getInt("SessionId") : -1;
    }

    private static boolean isMarkedOn(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.getBoolean("On");
    }

    @Nullable
    public static IPocketUpgrade getUpgrade(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        return compound != null && compound.contains("Upgrade") ? (IPocketUpgrade)PocketUpgrades.instance().get(compound.getString("Upgrade")) : null;
    }

    @Nullable
    public static UpgradeData<IPocketUpgrade> getUpgradeWithData(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound != null && compound.contains("Upgrade")) {
            IPocketUpgrade upgrade = (IPocketUpgrade)PocketUpgrades.instance().get(compound.getString("Upgrade"));
            return upgrade == null ? null : UpgradeData.of(upgrade, NBTUtil.getCompoundOrEmpty(compound, "UpgradeInfo"));
        } else {
            return null;
        }
    }

    public static void setUpgrade(ItemStack stack, @Nullable UpgradeData<IPocketUpgrade> upgrade) {
        CompoundTag compound = stack.getOrCreateTag();
        if (upgrade == null) {
            compound.remove("Upgrade");
            compound.remove("UpgradeInfo");
        } else {
            compound.putString("Upgrade", ((IPocketUpgrade)upgrade.upgrade()).getUpgradeID().toString());
            compound.put("UpgradeInfo", upgrade.data().copy());
        }

    }

    public static CompoundTag getUpgradeInfo(ItemStack stack) {
        return stack.getOrCreateTagElement("UpgradeInfo");
    }
}

