package com.lothrazar.simpletomb.item;

import java.util.List;
import javax.annotation.Nullable;
import com.lothrazar.simpletomb.ConfigTomb;
import com.lothrazar.simpletomb.ModTomb;
import com.lothrazar.simpletomb.TombRegistry;
import com.lothrazar.simpletomb.data.LocationBlockPos;
import com.lothrazar.simpletomb.data.MessageType;
import com.lothrazar.simpletomb.helper.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemGraveKey extends Item {

  private static final String TOMB_POS = "tombPos";
  protected static final String name = "grave_key";

  public ItemGraveKey() {
    super();
    this.setMaxStackSize(1);
    this.setRegistryName(name);
    this.setTranslationKey(name);
    //    this.setCreativeTab(null);
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    return TextFormatting.GOLD + super.getItemStackDisplayName(stack);
  }

  @Override
  public void onUsingTick(ItemStack stack, EntityLivingBase entity, int timeLeft) {
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      LocationBlockPos location = this.getTombPos(stack);
      if (location == null || location.isOrigin()
          || location.dim != player.dimension) {
        return;
      }
      double distance = location.getDistance(player.getPosition());
      boolean canTp = false;
      if (player.isCreative()) {
        canTp = ConfigTomb.INSTANCE.tpToGraveCreative;
      }
      else {
        canTp = (ConfigTomb.INSTANCE.distanceUntilTpSurvival > 0 &&
            distance < ConfigTomb.INSTANCE.distanceUntilTpSurvival)
            || ConfigTomb.INSTANCE.distanceUntilTpSurvival == -1;//-1 is magic value for ANY DISTANCE IS OK
      }
      if (canTp) {
        //ok so do particles and then Jump when time runs out
        if (timeLeft <= 1) {
          //teleport happens here
          BlockPos pos = location.toBlockPos();
          player.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
        }
        else if (entity.world.isRemote) {
          //not done, and can TP
          ModTomb.PROXY.produceParticleCasting(entity, p -> !p.isHandActive());
        }
      }
    }
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 86;
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BOW;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);
    playerIn.setActiveHand(handIn);
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
  }

  @Override
  public String getTranslationKey() {
    return ModTomb.MODID + ".item." + name;
  }

  @Override
  public String getTranslationKey(ItemStack stack) {
    return this.getTranslationKey();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
    if (GuiScreen.isShiftKeyDown()) {
      LocationBlockPos location = this.getTombPos(stack);
      //      this.addItemPosition(list, this.getTombPos(stack));
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (player != null && !location.isOrigin()) {
        BlockPos pos = player.getPosition();
        int distance = (int) location.getDistance(pos);
        list.add(TextFormatting.DARK_PURPLE +
            MessageType.MESSAGE_DISTANCE.getClientTranslation(
                distance,
                location.x, location.y, location.z, location.dim));
      }
    }
    super.addInformation(stack, world, list, flag);
  }

  public void addItemPosition(List<String> list, LocationBlockPos location) {
    EntityPlayerSP player = Minecraft.getMinecraft().player;
    if (player != null && !location.isOrigin()) {
      BlockPos pos = player.getPosition();
      int distance = (int) location.toBlockPos().getDistance(pos.getX(), pos.getY(), pos.getZ());
      list.add(TextFormatting.DARK_PURPLE +
          MessageType.MESSAGE_DISTANCE.getClientTranslation(
              distance,
              location.x, location.y, location.z, location.dim));
    }
  }

  public boolean setTombPos(ItemStack stack, LocationBlockPos location) {
    if (stack.getItem() == this && !location.isOrigin()) {
      NBTHelper.setLocation(stack, TOMB_POS, location);
      return true;
    }
    return false;
  }

  public LocationBlockPos getTombPos(ItemStack stack) {
    return stack.getItem() == this
        ? NBTHelper.getLocation(stack, TOMB_POS)
        : LocationBlockPos.ORIGIN;
  }

  public boolean removeKeyForGraveInInventory(EntityPlayer player, LocationBlockPos graveLoc) {
    IItemHandler itemHandler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    if (itemHandler != null) {
      for (int i = 0; i < itemHandler.getSlots(); ++i) {
        ItemStack stack = itemHandler.getStackInSlot(i);
        if (stack.getItem() == TombRegistry.grave_key &&
            TombRegistry.grave_key.getTombPos(stack).equals(graveLoc)) {
          itemHandler.extractItem(i, 1, false);
          return true;
        }
      }
    }
    return false;
  }

  public int countKeyInInventory(EntityPlayer player) {
    return (int) player.inventory.mainInventory.stream()
        .filter(stack -> stack.getItem() == TombRegistry.grave_key)
        .count();
  }
  //teleport to grave?
  // 
  //    Location location = this.getTombPos(stack);
  //    EntityPlayer newPlayer = Helper.teleportToGrave(player, location);
}
