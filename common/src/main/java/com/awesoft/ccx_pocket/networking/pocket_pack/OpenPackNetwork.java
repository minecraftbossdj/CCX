package com.awesoft.ccx_pocket.networking.pocket_pack;

import dev.architectury.networking.NetworkManager;

public class OpenPackNetwork {
    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                OpenPackPacket.ID,
                (buf, context) -> {
                    OpenPackPacket pkt = OpenPackPacket.decode(buf);

                    context.queue(()->{
                        OpenPackPacketHandler.handle(pkt, context.getPlayer());
                    });
                }
        );
    }
}
