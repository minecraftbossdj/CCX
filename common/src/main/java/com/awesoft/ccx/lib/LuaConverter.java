package com.awesoft.ccx.lib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class LuaConverter {//this class IS named after advanced peripheral's lib however only ONE function is theirs, being nbtToLua


    public static @Nullable Object nbtToLua(@Nullable Tag tag) {
        if (tag == null) return null;

        switch (tag.getId()) {
            case Tag.TAG_BYTE:
            case Tag.TAG_SHORT:
            case Tag.TAG_INT:
            case Tag.TAG_LONG:
                return ((NumericTag) tag).getAsLong();
            case Tag.TAG_FLOAT:
            case Tag.TAG_DOUBLE:
                return ((NumericTag) tag).getAsDouble();
            case Tag.TAG_STRING: // String
                return tag.getAsString();
            case Tag.TAG_COMPOUND: { // Compound
                var compound = (CompoundTag) tag;
                Map<String, Object> map = new HashMap<>(compound.size());
                for (var key : compound.getAllKeys()) {
                    var value = nbtToLua(compound.get(key));
                    if (value != null) map.put(key, value);
                }
                return map;
            }
            case Tag.TAG_LIST: {
                var list = (ListTag) tag;
                Map<Integer, Object> map = new HashMap<>(list.size());
                for (var i = 0; i < list.size(); i++) map.put(i, nbtToLua(list.get(i)));
                return map;
            }
            case Tag.TAG_BYTE_ARRAY: {
                var array = ((ByteArrayTag) tag).getAsByteArray();
                Map<Integer, Byte> map = new HashMap<>(array.length);
                for (var i = 0; i < array.length; i++) map.put(i + 1, array[i]);
                return map;
            }
            case Tag.TAG_INT_ARRAY: {
                var array = ((IntArrayTag) tag).getAsIntArray();
                Map<Integer, Integer> map = new HashMap<>(array.length);
                for (var i = 0; i < array.length; i++) map.put(i + 1, array[i]);
                return map;
            }

            default:
                return null;
        }
    }

    public static Map<String, Object> itemToLua(int invSlot, Container inv) {
        Map<String, Object> itemInfo = new HashMap<>();

        ItemStack item = inv.getItem(invSlot);

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.getItem());
        if (item.isEmpty()) return null;
        itemInfo.put("displayName", item.getDisplayName().getString());
        itemInfo.put("name", id.toString());
        itemInfo.put("count", item.getCount());
        itemInfo.put("maxStackSize", item.getMaxStackSize());
        itemInfo.put("nbt", nbtToLua(item.copy().getOrCreateTag()));
        if (item.isDamageableItem()) {
            itemInfo.put("damage", item.getDamageValue());
            itemInfo.put("maxDamage", item.getMaxDamage());
        }

        return itemInfo;
    }
}
