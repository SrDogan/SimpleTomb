package com.lothrazar.simpletomb.particle;

import com.lothrazar.simpletomb.helper.WorldHelper;
import com.lothrazar.simpletomb.proxy.ClientUtils;
import java.util.Random;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleGhost extends TransparentParticle {

  private final IAnimatedSprite spriteSet;
  private final double mX, mZ;

  private ParticleGhost(IAnimatedSprite spriteSet, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
    super(world, x, y + 1d, z);
    this.mX = motionX;
    this.mZ = motionZ;
    this.motionX = this.motionY = this.motionZ = 0d;
    setMaxAge(200);
    this.canCollide = false;
    multiplyParticleScaleBy(8f);
    setColor(1f, 1f, 1f);
    this.spriteSet = spriteSet;
    selectSpriteWithAge(this.spriteSet);
  }

  @Override
  public void tick() {
    super.tick();
    if (isAlive()) {
      if (this.age == 10) {
        this.motionX = mX;
        this.motionZ = mZ;
      }
      float ratio = this.age / (float) this.maxAge;
      setAlphaF((1f - ratio) * 0.8f);
      selectSpriteWithAge(this.spriteSet);
      if (world.isRemote) {
        ClientUtils.produceGraveSmoke(this.world, this.posX, this.posY - 1d, this.posZ);
      }
    }
  }

  @Override
  protected int getBrightnessForRender(float partialTick) {
    int skylight = 15;
    int blocklight = 15;
    return skylight << 20 | blocklight << 4;
  }

  @Override
  public IParticleRenderType getRenderType() {
    return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
  }

  public static class Factory implements IParticleFactory<BasicParticleType> {

    private IAnimatedSprite spriteSet;

    public Factory(IAnimatedSprite spriteSet) {
      this.spriteSet = spriteSet;
    }

    @Override
    public Particle makeParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      Random rand = world == null || world.rand == null ? new Random() : world.rand;
      return new ParticleGhost(this.spriteSet, world, x, y, z, WorldHelper.getRandom(rand, -0.05d, 0.05d), 0d, WorldHelper.getRandom(rand, -0.05d, 0.05d));
    }
  }
}
