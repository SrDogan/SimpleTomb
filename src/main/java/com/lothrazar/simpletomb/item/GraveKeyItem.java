package com.lothrazar.simpletomb.item;

import com.lothrazar.simpletomb.ConfigTomb;
import com.lothrazar.simpletomb.TombRegistry;
import com.lothrazar.simpletomb.block.BlockTomb;
import com.lothrazar.simpletomb.data.LocationBlockPos;
import com.lothrazar.simpletomb.data.MessageType;
import com.lothrazar.simpletomb.helper.NBTHelper;
import com.lothrazar.simpletomb.helper.WorldHelper;
import com.lothrazar.simpletomb.proxy.ClientUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GraveKeyItem extends Item {

  private static final String TOMB_POS = "tombPos";

  public GraveKeyItem(Item.Properties properties) {
    super(properties.maxStackSize(1));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public ITextComponent getName() {
    return new TranslationTextComponent(this.getTranslationKey()).mergeStyle(TextFormatting.GOLD);
  }

  @Override
  public void onUsingTick(ItemStack stack, LivingEntity entity, int timeLeft) {
    if (entity instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) entity;
      LocationBlockPos location = this.getTombPos(stack);
      if (location == null || location.isOrigin()
          || location.dim.equalsIgnoreCase(WorldHelper.dimensionToString(player.world)) == false) {
        return;
      }
      double distance = location.getDistance(player.getPosition());
      boolean canTp = false;
      if (player.isCreative()) {
        canTp = ConfigTomb.TPCREATIVE.get();
      }
      else {
        canTp = (ConfigTomb.TPSURVIVAL.get() > 0 &&
            distance < ConfigTomb.TPSURVIVAL.get()) || ConfigTomb.TPSURVIVAL.get() == -1;
        //-1 is magic value for ANY DISTANCE IS OK
      }
      if (canTp) {
        if (timeLeft <= 1) {
          //teleport happens here
          BlockPos pos = location.toBlockPos();
          player.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
        }
        else if (entity.world.isRemote) {
          //not done, and can TP
          ClientUtils.produceParticleCasting(entity, p -> !p.isHandActive());
        }
      }
    }
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    return 86;
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    if (ConfigTomb.KEYOPENONUSE.get()) {
      BlockPos pos = context.getPos();
      PlayerEntity player = context.getPlayer();
      if (player.getHeldItem(context.getHand()).getItem() == TombRegistry.GRAVE_KEY) {
        BlockState state = context.getWorld().getBlockState(pos);
        if (state.getBlock() instanceof BlockTomb) {
          //open me
          BlockTomb.activatePlayerGrave(player.world, pos, state, player);
          return ActionResultType.SUCCESS;
        }
      }
    }
    return ActionResultType.PASS;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);
    playerIn.setActiveHand(handIn);
    return ActionResult.resultSuccess(itemstack);
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
    if (Screen.hasShiftDown()) {
      LocationBlockPos location = this.getTombPos(stack);
      //      this.addItemPosition(list, this.getTombPos(stack));
      PlayerEntity player = Minecraft.getInstance().player;
      if (player != null && !location.isOrigin()) {
        BlockPos pos = player.getPosition();
        int distance = (int) location.getDistance(pos);
        list.add(new TranslationTextComponent(MessageType.MESSAGE_DISTANCE.getKey(),
            distance, location.x, location.y, location.z, location.dim)
                .mergeStyle(TextFormatting.DARK_PURPLE));
      }
    }
    super.addInformation(stack, world, list, flag);
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

  /**
   * Look for any key that matches this Location and remove that key from player
   */
  public boolean removeKeyForGraveInInventory(PlayerEntity player, LocationBlockPos graveLoc) {
    IItemHandler itemHandler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
    if (itemHandler != null) {
      for (int i = 0; i < itemHandler.getSlots(); ++i) {
        ItemStack stack = itemHandler.getStackInSlot(i);
        if (stack.getItem() == TombRegistry.GRAVE_KEY &&
            TombRegistry.GRAVE_KEY.getTombPos(stack).equals(graveLoc)) {
          itemHandler.extractItem(i, 1, false);
          return true;
        }
      }
    }
    return false;
  }

  /**
   * How many keys, ignoring data. casts long to int
   */
  public int countKeyInInventory(PlayerEntity player) {
    return (int) player.inventory.mainInventory.stream()
        .filter(stack -> stack.getItem() == TombRegistry.GRAVE_KEY)
        .count();
  }
}
