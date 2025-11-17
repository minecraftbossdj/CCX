package com.awesoft.ccx_drones.item;

import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import com.awesoft.ccx_drones.registry.CCXDEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DroneItem extends Item {
    public DroneItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        DroneEntity entity = new DroneEntity(CCXDEntities.DRONE_ENTITY.get(), useOnContext.getLevel());
        entity.setPos(useOnContext.getClickLocation().x,useOnContext.getClickLocation().y+1,useOnContext.getClickLocation().z);
        if(useOnContext.getItemInHand().hasTag() && useOnContext.getItemInHand().getItem() instanceof DroneItem)
        {
            CompoundTag extra = useOnContext.getItemInHand().getTag().getCompound("extra");
            entity.setAllData(extra);
        }
        useOnContext.getLevel().addFreshEntity(entity);
        useOnContext.getItemInHand().grow(-1);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        if(itemStack.hasTag() && itemStack.getItem() instanceof DroneItem) {
            CompoundTag tag = itemStack.getTag().getCompound("extra");
            list.add(Component.literal("Computer ID: "+tag.getInt("computerID")));
        }
    }
}

