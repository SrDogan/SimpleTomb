package com.lothrazar.simpletomb.particle;

import com.lothrazar.simpletomb.helper.WorldHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleGraveSmoke extends Particle {

  protected final int halfMaxAge;
  protected final float alphaStep;

  public ParticleGraveSmoke(World world, double x, double y, double z, double mX, double mY, double mZ) {
    super(world, x, y, z, mX, mY, mZ);
    SpriteTypes.FAKE_FOG.setTexture(this);
    this.motionX = mX;
    this.motionY = mY;
    this.motionZ = mZ;
    this.particleAlpha = 0.0F;
    this.multipleParticleScaleBy(3.0F);
    this.setMaxAge(80);
    this.halfMaxAge = this.particleMaxAge / 2;
    this.alphaStep = 0.08F / this.halfMaxAge;
    this.canCollide = false;
    float[] colors = WorldHelper.getRGBColor3F(16777215);
    this.setRBGColorF(colors[0], colors[1], colors[2]);
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    this.setAlphaF(
        MathHelper.clamp(
            this.particleAge < this.halfMaxAge
                ? this.particleAge
                : this.particleMaxAge - this.particleAge,
            0,
            this.halfMaxAge) *
            this.alphaStep);
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
