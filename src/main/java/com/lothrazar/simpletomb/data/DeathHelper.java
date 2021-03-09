package com.lothrazar.simpletomb.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;

public class DeathHelper {

  public static final DeathHelper INSTANCE = new DeathHelper();
  private final Map<UUID, LocationBlockPos> lastGraveList = new HashMap<>();

  public LocationBlockPos getLastGrave(PlayerEntity player) {
    return lastGraveList.getOrDefault(player.getGameProfile().getId(), LocationBlockPos.ORIGIN);
  }

  public LocationBlockPos deleteLastGrave(PlayerEntity player) {
    return lastGraveList.remove(player.getGameProfile().getId());
  }

  public LocationBlockPos putLastGrave(PlayerEntity player, LocationBlockPos loc) {
    return lastGraveList.put(player.getGameProfile().getId(), loc);
  }
}
