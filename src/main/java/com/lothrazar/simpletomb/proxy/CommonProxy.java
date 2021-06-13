package com.lothrazar.simpletomb.proxy;

import java.util.function.Predicate;
import com.lothrazar.simpletomb.ModTomb;
import com.lothrazar.simpletomb.block.TileEntityTomb;
import com.lothrazar.simpletomb.event.PlayerTombEvents;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy implements IProxy {

  @Override
  public void preInit() {
    MinecraftForge.EVENT_BUS.register(new PlayerTombEvents());
    GameRegistry.registerTileEntity(TileEntityTomb.class, new ResourceLocation(ModTomb.MODID, ":tombstone"));
  }

  @Override
  public void init() {}

  @Override
  public void postInit() {}

  @Override
  public void produceGraveSmoke(World var1, BlockPos var2) {}

  @Override
  public void produceGraveSoul(World var1, BlockPos var2) {}

  @Override
  public void produceParticleCasting(
      EntityLivingBase var1,
      Predicate<EntityLivingBase> var2) {}

  @Override
  public void registerModels() {}
}
