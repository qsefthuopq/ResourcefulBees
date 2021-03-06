package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.lib.MutationTypes;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("NullableProblems")
public class FluidToFluid implements IRecipeCategory<FluidToFluid.Recipe> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/beemutation.png");
    public static final ResourceLocation ICONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "fluid_to_fluid_mutation");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable info;
    private final IDrawable beeHive;
    private final String localizedName;

    public FluidToFluid(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_BACK, -12, 0, 99, 75).addPadding(0, 0, 0, 0).build();
        this.icon = guiHelper.createDrawable(ICONS, 0,0,16,16);
        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
        this.beeHive = guiHelper.createDrawableIngredient(new ItemStack(RegistryHandler.T1_BEEHIVE_ITEM.get()));
        this.localizedName = I18n.format("gui.resourcefulbees.jei.category.fluid_to_fluid_mutation");
    }

    public static List<FluidToFluid.Recipe> getMutationRecipes(IIngredientManager ingredientManager) {
        List<FluidToFluid.Recipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()){
            if (bee.getValue().hasMutation()) {

                String mutationIn = bee.getValue().getMutationInput();
                String mutationOut = bee.getValue().getMutationOutput();

                if (BeeInfoUtils.TAG_RESOURCE_PATTERN.matcher(mutationIn).matches()) {
                    mutationIn = mutationIn.replace(BeeConstants.TAG_PREFIX, "");

                    ITag<Fluid> fluidTag = BeeInfoUtils.getFluidTag(mutationIn);
                    if (fluidTag != null) {
                        Fluid fluidOut = BeeInfoUtils.getFluid(mutationOut);
                        if (BeeInfoUtils.isValidFluid(fluidOut)){
                            recipes.add(new Recipe(fluidTag, new FluidStack(fluidOut,1000), bee.getKey(), MutationTypes.BLOCK_TO_FLUID, true));
                        }
                    }
                } else {
                    Enum<MutationTypes> mutationType = bee.getValue().getMutationType();

                    if (MutationTypes.FLUID_TO_FLUID.equals(mutationType)) {
                        Fluid fluidIn = BeeInfoUtils.getFluid(mutationIn);
                        Fluid fluidOut = BeeInfoUtils.getFluid(mutationOut);
                        if (BeeInfoUtils.isValidFluid(fluidIn) && BeeInfoUtils.isValidFluid(fluidOut))
                            recipes.add( new Recipe( new FluidStack(fluidIn, 1000), new FluidStack(fluidOut, 1000), bee.getKey(), mutationType, false));
                    }
                } //END INDIVIDUAL CHECKS
            }
        }
        return recipes;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends FluidToFluid.Recipe> getRecipeClass() {
        return FluidToFluid.Recipe.class;
    }

    @Override
    public String getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(FluidToFluid.Recipe recipe, IIngredients ingredients) {
        if (recipe.isAcceptsAny()) {
            if (MutationTypes.FLUID_TO_FLUID.equals(recipe.mutationType)) {
                List<FluidStack> fluids = new ArrayList<>();
                for (Fluid element: recipe.tag.getAllElements() ) {
                    FluidStack fluid = new FluidStack(element, 1000);
                    fluids.add(fluid);
                }
                List<List<FluidStack>> fluid_fluids = new ArrayList<>();
                fluid_fluids.add(fluids);
                ingredients.setInputLists(VanillaTypes.FLUID, fluid_fluids);
                ingredients.setOutput(VanillaTypes.FLUID, recipe.fluidOut);
            }
        }
        else {
            if (MutationTypes.FLUID_TO_FLUID.equals(recipe.mutationType)) {
                ingredients.setInput(VanillaTypes.FLUID, recipe.fluidIn);
                ingredients.setOutput(VanillaTypes.FLUID, recipe.fluidOut);
            }
        }
        ingredients.setInput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.beeType, 135.0F));
    }

    @Override
    public List<ITextComponent> getTooltipStrings(Recipe recipe, double mouseX, double mouseY) {
        double infoX = 63D;
        double infoY = 8D;
        double beeX = 10D;
        double beeY = 6D;
        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D){
            return Collections.singletonList(new StringTextComponent(I18n.format("gui." + ResourcefulBees.MOD_ID + ".jei.category.mutation.info")));
        }
        if (mouseX >= beeX && mouseX <= beeX + 30D && mouseY >= beeY && mouseY <= beeY + 30D){
            return Collections.singletonList(new StringTextComponent(I18n.format("entity." + ResourcefulBees.MOD_ID + "." + recipe.beeType + "_bee")));
        }
        return IRecipeCategory.super.getTooltipStrings(recipe,mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, FluidToFluid.Recipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
        fluidStacks.init(0, false, 66, 49);
        fluidStacks.init(1, true, 16, 58);
        fluidStacks.set(0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        fluidStacks.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        ingredientStacks.init(0, true, 16, 10);
        ingredientStacks.set(0, ingredients.getInputs(JEICompat.ENTITY_INGREDIENT).get(0));
    }

    @Override
    public void draw(FluidToFluid.Recipe recipe, MatrixStack matrix, double mouseX, double mouseY) {
        this.beeHive.draw(matrix, 65, 10);
        this.info.draw(matrix, 63, 8);
    }

    public static class Recipe {
        private final FluidStack fluidIn;
        private final FluidStack fluidOut;
        private final String beeType;

        private final boolean acceptsAny;
        private final ITag<Fluid> tag;

        private final Enum<MutationTypes> mutationType;

        public Recipe(FluidStack baseBlock, FluidStack mutationBlock, String beeType, Enum<MutationTypes> type, boolean acceptsAny) {
            this.fluidIn = baseBlock;
            this.fluidOut = mutationBlock;
            this.beeType = beeType;
            this.mutationType = type;
            this.acceptsAny = acceptsAny;
            this.tag = null;
        }

        //TAGS!!!
        public Recipe(ITag<Fluid> baseBlock, FluidStack mutationBlock, String beeType, Enum<MutationTypes> type, boolean acceptsAny) {
            this.fluidIn = null;
            this.fluidOut = mutationBlock;
            this.beeType = beeType;
            this.mutationType = type;
            this.acceptsAny = acceptsAny;
            this.tag = baseBlock;
        }




        public boolean isAcceptsAny() { return acceptsAny; }
        public ITag<?> getTag() { return tag; }
        public String getBeeType() { return this.beeType; }
    }
}
