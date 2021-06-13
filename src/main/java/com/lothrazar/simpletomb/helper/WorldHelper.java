package com.lothrazar.simpletomb.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.lothrazar.simpletomb.data.LocationBlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.common.DimensionManager;

public class WorldHelper {

  public static LocationBlockPos findGraveSpawn(EntityPlayer player, BlockPos initPos) {
    int xRange = 8;
    int yRange = 7;
    int zRange = 8;
    Iterable<BlockPos> it = BlockPos.getAllInBox(
        new BlockPos(initPos.getX() - xRange, initPos.getY() - yRange, initPos.getZ() - zRange),
        new BlockPos(initPos.getX() + xRange, initPos.getY() + yRange, initPos.getZ() + zRange));
    List<BlockPos> positions = StreamSupport.stream(it.spliterator(), false).collect(Collectors.toList());
    positions.sort((pos0, pos1) -> {
      double dist0 = pos0.distanceSq(initPos.getX(), initPos.getY(), initPos.getZ());
      double dist1 = pos1.distanceSq(initPos.getX(), initPos.getY(), initPos.getZ());
      return Double.compare(dist0, dist1);
    });
    for (BlockPos pos : positions) {
      if (player.world.getBlockState(pos).getBlock() == Blocks.AIR) {
        return new LocationBlockPos(pos, player.dimension);
      }
    }
    return null;
  }

  public static boolean atInterval(long ticksExisted, int tick) {
    return ticksExisted > 0L && ticksExisted % tick == 0L;
  }

  public static int getRandom(Random random, int min, int max) {
    return random.nextInt(max - min + 1) + min;
  }

  public static float getRandom(Random random, float min, float max) {
    return random.nextFloat() * (max - min) + min;
  }

  public static double getRandom(Random random, double min, double max) {
    return random.nextDouble() * (max - min) + min;
  }

  public static BlockPos getCloserValidPos(World world, BlockPos pos) {
    WorldBorder border = world.getWorldBorder();
    boolean validXZ = border.contains(pos);
    boolean validY = !world.isOutsideBuildHeight(pos);
    if (validXZ && validY) {
      return pos;
    }
    else {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      if (!validXZ) {
        x = Math.min(Math.max(pos.getX(), (int) border.minX()),
            (int) border.maxX());
        z = Math.min(Math.max(pos.getZ(), (int) border.minZ()),
            (int) border.maxZ());
      }
      if (!validY) {
        y = Math.max(Math.min(pos.getY(), world.provider.getActualHeight()),
            0);
      }
      return new BlockPos(x, y, z);
    }
  }

  public static boolean isValidPos(World world, BlockPos pos) {
    return (world.getWorldBorder().contains(pos) && !world.isOutsideBuildHeight(pos));
  }

  public static boolean isValidDimension(int dim) {
    return getDimensionIds().contains(dim);
  }

  private static List<Integer> getDimensionIds() {
    return Arrays.asList(DimensionManager.getStaticDimensionIDs());
  }

  public static boolean isRuleKeepInventory(EntityPlayer player) {
    return isRuleKeepInventory(player.world);
  }

  public static boolean isRuleKeepInventory(World world) {
    return world.getGameRules().getBoolean("keepInventory");
  }

  public static void removeNoEvent(World world, BlockPos pos) {
    placeNoEvent(world, pos, Blocks.AIR.getDefaultState());
  }

  public static void placeNoEvent(World world, BlockPos pos, IBlockState state) {
    world.setBlockState(pos, state, 3);
  }

  public static boolean isNight(World world) {
    float angle = world.getCelestialAngle(0.0F);
    return angle >= 0.245F && angle <= 0.755F;
  }

  public static float[] getRGBColor3F(int color) {
    return new float[] {
        (color >> 16 & 255) / 255.0F,
        (color >> 8 & 255) / 255.0F,
        (color & 255) / 255.0F,
    };
  }

  public static float[] getHSBtoRGBF(float hue, float saturation, float brightness) {
    int r = 0;
    int g = 0;
    int b = 0;
    if (saturation == 0.0F) {
      r = g = b = (int) (brightness * 255.0F + 0.5F);
    }
    else {
      float h = (hue - (float) Math.floor(hue)) * 6.0F;
      float f = h - (float) Math.floor(h);
      float p = brightness * (1.0F - saturation);
      float q = brightness * (1.0F - saturation * f);
      float t = brightness * (1.0F - saturation * (1.0F - f));
      switch ((int) h) {
        case 0:
          r = (int) (brightness * 255.0F + 0.5F);
          g = (int) (t * 255.0F + 0.5F);
          b = (int) (p * 255.0F + 0.5F);
        break;
        case 1:
          r = (int) (q * 255.0F + 0.5F);
          g = (int) (brightness * 255.0F + 0.5F);
          b = (int) (p * 255.0F + 0.5F);
        break;
        case 2:
          r = (int) (p * 255.0F + 0.5F);
          g = (int) (brightness * 255.0F + 0.5F);
          b = (int) (t * 255.0F + 0.5F);
        break;
        case 3:
          r = (int) (p * 255.0F + 0.5F);
          g = (int) (q * 255.0F + 0.5F);
          b = (int) (brightness * 255.0F + 0.5F);
        break;
        case 4:
          r = (int) (t * 255.0F + 0.5F);
          g = (int) (p * 255.0F + 0.5F);
          b = (int) (brightness * 255.0F + 0.5F);
        break;
        case 5:
          r = (int) (brightness * 255.0F + 0.5F);
          g = (int) (p * 255.0F + 0.5F);
          b = (int) (q * 255.0F + 0.5F);
      }
    }
    return new float[] {
        r / 255.0F,
        g / 255.0F,
        b / 255.0F,
    };
  }
}
