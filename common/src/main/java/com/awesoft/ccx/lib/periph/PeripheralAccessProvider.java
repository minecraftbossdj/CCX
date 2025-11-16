package com.awesoft.ccx.lib.periph;

public class PeripheralAccessProvider {
    private static CCXPeripheralAccess INSTANCE;

    public static void register(CCXPeripheralAccess access) {
        INSTANCE = access;
    }

    public static CCXPeripheralAccess get() {
        if (INSTANCE == null) throw new IllegalStateException("PeripheralAccess not registered!");
        return INSTANCE;
    }
}
