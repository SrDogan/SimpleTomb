package com.lothrazar.simpletomb.data;

import com.lothrazar.simpletomb.ModTomb;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum MessageType {

  MESSAGE_FAIL_TO_PLACE_GRAVE("message.fail_to_place_grave"),
  //open grave
  MESSAGE_OPEN_GRAVE_SUCCESS("message.open_grave.success"), MESSAGE_OPEN_GRAVE_NEED_OWNER("message.open_grave.need_owner"),
  //
  MESSAGE_NO_LOOT_FOR_GRAVE("message.no_loot_for_grave"), MESSAGE_NO_PLACE_FOR_GRAVE("message.no_place_for_grave"),
  //rendered as text in world on gravestone
  MESSAGE_DAY("message.day"), MESSAGE_RIP("message.rip"),
  //logs or tooltips
  MESSAGE_NEW_GRAVE("message.new_grave"), MESSAGE_JOURNEYMAP("message.journeymap"), MESSAGE_DISTANCE("message.distance");

  public static final Style MESSAGE_SPECIAL;
  static {
    MESSAGE_SPECIAL = (new Style()).setColor(TextFormatting.GOLD).setItalic(false).setBold(false);
  }
  private final String key;

  MessageType(String key) {
    this.key = key;
  }

  public String getKey() {
    return ModTomb.MODID + "." + this.key;
  }

  public ITextComponent getTranslationWithStyle(Style style, Object... params) {
    return new TextComponentTranslation(getKey(), params).setStyle(style);
  }

  @SideOnly(Side.CLIENT)
  public String getClientTranslation(Object... params) {
    return params.length == 0
        ? I18n.format(getKey())
        : I18n.format(getKey(), params);
  }

  @SuppressWarnings("deprecation")
  public String getServerTranslation(Object... params) {
    return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this.getKey(), params);
  }

  public void sendSpecialMessage(ICommandSender sender, Object... params) {
    sender.sendMessage(this.getTranslationWithStyle(MESSAGE_SPECIAL, params));
  }
}
