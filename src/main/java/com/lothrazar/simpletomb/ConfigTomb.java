package com.lothrazar.simpletomb;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = ModTomb.MODID)
@Config(modid = ModTomb.MODID, name = ModTomb.MODID, category = "")
public class ConfigTomb {

  @Name(ModTomb.MODID)
  public static final ConfigTomb INSTANCE = new ConfigTomb();

  @SubscribeEvent
  public static void onConfigChanged(OnConfigChangedEvent event) {
    if (event.getModID().equals(ModTomb.MODID)) {
      ConfigManager.sync(ModTomb.MODID, Type.INSTANCE);
    }
  }

  @Name("tomb.enabled")
  @Comment({ "Whether to handle player death at all (false will disable almost the entire mod such as spawning the grave on death)" })
  public boolean handlePlayerDeath = true;
  @Name("tomb.log_message")
  @Comment({ "Whether to log the positions of players' graves to game (server) console" })
  public boolean logPlayerGrave = true;
  @Name("tomb.extra_items")
  @Comment({ "The radius in which extra items should be hunted for and collected when a grave is spawned; set to zero (0) to disable" })
  @RangeInt(min = 0, max = 8)
  public int pickUpGroundRange = 2;
  @Name("key.given")
  @Comment({ "Whether to give a Grave Key item to the player on death.  Grave can be opened without they key, but the key will help the player locate the grave" })
  public boolean graveKeyOnDeath = true;
  @Name("key.named")
  @Comment({ "If a key is being dropped, will the player's display name be added to the tomb key item name" })
  public boolean addNameToPlayersKey = true;
  @Name("teleport.survival")
  @Comment({ "When survival player is within this (straight line calculated) distance from the tomb, they can teleport to the tomb.  Set as zero (0) to disable survival TP feature.  Set as negative one (-1) to allow survival teleportation always and ignore the distance (within dimension)  " })
  @RangeInt(min = 1, max = 64)
  public int distanceUntilTpSurvival = 16;
  @Name("teleport.creative")
  @Comment({ "If creative players can teleport to the tomb with the key, ignoring distance" })
  public boolean tpToGraveCreative = true;
}
