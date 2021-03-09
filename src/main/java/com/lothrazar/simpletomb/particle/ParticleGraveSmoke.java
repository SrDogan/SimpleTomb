package com.lothrazar.simpletomb.particle;

import java.util.Random;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleGraveSmoke extends TransparentParticle {

  Random rand = new Random();
  private final IAnimatedSprite spriteSet;
  protected final int halfMaxAge;
  protected final float alphaStep;
  private final float rotIncrement;

  private ParticleGraveSmoke(IAnimatedSprite spriteSet, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
    super(world, x, y + 0.1d, z);
    this.motionX = motionX;
    this.motionY = motionY;
    this.motionZ = motionZ;
    this.particleAlpha = 0f;
    multiplyParticleScaleBy(4f);
    this.particleAngle = (float) (Math.PI * 2) * rand.nextFloat();
    this.rotIncrement = (float) (Math.PI * (rand.nextFloat() - 0.5f) * 0.01d);
    setMaxAge(80);
    this.halfMaxAge = this.maxAge / 2;
    this.alphaStep = 0.08f / this.halfMaxAge;
    this.canCollide = false;
    setColor(0, .5F, .1F);
    //    
    this.spriteSet = spriteSet;
    selectSpriteWithAge(this.spriteSet);
  }

  @Override
  public void tick() {
    super.tick();
    if (isAlive()) {
      selectSpriteWithAge(this.spriteSet);
      this.prevParticleAngle = this.particleAngle;
      this.particleAngle += rotIncrement;
      setAlphaF(MathHelper.clamp(this.age < this.halfMaxAge ? this.age : this.maxAge - this.age, 0, this.halfMaxAge) * this.alphaStep);
    }
  }

  @Override
  protected int getBrightnessForRender(float partialTick) {
    int skylight = 8;
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
      return new ParticleGraveSmoke(this.spriteSet, world, x, y + 0.4d, z, (rand.nextFloat() - 0.5f) * 0.03d, 0d, (rand.nextFloat() - 0.5f) * 0.03d);
    }
  }
}
