package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.item.AdvancedRemoteTerminalItem;
import com.awesoft.ccx.item.RemoteTerminalItem;
import com.awesoft.ccx.item.pciecards.BaseCardItem;
import com.awesoft.ccx.item.pciecards.ChestCardItem;
import com.awesoft.ccx.item.pciecards.PeripheralCardItem;
import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class CCXItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(CCX.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<ServerPocketItem> SERVER_ADVANCED = ITEMS.register("server_advanced",
            () -> new ServerPocketItem(new Item.Properties().stacksTo(1), ComputerFamily.ADVANCED));

    public static final RegistrySupplier<ServerPocketItem> SERVER_COMMAND = ITEMS.register("server_command",
            () -> new ServerPocketItem(new Item.Properties().stacksTo(1), ComputerFamily.COMMAND));

    public static final RegistrySupplier<Item> SERVER_REMOTE = ITEMS.register("server_remote",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final RegistrySupplier<PeripheralCardItem> PERIPHERAL_CARD = ITEMS.register("peripheral_card",
            () -> new PeripheralCardItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistrySupplier<ChestCardItem> INVENTORY_CARD = ITEMS.register("inventory_card",
            () -> new ChestCardItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistrySupplier<BaseCardItem> FAKE_CARD = ITEMS.register("fake_card",
            () -> new BaseCardItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistrySupplier<Item> REMOTE_TERMINAL = ITEMS.register("remote_terminal",
            () -> new RemoteTerminalItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistrySupplier<Item> REMOTE_TERMINAL_ADVANCED = ITEMS.register("remote_terminal_advanced",
            () -> new AdvancedRemoteTerminalItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistrySupplier<Item> FAKE_USB = ITEMS.register("fake_usb",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final RegistrySupplier<Item> WIRELESS_USB = ITEMS.register("wireless_usb",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static void register() {
        ITEMS.register();
    }
}
