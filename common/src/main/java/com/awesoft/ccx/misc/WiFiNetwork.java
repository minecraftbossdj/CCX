package com.awesoft.ccx.misc;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.usb.USBBlockEntity;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.NotAttachedException;

import java.util.ArrayList;
import java.util.List;

public class WiFiNetwork {
    static class Listener {
        public final int channel;
        public final IComputerAccess computer;
        public final USBBlockEntity blkEnt;

        Listener(IComputerAccess computer, int channel, USBBlockEntity blkEnt) {
            this.computer = computer;
            this.channel = channel;
            this.blkEnt = blkEnt;
        }
    }


    //I very much dislike how this code works, but I am limited by how cc peripherals work and me being dumb, so this code has to do for now.
    private static final List<Listener> listeners = new ArrayList<>();


    public static void broadcast(String message, int channel, List<IComputerAccess> computers) {
        try {
            listeners.forEach((listener -> {

                boolean isHost = false;
                for (IComputerAccess computer : computers) {
                    if (listener.computer.getID() == computer.getID()) isHost = true;
                }

                if (isHost) return;

                if (listener.channel == channel) {
                    IComputerAccess comp = listener.computer;
                    comp.queueEvent("wifi_message", channel, message);
                }

            }));
        } catch (NotAttachedException ignored) {}
    }

    public static void open(int channel, IComputerAccess comp, USBBlockEntity blkEnt) {
        try {
            for (int i = 0; i < listeners.size(); i++) {

                    if (listeners.get(i).computer.getID() == comp.getID()) {
                        listeners.remove(i);
                    }

            }
            listeners.add(new Listener(comp, channel, blkEnt));
        } catch (NotAttachedException ignored) {}
    }

    public static boolean close(IComputerAccess comp) {
        try {
            for (int i = 0; i < listeners.size(); i++) {
                if (listeners.get(i).computer.getID() == comp.getID()) {
                    listeners.remove(i);
                    return true;
                }
            }
        } catch (NotAttachedException ignored) {}
        return false;
    }

    public static boolean closeAll(USBBlockEntity blkEnt) {
        try {
            for (int i = 0; i < listeners.size(); i++) {
                if (listeners.get(i).blkEnt == blkEnt) {
                    listeners.remove(i);
                    return true;
                }

                if (listeners.get(i).blkEnt.getBlockPos() == blkEnt.getBlockPos()) { //JUST IN CASE SOMETHING FREAKY HAPPENS
                    listeners.remove(i);
                    return true;
                }
            }
        } catch (NotAttachedException ignored) {}
        return false;
    }
}
