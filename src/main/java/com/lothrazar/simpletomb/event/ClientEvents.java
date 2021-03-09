package com.lothrazar.simpletomb.event;

import com.lothrazar.simpletomb.ModTomb;
import com.lothrazar.simpletomb.TombRegistry;
import com.lothrazar.simpletomb.data.LocationBlockPos;
import com.lothrazar.simpletomb.helper.WorldHelper;
import com.lothrazar.simpletomb.particle.ParticleGraveSmoke;
import com.lothrazar.simpletomb.particle.ParticleGraveSoul;
import com.lothrazar.simpletomb.particle.ParticleRotatingSmoke;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = ModTomb.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientEvents {

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
    ParticleManager r = Minecraft.getInstance().particles;
    r.registerFactory(TombRegistry.GRAVE_SMOKE, ParticleGraveSmoke.Factory::new);
    r.registerFactory(TombRegistry.ROTATING_SMOKE, ParticleRotatingSmoke.Factory::new);
    r.registerFactory(TombRegistry.SOUL, ParticleGraveSoul.Factory::new);
  }

  @SubscribeEvent
  public void render(RenderWorldLastEvent event) {
    ClientPlayerEntity player = Minecraft.getInstance().player;
    if (player != null && player.world != null) {
      ItemStack stack = player.getHeldItemMainhand();
      if (stack.getItem() == TombRegistry.GRAVE_KEY) {
        LocationBlockPos location = TombRegistry.GRAVE_KEY.getTombPos(stack);
        if (location != null && !location.isOrigin() &&
            location.dim.equalsIgnoreCase(WorldHelper.dimensionToString(player.world)) &&
            World.isValid(location.toBlockPos())) {
          createBox(event.getMatrixStack(), location.x, location.y, location.z, 1.0D);
        }
      }
    }
  }

  @SuppressWarnings("deprecation")
  private static void createBox(MatrixStack matrixStack, double x, double y, double z, double offset) {
    Minecraft mc = Minecraft.getInstance();
    RenderSystem.disableTexture();
    RenderSystem.disableBlend();
    RenderSystem.disableDepthTest();
    RenderSystem.pushMatrix();
    Vector3d viewPosition = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
    long c = (System.currentTimeMillis() / 15L) % 360L;
    float[] color = WorldHelper.getHSBtoRGBF(c / 360f, 1f, 1f);
    matrixStack.push();
    // get a closer pos if too far
    Vector3d vec = new Vector3d(x, y, z).subtract(viewPosition);
    if (vec.distanceTo(Vector3d.ZERO) > 200d) { // could be 300
      vec = vec.normalize().scale(200d);
      x += vec.x;
      y += vec.y;
      z += vec.z;
    }
    x -= viewPosition.getX();
    y -= viewPosition.getY();
    z -= viewPosition.getZ();
    RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder renderer = tessellator.getBuffer();
    renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
    RenderSystem.color4f(color[0], color[1], color[2], 1f);
    RenderSystem.lineWidth(2.5f);
    renderer.pos(x, y, z).endVertex();
    renderer.pos(x + offset, y, z).endVertex();
    renderer.pos(x, y, z).endVertex();
    renderer.pos(x, y + offset, z).endVertex();
    renderer.pos(x, y, z).endVertex();
    renderer.pos(x, y, z + offset).endVertex();
    renderer.pos(x + offset, y + offset, z + offset).endVertex();
    renderer.pos(x, y + offset, z + offset).endVertex();
    renderer.pos(x + offset, y + offset, z + offset).endVertex();
    renderer.pos(x + offset, y, z + offset).endVertex();
    renderer.pos(x + offset, y + offset, z + offset).endVertex();
    renderer.pos(x + offset, y + offset, z).endVertex();
    renderer.pos(x, y + offset, z).endVertex();
    renderer.pos(x, y + offset, z + offset).endVertex();
    renderer.pos(x, y + offset, z).endVertex();
    renderer.pos(x + offset, y + offset, z).endVertex();
    renderer.pos(x + offset, y, z).endVertex();
    renderer.pos(x + offset, y, z + offset).endVertex();
    renderer.pos(x + offset, y, z).endVertex();
    renderer.pos(x + offset, y + offset, z).endVertex();
    renderer.pos(x, y, z + offset).endVertex();
    renderer.pos(x + offset, y, z + offset).endVertex();
    renderer.pos(x, y, z + offset).endVertex();
    renderer.pos(x, y + offset, z + offset).endVertex();
    tessellator.draw();
    matrixStack.pop();
    RenderSystem.popMatrix();
    RenderSystem.lineWidth(1f);
    RenderSystem.enableDepthTest();
    RenderSystem.enableBlend();
    RenderSystem.enableTexture();
    //    RenderSystem.color4f(1f, 1f, 1f, 1f);
  }
}
