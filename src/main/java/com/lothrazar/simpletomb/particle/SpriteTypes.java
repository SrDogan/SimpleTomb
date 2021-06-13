package com.lothrazar.simpletomb.particle;

import net.minecraft.client.particle.Particle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SpriteTypes {

  FAKE_FOG, GHOST, CASTING, AURA;

  public void setTexture(Particle particle) {
    particle.setParticleTexture(ParticleRegistry.getSprite(this));
  }
}