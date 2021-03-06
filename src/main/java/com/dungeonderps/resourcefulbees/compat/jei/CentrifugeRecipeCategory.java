package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CentrifugeRecipeCategory implements IRecipeCategory<CentrifugeRecipe> {

  public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "centrifuge");
  protected final IDrawableAnimated arrow;
  private final IDrawable icon;
  private final IDrawable background;
  private final String localizedName;

  public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
    this.background = guiHelper.createDrawable(new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/centrifuge.png"), 0, 0, 133, 65);
    this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.CENTRIFUGE_ITEM.get()));
    this.localizedName = I18n.format("gui.resourcefulbees.jei.category.centrifuge");
    this.arrow = guiHelper.drawableBuilder(new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/centrifuge.png"), 0, 66, 73, 30)
            .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
  }

  @Nonnull
  @Override
  public ResourceLocation getUid() {
    return ID;
  }

  @Nonnull
  @Override
  public Class<? extends CentrifugeRecipe> getRecipeClass() {
    return CentrifugeRecipe.class;
  }

  @Nonnull
  @Override
  public String getTitle() {
    return localizedName;
  }

  @Nonnull
  @Override
  public IDrawable getBackground() {
    return background;
  }

  @Nonnull
  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public void setIngredients(CentrifugeRecipe recipe, IIngredients iIngredients) {
    iIngredients.setInputIngredients(Lists.newArrayList(recipe.ingredient, Ingredient.fromItems(Items.GLASS_BOTTLE)));
    List<Pair<ItemStack,Double>> outputs = recipe.outputs;
    List<ItemStack> stacks = new ArrayList<>();
    stacks.add(outputs.get(2).getLeft().copy());
    stacks.add(outputs.get(0).getLeft().copy());
    stacks.add(outputs.get(1).getLeft().copy());
    iIngredients.setOutputs(VanillaTypes.ITEM, stacks);
  }

  @Override
  public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull CentrifugeRecipe centrifugeRecipe, @Nonnull IIngredients iIngredients) {
    IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();
    guiItemStacks.init(CentrifugeTileEntity.HONEYCOMB_SLOT, true, 9, 5);
    guiItemStacks.init(CentrifugeTileEntity.BOTTLE_SLOT, true, 9, 23);
    guiItemStacks.init(CentrifugeTileEntity.OUTPUT1, false, 108, 5);
    guiItemStacks.init(CentrifugeTileEntity.OUTPUT2, false, 108, 23);
    guiItemStacks.init(CentrifugeTileEntity.HONEY_BOTTLE, false, 59, 44);
    guiItemStacks.set(iIngredients);
  }

  @Override
  public void draw(CentrifugeRecipe recipe, @Nonnull MatrixStack matrix, double mouseX, double mouseY) {
    this.arrow.draw(matrix,31, 14);

    final double beeOutput = recipe.outputs.get(0).getRight();
    final double beeswax = recipe.outputs.get(1).getRight();
    final double honeyBottle = recipe.outputs.get(2).getRight();

    DecimalFormat decimalFormat = new DecimalFormat("##%");

    String honeyBottleString = decimalFormat.format(honeyBottle);
    String beeOutputString = decimalFormat.format(beeOutput);
    String beeswaxString = decimalFormat.format(beeswax);

    Minecraft minecraft = Minecraft.getInstance();
    FontRenderer fontRenderer = minecraft.fontRenderer;
    if (beeOutput < 1.0) fontRenderer.drawString(matrix, beeOutputString, 80, 10, 0xff808080);
    if (honeyBottle < 1.0) fontRenderer.drawString(matrix, honeyBottleString, 80, 50, 0xff808080);
    if (beeswax < 1.0) fontRenderer.drawString(matrix, beeswaxString, 80, 30, 0xff808080);
  }
}
