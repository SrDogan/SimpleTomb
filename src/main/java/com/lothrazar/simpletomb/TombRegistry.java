package com.lothrazar.simpletomb;

import com.lothrazar.simpletomb.block.BlockTomb;
import com.lothrazar.simpletomb.block.ModelTomb;
import com.lothrazar.simpletomb.item.ItemGraveKey;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = ModTomb.MODID)
public class TombRegistry {

  public static final BlockTomb grave_simple = new BlockTomb(ModelTomb.GRAVE_SIMPLE);
  public static final BlockTomb grave_normal = new BlockTomb(ModelTomb.GRAVE_NORMAL);
  public static final BlockTomb grave_cross = new BlockTomb(ModelTomb.GRAVE_CROSS);
  public static final BlockTomb tombstone = new BlockTomb(ModelTomb.GRAVE_TOMB);
  public static final ItemGraveKey grave_key = new ItemGraveKey();
  public static BlockTomb[] graves = new BlockTomb[] {
      grave_simple,
      grave_normal,
      grave_cross,
      tombstone,
  };

  @SubscribeEvent
  public static void registerBlocks(Register<Block> event) {
    event.getRegistry().registerAll(graves);
  }

  @SubscribeEvent
  public static void registerItems(Register<Item> event) {
    event.getRegistry().register(grave_key);
  }

  @SubscribeEvent
  public static void renderItems(ModelRegistryEvent event) {
    ModTomb.PROXY.registerModels();
  }
}
