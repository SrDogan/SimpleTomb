package com.lothrazar.simpletomb.event;

import com.lothrazar.simpletomb.TombRegistry;
import com.lothrazar.simpletomb.data.LocationBlockPos;
import com.lothrazar.simpletomb.helper.WorldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEvents {

  @SubscribeEvent
  public void render(RenderWorldLastEvent event) {
    EntityPlayerSP player = Minecraft.getMinecraft().player;
    if (player != null && player.world != null) {
      ItemStack stack = player.getHeldItemMainhand();
      if (stack.getItem() == TombRegistry.grave_key) {
        LocationBlockPos location = TombRegistry.grave_key.getTombPos(stack);
        if (location != null && !location.isOrigin() &&
            location.dim == player.world.provider.getDimension() &&
            player.world.isValid(location.toBlockPos())) {
          createBox(location.x, location.y, location.z, 1.0D);
        }
      }
    }
  }

  private static void createBox(double x, double y, double z, double offset) {
    Minecraft mc = Minecraft.getMinecraft();
    GlStateManager.disableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.disableDepth();
    Vec3d location = (new Vec3d(x, y, z)).subtract(
        mc.getRenderManager().viewerPosX,
        mc.getRenderManager().viewerPosY,
        mc.getRenderManager().viewerPosZ);
    if (location.distanceTo(Vec3d.ZERO) > 200.0D) {
      location = location.normalize().scale(200.0D);
    }
    x = location.x;
    y = location.y;
    z = location.z;
    long c = System.currentTimeMillis() / 15L % 360L;
    float[] color = WorldHelper.getHSBtoRGBF(c / 360.0F, 1.0F, 1.0F);
    RenderGlobal.drawBoundingBox(
        x,
        y,
        z,
        x + offset,
        y + offset,
        z + offset,
        color[0],
        color[1],
        color[2],
        1.0F);
    GlStateManager.enableDepth();
    GlStateManager.enableBlend();
    GlStateManager.enableTexture2D();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }
}
