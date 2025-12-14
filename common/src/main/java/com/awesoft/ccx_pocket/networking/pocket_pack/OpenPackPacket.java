package com.awesoft.ccx_pocket.networking.pocket_pack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class OpenPackPacket {
    public static final ResourceLocation ID = new ResourceLocation("ccx_pocket", "open_pack");;

    public static OpenPackPacket decode(FriendlyByteBuf buf) {
        return new OpenPackPacket();
    }

    public void encode(FriendlyByteBuf buf) {}
}
