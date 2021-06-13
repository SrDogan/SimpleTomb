package com.lothrazar.simpletomb.helper;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemHandlerHelper;

public class EntityHelper {

  public static final String NBT_PLAYER_PERSISTED = "PlayerPersisted";

  public static boolean autoequip(ItemStack stack, EntityPlayer player) {
    ResourceLocation registryName = stack.getItem().getRegistryName();
    if (!stack.isEmpty() && registryName != null) {
      if (EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack) > 0) {
        return false;
      }
      if (stack.getMaxStackSize() == 1) {
        if (player.getHeldItemOffhand().isEmpty()) {
          if (stack.getItem().isShield(stack, player) &&
              player.replaceItemInInventory(99, stack.copy())) {
            return true;
          }
        }
        EntityEquipmentSlot slot = stack.getItem().getEquipmentSlot(stack);
        boolean isElytra = false;
        if (slot == null) {
          if (stack.getItem() instanceof ItemArmor) {
            slot = ((ItemArmor) stack.getItem()).armorType;
          }
          else {
            if (!(stack.getItem() instanceof ItemElytra)) {
              return false;
            }
            slot = EntityEquipmentSlot.CHEST;
            isElytra = true;
          }
        }
        else if (slot == EntityEquipmentSlot.CHEST) {
          isElytra = stack.getItem() instanceof ItemElytra;
        }
        int slotId = slot.getIndex();
        ItemStack stackInSlot = player.inventory.armorInventory.get(slotId);
        if (stackInSlot.isEmpty()) {
          player.inventory.armorInventory.set(slotId, stack.copy());
          return true;
        }
        if (slot != EntityEquipmentSlot.CHEST) {
          return false;
        }
        //        boolean equipElytraInPriority = true;//DeathHandler.INSTANCE.getOptionEquipElytraInPriority(player.getUniqueID());
        //        boolean canEquip = isElytra(stackInSlot)
        //            ? !isElytra && !equipElytraInPriority
        //            : isElytra && equipElytraInPriority;
        if (isElytra) {
          ItemHandlerHelper.giveItemToPlayer(player, stackInSlot.copy());
          player.inventory.armorInventory.set(slotId, stack.copy());
          return true;
        }
      }
      return false;
    }
    return false;
  }

  public static boolean isValidPlayer(@Nullable Entity entity) {
    return entity instanceof EntityPlayer && !(entity instanceof FakePlayer);
  }

  public static boolean isValidPlayerMP(@Nullable Entity entity) {
    return isValidPlayer(entity) && !entity.world.isRemote;
  }

  public static NBTTagCompound getPersistentTag(EntityPlayer player) {
    NBTTagCompound persistentData = player.getEntityData();
    NBTTagCompound persistentTag;
    if (persistentData.hasKey(NBT_PLAYER_PERSISTED)) {
      persistentTag = (NBTTagCompound) persistentData.getTag(NBT_PLAYER_PERSISTED);
      return persistentTag;
    }
    else {
      persistentTag = new NBTTagCompound();
      persistentData.setTag(NBT_PLAYER_PERSISTED, persistentTag);
      return persistentTag;
    }
  }
}
