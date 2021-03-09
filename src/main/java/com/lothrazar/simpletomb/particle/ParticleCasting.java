package com.lothrazar.simpletomb.particle;

import com.lothrazar.simpletomb.ModTomb;
import com.lothrazar.simpletomb.helper.WorldHelper;
import java.util.function.Predicate;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleCasting extends CustomParticle {

  private static final ResourceLocation COMMON_TEXTURE = new ResourceLocation(ModTomb.MODID, "textures/particle/casting.png");
  private final LivingEntity caster;
  private final Predicate<LivingEntity> predic;
  private final double radius = 1.1;
  private double angle;
  private static final double ROT_INCR = Math.PI * 0.05D;
  private final float colorR;
  private final float colorG;
  private final float colorB;
  private boolean goUp;

  public ParticleCasting(ClientWorld world, LivingEntity caster, Predicate<LivingEntity> predic, double addY, double angle) {
    super(world, caster.getPosX(), caster.getPosY() + addY, caster.getPosZ());
    this.motionX = this.motionY = this.motionZ = 0d;
    setAlphaF(1f);
    this.goUp = addY < 1d;
    this.caster = caster;
    this.predic = predic;
    this.particleScale = world.rand.nextFloat() * 0.1f + 0.15f;
    this.angle = angle + WorldHelper.getRandom(world.rand, -0.25, 0.25);
    this.particleAngle = world.rand.nextFloat() * (float) (2d * Math.PI);
    float[] color = WorldHelper.getRGBColor3F(14937088);
    this.colorR = color[0];
    this.colorG = color[1];
    this.colorB = color[2];
    this.canCollide = false;
    updatePosition();
  }

  private void updatePosition() {
    this.angle += 0.01f;
    this.prevPosX = this.posX = caster.getPosX() + this.radius * Math.cos(2 * Math.PI * (this.angle));
    this.prevPosY = this.posY = this.posY + (this.goUp ? 0.02d : -0.02d);
    this.prevPosZ = this.posZ = caster.getPosZ() + this.radius * Math.sin(2 * Math.PI * (this.angle));
    setColor(clampColor(this.colorR + (WorldHelper.getRandom(world.rand, -20f, 20f) / 255f)), clampColor(this.colorG - (WorldHelper.getRandom(world.rand, -20f, 20f) / 255f)),
        clampColor(this.colorB + (WorldHelper.getRandom(world.rand, -20f, 20f) / 255f)));
    this.prevParticleAngle = this.particleAngle;
    this.particleAngle += ROT_INCR;
  }

  private float clampColor(float color) {
    return MathHelper.clamp(color, 0f, 1f);
  }

  @Override
  public void tick() {
    if (this.posY > caster.getPosY() + 2d || this.posY < caster.getPosY()) {
      this.goUp = !this.goUp;
    }
    if (this.predic.test(this.caster)) {
      setExpired();
    }
    updatePosition();
    this.age++;
  }

  @Override
  protected int getBrightnessForRender(float partialTick) {
    int skylight = 5;
    int blocklight = 15;
    return skylight << 20 | blocklight << 4;
  }

  @Override
  ResourceLocation getTexture() {
    return COMMON_TEXTURE;
  }
}
