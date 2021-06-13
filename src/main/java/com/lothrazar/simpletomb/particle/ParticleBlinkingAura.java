package com.lothrazar.simpletomb.particle;

import com.lothrazar.simpletomb.helper.WorldHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBlinkingAura extends Particle {

  private final int colorMinR;
  private final int colorMinG;
  private final int colorMinB;
  private final int colorMaxR;
  private final int colorMaxG;
  private final int colorMaxB;

  public ParticleBlinkingAura(
      World world,
      double x,
      double y,
      double z,
      int colorMinR,
      int colorMinG,
      int colorMinB,
      int colorMaxR,
      int colorMaxG,
      int colorMaxB) {
    super(world, x, y, z, 0.0D, 0.0D, 0.0D);
    SpriteTypes.FAKE_FOG.setTexture(this);
    this.motionX = 0.0D;
    this.motionY = 0.0D;
    this.motionZ = 0.0D;
    this.setAlphaF(0.15F);
    this.colorMinR = colorMinR;
    this.colorMinG = colorMinG;
    this.colorMinB = colorMinB;
    this.colorMaxR = colorMaxR;
    this.colorMaxG = colorMaxG;
    this.colorMaxB = colorMaxB;
    this.setRBGColorF(
        colorMinR / 255.0F,
        colorMinG / 255.0F,
        colorMinB / 255.0F);
    this.multipleParticleScaleBy(WorldHelper.getRandom(world.rand, 0.6F, 0.8F));
    this.setMaxAge(7);
    this.canCollide = false;
  }

  private void updateCurrentColor() {
    this.setRBGColorF(
        WorldHelper.getRandom(world.rand, this.colorMinR, this.colorMaxR) / 255.0F,
        WorldHelper.getRandom(world.rand, this.colorMinG, this.colorMaxG) / 255.0F,
        WorldHelper.getRandom(world.rand, this.colorMinB, this.colorMaxB) / 255.0F);
  }

  @Override
  public void onUpdate() {
    if (this.particleAge++ >= this.particleMaxAge) {
      this.setExpired();
    }
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;
    this.updateCurrentColor();
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
