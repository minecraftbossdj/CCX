package com.awesoft.ccx_pocket.networking.hmd;

import dev.architectury.networking.NetworkManager;

public class OpenHMDNetwork {
    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                OpenHMDPacket.ID,
                (buf, context) -> {
                    OpenHMDPacket pkt = OpenHMDPacket.decode(buf);

                    context.queue(()->{
                        OpenHMDPacketHandler.handle(pkt, context.getPlayer());
                    });
                }
        );
    }
}
