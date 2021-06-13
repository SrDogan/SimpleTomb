package com.lothrazar.simpletomb.particle;

import java.util.EnumMap;
import com.lothrazar.simpletomb.ModTomb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = ModTomb.MODID, value = { Side.CLIENT })
@SideOnly(Side.CLIENT)
public class ParticleRegistry {

  private static final EnumMap<SpriteTypes, TextureAtlasSprite> sprites = new EnumMap<>(SpriteTypes.class);

  @SubscribeEvent
  public static void onTextureStitch(Pre event) {
    for (SpriteTypes spriteType : SpriteTypes.values()) {
      sprites.put(spriteType, event.getMap().registerSprite(
          new ResourceLocation(ModTomb.MODID, "particles/" + spriteType.name().toLowerCase())));
    }
  }

  public static TextureAtlasSprite getSprite(
      SpriteTypes particleType) {
    TextureAtlasSprite sprite = sprites.get(particleType);
    return sprite == null
        ? Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("missingno")
        : sprite;
  }
}
