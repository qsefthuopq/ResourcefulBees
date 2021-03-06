package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.container.MechanicalCentrifugeContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class MechanicalCentrifugeScreen extends ContainerScreen<MechanicalCentrifugeContainer> {
    public MechanicalCentrifugeScreen(MechanicalCentrifugeContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }
    int textColor = 0x404040;

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture;
        texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/mechanical_centrifuge.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bindTexture(texture);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrix, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        this.font.drawString(matrix, this.title.getString(), 25, 5, textColor);
    }
}
