package com.awesoft.ccx_drones.client.screen;

import com.awesoft.ccx_drones.CCXDrones;
import com.awesoft.ccx_drones.menu.DroneMenu;
import dan200.computercraft.client.gui.AbstractComputerScreen;
import dan200.computercraft.client.gui.GuiSprites;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import dan200.computercraft.client.render.RenderTypes;
import dan200.computercraft.client.render.SpriteRenderer;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.InputHandler;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dev.architectury.platform.Platform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static com.awesoft.ccx_drones.menu.DroneMenu.BORDER;


public class DroneScreen extends AbstractComputerScreen<DroneMenu> {
    private static final ResourceLocation BACKGROUND_ADVANCED = new ResourceLocation("ccx_drones", "textures/gui/drone.png");

    private static final int TEX_WIDTH = 278;
    private static final int TEX_HEIGHT = 217;

    private static final int FULL_TEX_SIZE = 512;

    public DroneScreen(DroneMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, 8);
        imageWidth = TEX_WIDTH + AbstractComputerMenu.SIDEBAR_WIDTH;
        imageHeight = TEX_HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        var advanced = family == ComputerFamily.ADVANCED;
        var texture = BACKGROUND_ADVANCED;
        graphics.blit(texture, leftPos + AbstractComputerMenu.SIDEBAR_WIDTH, topPos, 0, 0, 0, TEX_WIDTH, TEX_HEIGHT, FULL_TEX_SIZE, FULL_TEX_SIZE);

        var spriteRenderer = SpriteRenderer.createForGui(graphics, RenderTypes.GUI_SPRITES);
        ComputerSidebar.renderBackground(spriteRenderer, GuiSprites.getComputerTextures(family), leftPos, topPos + sidebarYOffset);
        graphics.flush();
    }

    private static Field COMPUTER_ACTIONS;
    private static Field COMPUTER_INPUT;

    static {
        try {
            COMPUTER_ACTIONS =
                    AbstractComputerScreen.class.getDeclaredField("computerActions");
            COMPUTER_ACTIONS.setAccessible(true);

            COMPUTER_INPUT = AbstractComputerScreen.class.getDeclaredField("computerInput");
            COMPUTER_INPUT.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
            COMPUTER_ACTIONS = null;
            COMPUTER_INPUT = null;
        }

        try {
            Class<?> computerActionsClass = Class.forName(
                    "dan200.computercraft.client.gui.ClientComputerActions"
            );
            Class<?> computerInputClass = Class.forName(
                    "dan200.computercraft.core.input.UserComputerInput"
            );
            NEW_CCTERM = TerminalWidget.class.getConstructor(
                    Terminal.class,
                    computerInputClass,
                    computerActionsClass,
                    int.class,
                    int.class
            );
        } catch (NoSuchMethodException e) {
            CCXDrones.LOGGER.warn("Error occured while getting variables: "+e);
        } catch (ClassNotFoundException e) {
            CCXDrones.LOGGER.warn("Errored occured while getting variables: "+e);
        }
    }

    @Nullable
    protected Object getComputerOptions() {
        if (COMPUTER_ACTIONS == null) return null;
        try {
            return COMPUTER_ACTIONS.get(this);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    @Nullable
    protected Object getComputerInput() {
        if (COMPUTER_INPUT == null) return null;
        try {
            return COMPUTER_INPUT.get(this);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static Constructor<? extends TerminalWidget> NEW_CCTERM;


    @Override
    protected TerminalWidget createTerminal() {
        if (Platform.getMod("computercraft").getVersion().contains("1.117.")) {
            try {
                return NEW_CCTERM.newInstance(
                        terminalData,
                        getComputerInput(),
                        getComputerOptions(),
                        leftPos + BORDER + AbstractComputerMenu.SIDEBAR_WIDTH,
                        topPos + BORDER
                );
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                CCXDrones.LOGGER.warn("Error occured: " + e);
            }
        }
        return new TerminalWidget(terminalData, input, leftPos + BORDER + AbstractComputerMenu.SIDEBAR_WIDTH, topPos + BORDER);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
