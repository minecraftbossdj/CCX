package com.awesoft.ccx_drones.entity.drone;

import com.awesoft.ccx_drones.menu.DroneMenu;
import com.awesoft.ccx_drones.registry.CCXDComponents;
import com.awesoft.ccx_drones.registry.CCXDItems;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.container.SingleContainerData;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.util.IDAssigner;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

public class DroneEntity extends Mob {

    private Vec3 targetPos;

    DroneBrain brain = new DroneBrain(this); //TODO: port more stuff over to DroneBrain so its actually useful and not just for GUI

    //TODO: organize this shit AGAIN :sob:
    //TODO: do more testing with this

    public static final EntityDataAccessor<CompoundTag> EXTRA = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public static final EntityDataAccessor<CompoundTag> CLIENT_INVENTORY = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.COMPOUND_TAG);

    public DroneEntity(EntityType<DroneEntity> e, Level level) {
        super(e, level);

    }

    int INVENTORY_SIZE = 7;

    public int getInvSize() {
        return INVENTORY_SIZE;
    }

    private final DroneInventory inventory = new DroneInventory(INVENTORY_SIZE,this);

    public Container getInventory() {
        return inventory;
    }

    private boolean loaderActive = false;

    public void setTargetPos(Vec3 pos) {
        this.targetPos = pos;
    }

    public float propellerRotation;

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide) {
            if (!this.isDeadOrDying()) {
                ServerComputer computer = createOrUpkeepComputer();
                computer.keepAlive();


                if (targetPos != null) {
                    Vec3 targetCenter = new Vec3(targetPos.x+0.5,targetPos.y+0.5,targetPos.z+0.5);
                    Vec3 dir = targetCenter.subtract(position()).normalize();
                    double speed = 0.1;
                    setDeltaMovement(getDeltaMovement().scale(0.9).add(dir.scale(speed)));
                    this.lookAt(EntityAnchorArgument.Anchor.EYES,targetCenter);

                    if (position().closerThan(targetCenter, 0.5)) {
                        targetPos = null;
                        setDeltaMovement(Vec3.ZERO);

                        Object[] eventarg = new Object[1];
                        eventarg[0] = "drone_movement";
                        computer.queueEvent("task_complete", eventarg);
                    }
                }

            }
        }
        if (engineOn()) {
            setDeltaMovement(getForward().multiply(0.1,0.1,0.1));
        }
        propellerRotation = (propellerRotation + 25f) % 360f;
    }


    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!level().isClientSide) {
            if (held.isEmpty()) {
                ServerComputer computer = getServerComputer();
                if (computer == null) {return InteractionResult.FAIL;}
                PlatformHelper.get().openMenu(
                        player,
                        player.getItemInHand(hand).getHoverName(),
                        (id, inventory, entity) ->
                                new DroneMenu(
                                        id,
                                        (p) -> true,
                                        ComputerFamily.ADVANCED,
                                        computer,
                                        null,
                                        player.getInventory(),
                                        this.getInventory(),
                                        (SingleContainerData) brain::getSelectedSlot

                                ),
                        new ComputerContainerData(computer, ItemStack.EMPTY)
                );
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("extra",entityData.get(EXTRA));


        ListTag itemsList = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                inventory.getItem(i).save(itemTag);
                itemsList.add(itemTag);
            }
        }
        compoundTag.put("Inventory", itemsList);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        CompoundTag a = new CompoundTag();
        entityData.define(EXTRA,a.copy());
        entityData.define(CLIENT_INVENTORY, new CompoundTag());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        entityData.set(EXTRA,compoundTag.getCompound("extra"));

        ListTag itemsList = compoundTag.getList("Inventory", 10); // 10 = CompoundTag
        for (int i = 0; i < itemsList.size(); i++) {
            CompoundTag itemTag = itemsList.getCompound(i);
            int slot = itemTag.getByte("Slot");
            if (slot >= 0 && slot < inventory.getContainerSize()) {
                inventory.setItem(slot, ItemStack.of(itemTag));
            }
        }

        syncInventoryToClient();
    }

    public void setComputerID(int computerID) {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putInt("computerID",computerID);
        entityData.set(EXTRA,tag);

    }

    public void setCarrying(BlockPos pos) {
        CompoundTag tag = entityData.get(EXTRA);

        System.out.println("picking up!");
        BlockState state = level().getBlockState(pos);
        BlockEntity entity = level().getBlockEntity(pos);

        CompoundTag stateTag = NbtUtils.writeBlockState(state);
        tag.put("carryingState",stateTag);

        if(entity!=null)
        {
            CompoundTag entityTag = entity.saveWithFullMetadata();
            tag.put("carryingEntity",entityTag);
            Clearable.tryClear(entity);
        }


        entityData.set(EXTRA,tag,true);
        level().setBlock(pos, Blocks.AIR.defaultBlockState(),2);
    }

    public void dropCarrying(BlockPos pos)
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("carryingState"))
        {
            System.out.println("dropping!");
            BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(),tag.getCompound("carryingState"));
            level().setBlock(pos,state,2);
            if(tag.contains("carryingEntity"))
            {
                BlockEntity entity = BlockEntity.loadStatic(pos,state,tag.getCompound("carryingEntity"));
                level().setBlockEntity(entity);
                tag.remove("carryingEntity");
            }
            tag.remove("carryingState");
            entityData.set(EXTRA,tag,true);
        }
    }

    public boolean canPlayerUse(Player player) {
        return true;
    }

    public boolean isCarryingBlock()
    {
        CompoundTag tag = entityData.get(EXTRA);
        return tag.contains("carryingState");
    }

    public void setComputerUUID(UUID computerUUID) {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putUUID("computerUUID",computerUUID);
        entityData.set(EXTRA,tag);
    }

    public void syncInventoryToClient() {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                stack.save(itemTag);
                tag.put("Slot" + i, itemTag);
            }
        }
        entityData.set(CLIENT_INVENTORY, tag);
    }

    public ItemStack getSlotForRenderer(int slot) {
        if (!level().isClientSide) return ItemStack.EMPTY;
        CompoundTag tag = entityData.get(CLIENT_INVENTORY);
        if(tag.contains("Slot" + slot)) {
            return ItemStack.of(tag.getCompound("Slot" + slot));
        }
        return ItemStack.EMPTY;
    }


    public boolean hasUpgrade(String upgrade) {

        Item stringItem = BuiltInRegistries.ITEM.get(new ResourceLocation(upgrade));

        if (stringItem != null && stringItem != Items.AIR) {
            return inventory.hasAnyOf(Set.of(stringItem));
        }
        return false;

    }

    public void setEngineOn(boolean on)
    {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putBoolean("engineOn",on);
        entityData.set(EXTRA,tag);
    }
    public boolean engineOn()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("engineOn"))
        {
            return tag.getBoolean("engineOn");
        }
        return false;
    }

    public int getComputerID()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("computerID"))
        {
            return tag.getInt("computerID");
        }
        return -1;
    }
    public UUID getComputerUUID()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("computerUUID")) {
            return tag.getUUID("computerUUID");
        }
        return null;
    }
    public void setAllData(CompoundTag tag) {
        entityData.set(EXTRA,tag);
    }
    public CompoundTag getAllData() {
        return entityData.get(EXTRA);
    }



    public ServerComputer createOrUpkeepComputer() {
        ServerContext context = ServerContext.get(this.getServer());
        ServerComputer computer = context.registry().get(getComputerUUID());
        if (computer == null) {
            if (getComputerID() < 0) {
                System.out.println("defining ID");
                setComputerID(ComputerCraftAPI.createUniqueNumberedSaveDir(this.getServer(), IDAssigner.COMPUTER));
            }

            computer = new ServerComputer(
                    (ServerLevel) this.level(), this.getOnPos(), ServerComputer.properties(getComputerID(),ComputerFamily.ADVANCED)
                    .addComponent(CCXDComponents.DRONEAPI,this)
                    .terminalSize(Config.TURTLE_TERM_WIDTH,Config.TURTLE_TERM_HEIGHT)
            );

            System.out.println("Computer ID: "+computer.getID());
            setComputerUUID(computer.register());

            computer.turnOn();


        }

        return computer;

    }

    public ServerComputer getServerComputer() {
        ServerContext context = ServerContext.get(this.getServer());
        ServerComputer computer = context.registry().get(getComputerUUID());
        return computer;
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        super.dropAllDeathLoot(damageSource);

        ServerContext context = ServerContext.get(this.getServer());
        ServerComputer computer = context.registry().get(getComputerUUID());
        if (computer != null) {
            computer.close();
        }

        ItemStack stack = new ItemStack(CCXDItems.DRONE_ITEM.get());
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("extra",getAllData());
        stack.setTag(compoundTag);
        ItemEntity entity = new ItemEntity(level(),getX(),getY(),getZ(),stack);
        level().addFreshEntity(entity);

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack invItem = getInventory().getItem(i);

            if (!invItem.isEmpty()) {
                ItemEntity drop = new ItemEntity(
                        this.level(),
                        getX(),
                        getY(),
                        getZ(),
                        invItem
                );

                level().addFreshEntity(drop);
            }
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return -2;
    }
}
