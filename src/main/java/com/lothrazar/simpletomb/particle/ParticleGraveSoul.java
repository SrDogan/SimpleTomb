package com.lothrazar.simpletomb.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleGraveSoul extends Particle {

  //  private static final TextureAtlasSprite commonTexture =;
  private final double radius;
  private final double centerX;
  private final double centerZ;

  public ParticleGraveSoul(World world, double x, double y, double z, double radius) {
    super(world, x, y + 0.85D, z);
    //    this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks()
    //        .getAtlasSprite(ModTombstone.MODID + ":particles/ghost"));
    SpriteTypes.GHOST.setTexture(this);
    this.particleMaxAge = 100;
    this.particleScale = 0.3F;
    this.centerX = x + 0.5D;
    this.centerZ = z + 0.5D;
    this.radius = radius;
    this.updatePosition();
    this.particleAlpha = 0.7F;
    this.particleRed = 0.31764707F;
    this.particleGreen = 0.09803922F;
    this.particleBlue = 0.54509807F;
    this.canCollide = false;
  }

  private void updatePosition() {
    double ratio = (double) this.particleAge / (double) this.particleMaxAge;
    this.motionX = this.motionY = this.motionZ = 0.0D;
    this.prevPosX = this.posX = this.centerX + this.radius * Math.cos(6.283185307179586D * ratio);
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ = this.centerZ + this.radius * Math.sin(6.283185307179586D * ratio);
  }

  @Override
  public void onUpdate() {
    if (this.particleAge++ >= this.particleMaxAge) {
      this.setExpired();
    }
    this.updatePosition();
    ParticleBlinkingAura particle = new ParticleBlinkingAura(this.world, this.posX, this.posY + 0.02D, this.posZ,
        88,
        25,
        139,
        190,
        25,
        159);
    Minecraft.getMinecraft().effectRenderer.addEffect(particle);
  }

  @Override
  public boolean shouldDisableDepth() {
    return false;
  }

  @Override
  public int getFXLayer() {
    return 1;
  }
}
