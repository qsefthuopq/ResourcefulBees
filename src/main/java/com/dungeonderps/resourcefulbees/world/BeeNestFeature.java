package com.dungeonderps.resourcefulbees.world;

import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.entity.passive.ResourcefulBee;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;

import javax.annotation.Nonnull;
import java.util.Random;

//import com.dungeonderps.resourcefulbees.tileentity.beenest.BeeNestEntity;

public class BeeNestFeature extends Feature<NoFeatureConfig> {

    public BeeNestFeature(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }



    @Override
    public boolean func_230362_a_(@Nonnull ISeedReader worldIn, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull NoFeatureConfig config) {
        Biome biome = worldIn.getBiome(pos);
        Biome.Category category = biome.getCategory();

        if(!Config.GENERATE_BEE_NESTS.get() || rand.nextDouble() > MathUtils.clamp(Config.BEE_NEST_GENERATION_WEIGHT.get(), 0, 1))
            return false;

        boolean flag = rand.nextBoolean();
        BlockPos newPos;
        int y;
        if (category == Biome.Category.NETHER) {
            y = MathUtils.nextIntInclusive(32, 100);
            newPos = new BlockPos(pos.getX(), y, pos.getZ());
            while (worldIn.isAirBlock(newPos.down())){
                newPos = newPos.down();
            }
            while (!worldIn.isAirBlock(newPos)){
                newPos = newPos.up();
            }
            if (newPos.getY() > 100)
                return false;
            if (worldIn.getBlockState(newPos.down()).getBlock().equals(Blocks.LAVA))
                if (rand.nextInt(10) != 10)
                    return false;
        }
        else {
            y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
            newPos = new BlockPos(pos.getX(), y, pos.getZ());
        }
        if (newPos.getY() == 0)
            return false;

        Direction direction = Direction.UP;
        for (Direction dir : BlockStateProperties.FACING.getAllowedValues()) {
            BlockPos blockPos = newPos.offset(dir, 1);
            if (worldIn.isAirBlock(blockPos)) {
                direction = dir;
                break;
            }
        }

        if (direction == Direction.UP)
            return false;

        BlockState newState;

        switch (category) {
            case THEEND:
                newState = RegistryHandler.PURPUR_BEE_NEST.get().getStateContainer().getBaseState();
                break;
            case NETHER:
                if (flag)
                    newState = RegistryHandler.NETHER_BEE_NEST.get().getStateContainer().getBaseState();
                else
                    newState = RegistryHandler.WITHER_BEE_NEST.get().getStateContainer().getBaseState();
                break;
            case SAVANNA:
            case DESERT:
            case MESA:
                newState = RegistryHandler.ACACIA_BEE_NEST.get().getStateContainer().getBaseState();
                break;
            case JUNGLE:
                newState = RegistryHandler.JUNGLE_BEE_NEST.get().getStateContainer().getBaseState();
                break;
            case BEACH:
            case OCEAN:
            case ICY:
                newState = RegistryHandler.PRISMARINE_BEE_NEST.get().getStateContainer().getBaseState();
                break;
            case MUSHROOM:
            case TAIGA:
            case SWAMP:
                newState = RegistryHandler.BEE_NEST.get().getStateContainer().getBaseState();
                break;
            default:
                if (flag)
                    newState = RegistryHandler.BEE_NEST.get().getStateContainer().getBaseState();
                else
                    newState = RegistryHandler.GRASS_BEE_NEST.get().getStateContainer().getBaseState();
        }

        worldIn.setBlockState(newPos, newState, 1);
        TileEntity tileEntity = worldIn.getTileEntity(newPos);

        if (tileEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity nest = (TieredBeehiveTileEntity) tileEntity;
            // TODO - figure out why 0.5f for nests turns to 0.0 when calling getMaxBees() and breaks when hives work fine.
            int maxBees = Math.round(Config.HIVE_MAX_BEES.get() * 0.5f);  //nest.getMaxBees();
            for (int i = rand.nextInt(maxBees); i < maxBees ; i++) {
                ResourcefulBee bee = RegistryHandler.CUSTOM_BEE.get().create(worldIn.getWorld());
                if (bee != null) {
                    bee.setPosition(newPos.getX(), newPos.getY(), newPos.getZ());
                    bee.setBeeType(true);
                    CompoundNBT compoundNBT = new CompoundNBT();
                    bee.writeUnlessPassenger(compoundNBT);
                    int timeinhive = rand.nextInt(bee.getBeeInfo().getMaxTimeInHive());
                    TieredBeehiveTileEntity.Bee beehivetileentity$bee = new TieredBeehiveTileEntity.Bee(compoundNBT, 0, timeinhive);
                    nest.bees.add(beehivetileentity$bee);
                }
            }
        }
        return true;
    }
}
