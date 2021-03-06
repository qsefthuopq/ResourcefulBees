package com.dungeonderps.resourcefulbees.init;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.TieredBeehiveBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;
import static com.dungeonderps.resourcefulbees.config.BeeBuilder.BEE_PATH;
import static com.dungeonderps.resourcefulbees.config.BeeBuilder.RESOURCE_PATH;

public class ModSetup {

    public static void initialize(){
        setupPaths();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ModSetup::loadResources);
    }

    private static void setupPaths(){
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path rbBeesPath = Paths.get(configPath.toAbsolutePath().toString(), ResourcefulBees.MOD_ID, "bees");
        Path rbAssetsPath = Paths.get(configPath.toAbsolutePath().toString(),ResourcefulBees.MOD_ID, "resources");
        BEE_PATH = rbBeesPath;
        RESOURCE_PATH = rbAssetsPath;

        try { Files.createDirectories(rbBeesPath);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) { LOGGER.error("failed to create resourcefulbees config directory");}

        try { Files.createDirectory(rbAssetsPath);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) { LOGGER.error("Failed to create assets directory");}

        try {
            FileWriter file = new FileWriter(Paths.get(rbAssetsPath.toAbsolutePath().toString(), "pack.mcmeta").toFile());
            String mcMetaContent = "{\"pack\":{\"pack_format\":5,\"description\":\"Resourceful Bees resource pack used for lang purposes for the user to add lang for bee/items.\"}}";
            file.write(mcMetaContent);
            file.close();
        } catch (FileAlreadyExistsException ignored){
        } catch (IOException e) { LOGGER.error("Failed to create pack.mcmeta file for resource loading");}
    }

    public static void setupDispenserCollectionBehavior() {
        DispenserBlock.registerDispenseBehavior(Items.SHEARS.asItem(), new OptionalDispenseBehavior() {
            @Nonnull
            protected ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
                World world = source.getWorld();
                if (!world.isRemote()) {
                    this.setSuccessful(false);
                    BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));

                    for(net.minecraft.entity.Entity entity : world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(blockpos), e -> !e.isSpectator() && e instanceof net.minecraftforge.common.IForgeShearable)) {
                        net.minecraftforge.common.IForgeShearable target = (net.minecraftforge.common.IForgeShearable)entity;
                        if (target.isShearable(stack, world, blockpos)) {
                            FakePlayer fakie = FakePlayerFactory.getMinecraft((ServerWorld) world);
                            java.util.List<ItemStack> drops = target.onSheared(fakie, stack, entity.world, blockpos,
                                    net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantments.FORTUNE, stack));
                            java.util.Random rand = new java.util.Random();
                            drops.forEach(d -> {
                                net.minecraft.entity.item.ItemEntity ent = entity.entityDropItem(d, 1.0F);
                                if (ent != null)
                                    ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
                            });
                            if (stack.attemptDamageItem(1, world.rand, null)) {
                                stack.setCount(0);
                            }

                            this.setSuccessful(true);
                            break;
                        }
                    }

                    if (!this.isSuccessful()) {
                        BlockState blockstate = world.getBlockState(blockpos);
                        if (blockstate.isIn(BlockTags.BEEHIVES)) {
                            int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                            if (i >= 5) {
                                if (stack.attemptDamageItem(1, world.rand, null)) {
                                    stack.setCount(0);
                                }

                                BeehiveBlock.dropHoneyComb(world, blockpos);
                                ((BeehiveBlock)blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null, BeehiveTileEntity.State.BEE_RELEASED);
                                this.setSuccessful(true);
                            }
                        }
                        else if (blockstate.getBlock() instanceof TieredBeehiveBlock) {
                            int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                            if (i >= 5) {
                                if (stack.attemptDamageItem(1, world.rand, null)) {
                                    stack.setCount(0);
                                }

                                TieredBeehiveBlock.dropResourceHoneycomb((TieredBeehiveBlock) blockstate.getBlock(), world, blockpos);
                                ((BeehiveBlock) blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null,
                                        BeehiveTileEntity.State.BEE_RELEASED);
                                this.setSuccessful(true);
                            }
                        }
                    }
                }
                return stack;
            }
        });
    }

    public static void loadResources() {
        Minecraft.getInstance().getResourcePackList().addPackFinder(new IPackFinder() {

            @Override
            public <T extends ResourcePackInfo> void func_230230_a_(@Nonnull Consumer<T> p_230230_1_, @Nonnull ResourcePackInfo.IFactory<T> factory) {
                final T packInfo = ResourcePackInfo.createResourcePack(
                        ResourcefulBees.MOD_ID,
                        true,
                        () -> new FolderPack(RESOURCE_PATH.toFile()),
                        factory,
                        ResourcePackInfo.Priority.TOP,
                        IPackNameDecorator.createUnnamedDecorator()
                );
                if (packInfo == null) {
                    LOGGER.error("Failed to load resource pack, some things may not work.");
                    return;
                }
                p_230230_1_.accept(packInfo);
            }
        });
    }
}
