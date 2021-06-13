package com.lothrazar.simpletomb.helper;

import javax.annotation.Nullable;
import com.lothrazar.simpletomb.data.LocationBlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class NBTHelper {

  private static NBTTagCompound getOrCreateTag(ItemStack stack) {
    if (stack.getTagCompound() == null) {
      stack.setTagCompound(new NBTTagCompound());
    }
    return stack.getTagCompound();
  }

  private static void setBlockPos(NBTTagCompound tag, String keyName, BlockPos keyValue) {
    tag.setInteger(keyName + "X", keyValue.getX());
    tag.setInteger(keyName + "Y", keyValue.getY());
    tag.setInteger(keyName + "Z", keyValue.getZ());
  }

  private static BlockPos getBlockPos(@Nullable NBTTagCompound tag, String keyName) {
    return (tag != null &&
        tag.hasKey(keyName + "X") &&
        tag.hasKey(keyName + "Y") &&
        tag.hasKey(keyName + "Z"))
            ? new BlockPos(
                tag.getInteger(keyName + "X"),
                tag.getInteger(keyName + "Y"),
                tag.getInteger(keyName + "Z"))
            : LocationBlockPos.ORIGIN_POS;
  }

  public static ItemStack setLocation(ItemStack stack, String keyName, LocationBlockPos location) {
    setLocation(getOrCreateTag(stack), keyName, location);
    return stack;
  }

  private static NBTTagCompound setLocation(NBTTagCompound tag, String keyName, LocationBlockPos location) {
    setBlockPos(tag, keyName, location.toBlockPos());
    tag.setInteger(keyName + "D", location.dim);
    return tag;
  }

  public static LocationBlockPos getLocation(ItemStack stack, String keyName) {
    return getLocation(getOrCreateTag(stack), keyName);
  }

  private static LocationBlockPos getLocation(@Nullable NBTTagCompound tag, String keyName) {
    if (tag != null && tag.hasKey(keyName + "D")) {
      BlockPos pos = getBlockPos(tag, keyName);
      if (!pos.equals(LocationBlockPos.ORIGIN_POS)) {
        return new LocationBlockPos(pos, tag.getInteger(keyName + "D"));
      }
    }
    return LocationBlockPos.ORIGIN;
  }
}
