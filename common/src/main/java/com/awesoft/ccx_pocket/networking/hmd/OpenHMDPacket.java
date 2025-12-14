package com.awesoft.ccx_pocket.networking.hmd;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class OpenHMDPacket {
    public static final ResourceLocation ID = new ResourceLocation("ccx_pocket", "open_hmd");;

    public static OpenHMDPacket decode(FriendlyByteBuf buf) {
        return new OpenHMDPacket();
    }

    public void encode(FriendlyByteBuf buf) {}
}
