package com.awesoft.ccx_drones.client.screen;

import com.awesoft.ccx_drones.menu.DroneMenu;
import dan200.computercraft.client.gui.AbstractComputerScreen;
import dan200.computercraft.client.gui.GuiSprites;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import dan200.computercraft.client.render.RenderTypes;
import dan200.computercraft.client.render.SpriteRenderer;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

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

    @Override
    protected TerminalWidget createTerminal() {
        return new TerminalWidget(terminalData, input, leftPos + BORDER + AbstractComputerMenu.SIDEBAR_WIDTH, topPos + BORDER);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
