package com.lothrazar.simpletomb.command;

import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

public interface ITombCommand {

  public boolean needsOp();

  public String getName();

  public int execute(CommandContext<CommandSource> ctx, List<String> arguments, PlayerEntity player);
}
