package com.lothrazar.simpletomb.command;

import com.lothrazar.simpletomb.ModTomb;
import com.lothrazar.simpletomb.data.DeathHelper;
import com.lothrazar.simpletomb.data.LocationBlockPos;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandGetGrave implements ITombCommand {

  @Override
  public boolean needsOp() {
    return false;
  }

  @Override
  public String getName() {
    return "latest";
  }

  @Override
  public int execute(CommandContext<CommandSource> ctx, List<String> arguments, PlayerEntity player) {
    LocationBlockPos graveLoc = DeathHelper.INSTANCE.getLastGrave(player);
    TranslationTextComponent msg = new TranslationTextComponent("" + graveLoc.toString());
    if (graveLoc == LocationBlockPos.ORIGIN) {
      msg = new TranslationTextComponent(ModTomb.MODID + ".commands." + getName() + ".null");
    }
    msg.setStyle(Style.EMPTY.setFormatting(TextFormatting.GOLD));
    player.sendMessage(msg, player.getUniqueID());
    return 0;
  }
}
