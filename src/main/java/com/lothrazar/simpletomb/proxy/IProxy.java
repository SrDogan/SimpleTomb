package com.lothrazar.simpletomb.proxy;

import java.util.function.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IProxy {

  void preInit();

  void init();

  void postInit();

  void produceGraveSmoke(World var1, BlockPos var2);

  void produceGraveSoul(World var1, BlockPos var2);

  void produceParticleCasting(
      EntityLivingBase var1,
      Predicate<EntityLivingBase> var2);

  void registerModels();
}
