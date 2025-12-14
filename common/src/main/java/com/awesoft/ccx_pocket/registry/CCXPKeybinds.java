package com.awesoft.ccx_pocket.registry;

import com.awesoft.ccx_pocket.networking.hmd.OpenHMDPacket;
import com.awesoft.ccx_pocket.networking.pocket_pack.OpenPackPacket;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import org.lwjgl.glfw.GLFW;

public class CCXPKeybinds {
    public static final String OPEN_HMD_STRING = "key.ccx_pocket.open_hmd";
    public static final String OPEN_PACK_STRING = "key.ccx_pocket.open_pack";

    public static KeyMapping OPEN_HMD = new KeyMapping(
            OPEN_HMD_STRING,
            GLFW.GLFW_KEY_H,
            "category.ccx_pocket.main"
    );

    public static KeyMapping OPEN_PACK = new KeyMapping(
            OPEN_PACK_STRING,
            GLFW.GLFW_KEY_G,
            "category.ccx_pocket.main"
    );

    public static void register() {
        KeyMappingRegistry.register(OPEN_HMD);
        KeyMappingRegistry.register(OPEN_PACK);

        ClientTickEvent.CLIENT_POST.register(client -> {
            while (OPEN_HMD.consumeClick()) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

                NetworkManager.sendToServer(OpenHMDPacket.ID, buf);
            }
            while (OPEN_PACK.consumeClick()) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

                NetworkManager.sendToServer(OpenPackPacket.ID, buf);
            }
        });
    }

}
