package com.lothrazar.simpletomb.block;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.lothrazar.simpletomb.data.MessageType;
import com.lothrazar.simpletomb.helper.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTomb extends TileEntitySpecialRenderer<TileEntityTomb> {

  private static final String TIME_FORMAT = "HH:mm:ss";
  private static final String DATE_FORMAT = "yyyy/MM/dd";
  private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(
      0,
      0,
      64,
      32);

  @SuppressWarnings("incomplete-switch")
  @Override
  public void render(
      TileEntityTomb te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    //      ConfigTombstone.client.graveSkinRule ==
    //      ConfigTombstone.CatClient.GraveSkinRule.FORCE_HALLOWEEN ||
    //      Helper.isDateAroundHalloween(te.getWorld()) &&
    //      ConfigTombstone.client.graveSkinRule !=
    //      ConfigTombstone.CatClient.GraveSkinRule.FORCE_NORMAL;
    int rotationIndex;
    float value;
    float modY;
    if (!te.hasOwner()) {
      return;
    }
    IBlockState knownState = te.getWorld().getBlockState(te.getPos());
    if (!(knownState.getBlock() instanceof BlockTomb)) {
      return;
    }
    EnumFacing facing = knownState.getValue(BlockTomb.FACING);
    BlockTomb grave = (BlockTomb) knownState.getBlock();
    if (true) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      GlStateManager.enableAlpha();
      float modX = 0.5f;
      float modY2 = 0.07f;
      float modZ = 0.5f;
      block0: switch (grave.getGraveType()) {
        case GRAVE_NORMAL: {
          modY2 += 0.35f;
          break;
        }
        case GRAVE_CROSS: {
          switch (facing) {
            case SOUTH: {
              modZ -= 0.3f;
              modX -= 0.4f;
              break block0;
            }
            case WEST: {
              modX += 0.3f;
              modZ -= 0.4f;
              break block0;
            }
            case EAST: {
              modX -= 0.3f;
              modZ += 0.4f;
              break block0;
            }
          }
          modZ += 0.3f;
          modX += 0.4f;
          break;
        }
        case GRAVE_TOMB: {
          modY2 += 0.6f;
          break;
        }
        default: {
          modY2 += 0.1f;
        }
      }
      Minecraft.getMinecraft().renderEngine.bindTexture(
          new ResourceLocation("minecraft", "textures/entity/skeleton/skeleton.png"));
      //      TextureHelper.SKELETON_HEAD.bindTexture();
      GlStateManager.translate(
          (float) x + modX,
          (float) y + modY2,
          (float) z + modZ);
      GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
      GlStateManager.rotate(facing.getHorizontalAngle(), 0.0f, 1.0f, 0.0f);
      if (grave.getGraveType() == ModelTomb.GRAVE_NORMAL ||
          grave.getGraveType() == ModelTomb.GRAVE_SIMPLE) {
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.scale(0.2f, 0.2f, 0.2f);
        ItemStack pumpkin = new ItemStack(
            WorldHelper.isNight(te.getWorld())
                ? Blocks.LIT_PUMPKIN
                : Blocks.PUMPKIN);
        Minecraft.getMinecraft()
            .getRenderItem()
            .renderItem(pumpkin, TransformType.NONE);
      }
      else {
        this.skeletonHead.render(null, partialTicks,
            0.0f, 0.0f, 0.0f, 0.0f, 0.015625f);
      }
      GlStateManager.popMatrix();
    } ////////////////////////
    if (!te.hasOwner()) {
      return;
    }
    float modX = 0.5f;
    float modZ = 0.5f;
    switch (grave.getGraveType()) {
      case GRAVE_CROSS: {
        value = 0.25f;
        modY = 0.06375f;
        break;
      }
      case GRAVE_NORMAL: {
        value = 0.12625f;
        modY = 0.5f;
        break;
      }
      case GRAVE_SIMPLE: {
        value = 0.18875f;
        modY = 0.4f;
        break;
      }
      default: {
        value = 0.56375f;
        modY = 0.25f;
      }
    }
    boolean is_cross = grave.getGraveType() == ModelTomb.GRAVE_CROSS;
    switch (facing) {
      case SOUTH: {
        rotationIndex = 0;
        if (is_cross) {
          modZ = 1.0f - value;
          break;
        }
        modZ = value;
        break;
      }
      case WEST: {
        rotationIndex = -1;
        if (is_cross) {
          modX = value;
          break;
        }
        modX = 1.0f - value;
        break;
      }
      case EAST: {
        rotationIndex = 1;
        if (is_cross) {
          modX = 1.0f - value;
          break;
        }
        modX = value;
        break;
      }
      default: {
        rotationIndex = 2;
        modZ = is_cross ? value : 1.0f - value;
      }
    }
    GlStateManager.enableRescaleNormal();
    GlStateManager.pushMatrix();
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    GlStateManager.disableLighting();
    GlStateManager.translate(
        (float) x + modX,
        (float) y + modY,
        (float) z + modZ);
    GlStateManager.rotate(90.0f * rotationIndex, 0.0f, 1.0f, 0.0f);
    if (is_cross) {
      GlStateManager.rotate(90.0f, -1.0f, 0.0f, 0.0f);
    }
    GlStateManager.depthMask(false);
    FontRenderer fontRender = this.getFontRenderer();
    GlStateManager.pushMatrix();
    float scale = 0.007f;
    GlStateManager.scale(scale, -scale, scale);
    this.showString(TextFormatting.BOLD +
        MessageType.MESSAGE_RIP.getClientTranslation(new Object[0]), fontRender,
        0, 2962496 + -16777216);
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    scale = 0.005f;
    GlStateManager.scale(scale, -scale, scale);
    this.showString(TextFormatting.BOLD + te.getOwnerName(), fontRender,
        9, 5991302 + -16777216);
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    scale = 0.004f;//: 0.004f;
    GlStateManager.scale(scale, -scale, scale);
    int textColor = 2962496 + -16777216;
    long days = te.timer / 24000;
    String dateStringmc = MessageType.MESSAGE_DAY.getClientTranslation(days);
    this.showString(
        dateStringmc,
        fontRender,
        20,
        textColor);
    //    }
    //    else {
    Date date = new Date(te.getOwnerDeathTime());
    String dateString = new SimpleDateFormat(DATE_FORMAT).format(date);
    String timeString = " " + new SimpleDateFormat(TIME_FORMAT).format(date);
    this.showString(TextFormatting.BOLD + dateString, fontRender,
        36, textColor);
    this.showString(TextFormatting.BOLD + timeString, fontRender,
        46, textColor);
    //    }
    GlStateManager.popMatrix();
    GlStateManager.depthMask(true);
    GlStateManager.popMatrix();
    GlStateManager.enableLighting();
  }

  private void showString(String content, FontRenderer fontRenderer, int posY, int color) {
    fontRenderer.drawString(content,
        (-fontRenderer.getStringWidth(content)) / 2.0F,
        posY - 30,
        color,
        true);
  }
}
