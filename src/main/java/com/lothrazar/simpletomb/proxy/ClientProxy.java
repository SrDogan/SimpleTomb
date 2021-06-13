package com.lothrazar.simpletomb.proxy;

import java.util.function.Predicate;
import com.lothrazar.simpletomb.TombRegistry;
import com.lothrazar.simpletomb.block.CustomStateMapper;
import com.lothrazar.simpletomb.block.RenderTomb;
import com.lothrazar.simpletomb.block.TileEntityTomb;
import com.lothrazar.simpletomb.event.ClientEvents;
import com.lothrazar.simpletomb.particle.ParticleCasting;
import com.lothrazar.simpletomb.particle.ParticleGraveSmoke;
import com.lothrazar.simpletomb.particle.ParticleGraveSoul;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

  public static enum FogDensity {
    NONE, LOW, NORMAL, HIGH,
  }

  @Override
  public void init() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTomb.class, new RenderTomb());
    MinecraftForge.EVENT_BUS.register(new ClientEvents());
  }

  @Override
  public void produceGraveSmoke(World world, BlockPos pos) {
    int fogDensity = world.rand.nextInt(FogDensity.values().length);
    for (int i = 0; i <= fogDensity; ++i) {
      ParticleGraveSmoke particle = new ParticleGraveSmoke(world,
          pos.getX() + world.rand.nextGaussian(),
          pos.getY() + 0.4D,
          pos.getZ() + world.rand.nextGaussian(),
          (world.rand.nextFloat() - 0.5F) * 0.03D, 0.0D, (world.rand.nextFloat() - 0.5F) * 0.03D);
      Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }
  }

  @Override
  public void produceGraveSoul(World world, BlockPos pos) {
    ParticleGraveSoul particle = new ParticleGraveSoul(world, pos.getX(), pos.getY(), pos.getZ(), 0.3D);
    Minecraft.getMinecraft().effectRenderer.addEffect(particle);
  }

  @Override
  public void produceParticleCasting(
      EntityLivingBase caster,
      Predicate<EntityLivingBase> predic) {
    if (caster != null) {
      for (int i = 1; i <= 2; ++i) {
        ParticleCasting particle = new ParticleCasting(caster.world, caster, predic, 0.0D, i * 0.5D);
        ParticleManager er = Minecraft.getMinecraft().effectRenderer;
        er.addEffect(particle);
        particle = new ParticleCasting(caster.world, caster, predic, 0.5D, (i + 1) * 0.5D);
        er.addEffect(particle);
        particle = new ParticleCasting(caster.world, caster, predic, 1.0D, i * 0.5D);
        er.addEffect(particle);
        particle = new ParticleCasting(caster.world, caster, predic, 1.5D, (i + 1) * 0.5D);
        er.addEffect(particle);
        particle = new ParticleCasting(caster.world, caster, predic, 2.0D, i * 0.5D);
        er.addEffect(particle);
      }
    }
  }

  @Override
  public void registerModels() {
    for (int i = 0; i < TombRegistry.graves.length; ++i) {
      ModelLoader.setCustomStateMapper(TombRegistry.graves[i], new CustomStateMapper());
    }
    Item item = TombRegistry.grave_key;
    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
  }
}
