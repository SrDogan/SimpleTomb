package com.lothrazar.simpletomb.particle;

import java.util.function.Predicate;
import com.lothrazar.simpletomb.helper.WorldHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleCasting extends Particle {

  //  private static final TextureAtlasSprite commonTexture = Minecraft
  //      .getMinecraft()
  //      .getTextureMapBlocks()
  //      .getAtlasSprite(ModTombstone.MODID + ":particles/fake_fog");
  private final EntityLivingBase caster;
  private final Predicate<EntityLivingBase> predic;
  //  private final double radius = 1.1D;
  private final double angle;
  private final float colorR;
  private final float colorG;
  private final float colorB;
  private boolean goUp;

  public ParticleCasting(
      World world,
      EntityLivingBase caster,
      Predicate<EntityLivingBase> predic,
      double addY,
      double angle) {
    super(world, caster.posX, caster.posY + addY, caster.posZ);
    //    this.setParticleTexture(commonTexture);
    SpriteTypes.CASTING.setTexture(this);
    this.goUp = addY < 1.0D;
    this.caster = caster;
    this.predic = predic;
    this.particleScale = 1.3F;
    this.angle = angle + WorldHelper.getRandom(world.rand, -0.25D, 0.25D);
    float[] color = WorldHelper.getRGBColor3F(14937088);//particleCastingColor
    this.colorR = color[0];
    this.colorG = color[1];
    this.colorB = color[2];
    this.setRBGColorF(this.colorR, this.colorG, this.colorB);
    this.particleAlpha = 1.0F;
    this.canCollide = false;
    this.updatePosition();
  }

  private void updatePosition() {
    double ratio = this.particleAge % 80.0D / 80.0D;
    this.motionX = this.motionY = this.motionZ = 0.0D;
    this.prevPosX = this.posX = this.caster.posX +
        1.1D *
            Math.cos(6.283185307179586D * (ratio + this.angle));
    this.prevPosY = this.posY += this.goUp ? 0.02D : -0.02D;
    this.prevPosZ = this.posZ = this.caster.posZ +
        1.1D *
            Math.sin(6.283185307179586D * (ratio + this.angle));
    this.setRBGColorF(
        this.clampColor(this.colorR + WorldHelper.getRandom(world.rand, -10.0F, 50.0F) / 255.0F),
        this.clampColor(this.colorG - WorldHelper.getRandom(world.rand, 0.0F, 30.0F) / 255.0F),
        this.clampColor(this.colorB + WorldHelper.getRandom(world.rand, -50.0F, 30.0F) / 255.0F));
  }

  private float clampColor(float color) {
    return MathHelper.clamp(color, 0.0F, 1.0F);
  }

  @Override
  public void onUpdate() {
    if (this.posY > this.caster.posY + 2.0D || this.posY < this.caster.posY) {
      this.goUp = !this.goUp;
    }
    if (this.predic.test(this.caster)) {
      this.setExpired();
    }
    this.updatePosition();
    ++this.particleAge;
  }

  @Override
  public boolean shouldDisableDepth() {
    return true;
  }

  @Override
  public int getFXLayer() {
    return 1;
  }

  @Override
  public int getBrightnessForRender(float partialTick) {
    return 15728880;
  }
}
