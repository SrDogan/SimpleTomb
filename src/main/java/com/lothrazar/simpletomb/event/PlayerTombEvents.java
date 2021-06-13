package com.lothrazar.simpletomb.event;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.apache.logging.log4j.Level;
import com.lothrazar.simpletomb.ConfigTomb;
import com.lothrazar.simpletomb.ModTomb;
import com.lothrazar.simpletomb.TombRegistry;
import com.lothrazar.simpletomb.block.BlockTomb;
import com.lothrazar.simpletomb.block.TileEntityTomb;
import com.lothrazar.simpletomb.data.LocationBlockPos;
import com.lothrazar.simpletomb.data.MessageType;
import com.lothrazar.simpletomb.helper.EntityHelper;
import com.lothrazar.simpletomb.helper.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.ExplosionEvent.Detonate;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class PlayerTombEvents {

  private static final String TB_SOULBOUND_STACKS = "tb_soulbound_stacks";
  //  private static final String TB_MAPPING_STACKS = "tb_mapping_stacks";

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerLogged(PlayerLoggedInEvent event) {
    if (EntityHelper.isValidPlayerMP(event.player)) {
      EntityPlayerMP player = (EntityPlayerMP) event.player;
      assert player.getServer() != null;
      NBTTagCompound playerData = player.getEntityData();
      NBTTagCompound persistantData;
      if (playerData.hasKey(EntityHelper.NBT_PLAYER_PERSISTED)) {
        persistantData = (NBTTagCompound) playerData.getTag(EntityHelper.NBT_PLAYER_PERSISTED);
      }
      else {
        persistantData = new NBTTagCompound();
        playerData.setTag(EntityHelper.NBT_PLAYER_PERSISTED, persistantData);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onDetonate(Detonate event) {
    event.getAffectedBlocks().removeIf(blockPos -> (event.getWorld()
        .getBlockState(blockPos).getBlock() instanceof BlockTomb));
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    if (EntityHelper.isValidPlayerMP(event.player) && !event.player.isSpectator()) {
      //      EntityHelper.syncTBCapability((EntityPlayerMP) event.player);
      NBTTagCompound persistentTag = EntityHelper.getPersistentTag(event.player);
      NBTTagList stackList = persistentTag.getTagList(TB_SOULBOUND_STACKS, 10);
      for (int i = 0; i < stackList.tagCount(); ++i) {
        ItemStack stack = new ItemStack(stackList.getCompoundTagAt(i));
        if (!stack.isEmpty()) {
          ItemHandlerHelper.giveItemToPlayer(event.player, stack);
        }
      }
      persistentTag.removeTag(TB_SOULBOUND_STACKS);
      event.player.inventoryContainer.detectAndSendChanges();
    }
  }

  private void storeSoulboundsOnBody(EntityPlayer player, List<ItemStack> keys) {
    NBTTagCompound persistentTag = EntityHelper.getPersistentTag(player);
    NBTTagList stackList = new NBTTagList();
    persistentTag.setTag(TB_SOULBOUND_STACKS, stackList);
    for (ItemStack key : keys) {
      stackList.appendTag(key.serializeNBT());
    }
    keys.clear();
  }
  //
  //  public static int hashCode(ItemStack itemStack) {
  //    int tagHash = (itemStack.getTagCompound() == null) ? 0 : itemStack.getTagCompound().hashCode();
  //    return itemStack.getItem().hashCode() + itemStack.getCount() + tagHash;
  //  }

  private void storeIntegerStorageMap(EntityPlayer player) {
    //    NBTTagCompound persistentTag = EntityHelper.getPersistentTag(player);
    //    List<String> mapped = new ArrayList<>();
    //    //    NBTTagList stackList = new NBTTagList();
    //    for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
    //      //      TB_MAPPING_STACKS
    //      ItemStack here = player.inventory.getStackInSlot(i);
    //      //      persistentTag.setInteger(TB_MAPPING_STACKS + i, hashCode(here));
    //      //      System.out.println(i + " player inventory = " + player.inventory.getStackInSlot(i));
    //      //TODO: create an ITEMIDSLOT -> MAP
    //      mapped.add(i + ":" + hashCode(here));
    //      //to remap those first
    //      //
    //      //
    //    }
    //    persistentTag.setString(TB_MAPPING_STACKS, String.join(",", mapped));
  }
  //  public static int matchingSlotForHashCode(EntityPlayer player, int hashCode) {
  //    NBTTagCompound persistentTag = EntityHelper.getPersistentTag(player);
  //    String[] stuff = persistentTag.getString(TB_MAPPING_STACKS).split(",");
  //    for (String spl : stuff) {
  //      String index = spl.split(":")[0];
  //      String localHash = spl.split(":")[1];
  //      if (hashCode == Integer.parseInt(localHash)) {
  //        return Integer.parseInt(index);
  //      }
  //    }
  //    //used by TileEntityTomb:: GiveInventory
  //    return -1;
  //  }

  @SubscribeEvent
  public void onPlayerDeath(LivingDeathEvent event) {
    if (!ConfigTomb.INSTANCE.handlePlayerDeath) {
      return;
    }
    if (event.getEntityLiving() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) event.getEntityLiving();
      storeIntegerStorageMap(player);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
  public void onPlayerDrops(PlayerDropsEvent event) {
    if (!ConfigTomb.INSTANCE.handlePlayerDeath) {
      return;
    }
    if (!EntityHelper.isValidPlayer(event.getEntityPlayer()) ||
        WorldHelper.isRuleKeepInventory(event.getEntityPlayer())) {
      return;
    }
    EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
    WorldServer world = player.getServerWorld();
    ListIterator<EntityItem> it = event.getDrops().listIterator();
    ArrayList<ItemStack> keys = new ArrayList<>();
    while (it.hasNext()) {
      EntityItem entityItem = it.next();
      if (entityItem != null && !entityItem.getItem().isEmpty()) {
        ItemStack stack = entityItem.getItem();
        if (stack.getItem() == TombRegistry.grave_key) {
          keys.add(stack.copy());
          it.remove();
        }
      }
    }
    List<EntityItem> itemsPickedUpFromGround = pickupFromGround(player, keys);
    this.storeSoulboundsOnBody(player, keys);
    boolean hasDrop = event.getDrops().size() > 0 || itemsPickedUpFromGround.size() > 0;
    if (!hasDrop) {
      MessageType.MESSAGE_NO_LOOT_FOR_GRAVE.sendSpecialMessage(player);
      return;
    }
    //    for (int i = 0; i < event.getDrops().size(); i++) {
    //      System.out.println(i + " getdrops size = " + event.getDrops().get(i).getItem());
    //    }
    BlockPos initPos = WorldHelper.getCloserValidPos(world, new BlockPos(player));
    //    LocationBlockPos spawnPos = (new SpawnHelper(world, initPos)).findSpawnPlace(true);
    LocationBlockPos spawnPos = WorldHelper.findGraveSpawn(player, initPos);
    if (spawnPos == null) {
      MessageType.MESSAGE_NO_PLACE_FOR_GRAVE.sendSpecialMessage(player);
      //            LangKey.MESSAGE_NO_PLACE_FOR_GRAVE.sendLog();
      ModTomb.LOGGER.log(Level.INFO, MessageType.MESSAGE_NO_PLACE_FOR_GRAVE.getServerTranslation());
      return;
    }
    EnumFacing facing = player.getHorizontalFacing().getOpposite();
    IBlockState state = TombRegistry.graves[world.rand.nextInt(TombRegistry.graves.length)].getDefaultState();
    state = state.withProperty(BlockTomb.FACING, facing);
    state = state.withProperty(BlockTomb.HAS_SOUL, world.rand.nextDouble() > 0.5);
    state = state.withProperty(BlockTomb.MODEL_TEXTURE, world.rand.nextInt(2));
    WorldHelper.placeNoEvent(world, spawnPos.toBlockPos(), state);
    TileEntity tile = world.getTileEntity(spawnPos.toBlockPos());
    if (!(tile instanceof TileEntityTomb)) {
      //we failed to place it 
      MessageType.MESSAGE_FAIL_TO_PLACE_GRAVE.sendSpecialMessage(player);
      ModTomb.LOGGER.log(Level.INFO, MessageType.MESSAGE_FAIL_TO_PLACE_GRAVE.getServerTranslation());
      return;
    }
    //else grave success
    TileEntityTomb grave = (TileEntityTomb) tile;
    grave.initTombstoneOwner(player);
    ModTomb.LOGGER.log(Level.INFO, MessageType.MESSAGE_NEW_GRAVE.getServerTranslation());
    MessageType.MESSAGE_NEW_GRAVE.sendSpecialMessage(player);
    MessageType.MESSAGE_JOURNEYMAP.sendSpecialMessage(player, spawnPos.x, spawnPos.y, spawnPos.z, spawnPos.dim);
    if (//ConfigTombstone.general.playerGraveAccess ||
    ConfigTomb.INSTANCE.graveKeyOnDeath) {
      ItemStack key = new ItemStack(TombRegistry.grave_key);
      TombRegistry.grave_key.setTombPos(key, spawnPos);
      if (ConfigTomb.INSTANCE.addNameToPlayersKey) {
        key.setStackDisplayName(TextFormatting.GOLD + player.getDisplayNameString() + " " + key.getDisplayName());
      }
      keys.add(key);
    }
    this.storeSoulboundsOnBody(player, keys);
    IItemHandler itemHandler = grave.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    //      boolean hasLosses = world.rand.nextInt(100) + 1 <= ConfigTombstone.general.chanceLossOnDeath;
    //int countLoss = 0; 
    //    ItemStack test = player.inventory.getStackInSlot(40);
    //    System.out.println("testignoringdrops = "+test);
    for (EntityItem entityItem : event.getDrops()) {
      if (!entityItem.getItem().isEmpty()) {
        ItemHandlerHelper.insertItemStacked(itemHandler, entityItem.getItem().copy(), false);
        entityItem.setItem(ItemStack.EMPTY);
      }
    }
    for (EntityItem entityItem : itemsPickedUpFromGround) {
      ItemHandlerHelper.insertItemStacked(itemHandler, entityItem.getItem(), false);
      entityItem.setItem(ItemStack.EMPTY);
    }
    world.notifyBlockUpdate(spawnPos.toBlockPos(), Blocks.AIR.getDefaultState(), state, 2);
  }

  private List<EntityItem> pickupFromGround(EntityPlayerMP player, ArrayList<ItemStack> keys) {
    double range = ConfigTomb.INSTANCE.pickUpGroundRange;
    if (range == 0) {
      return new ArrayList<>();//disabled
    }
    return player.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(
        player.posX - range,
        player.posY - range,
        player.posZ - range,
        player.posX + range,
        player.posY + range,
        player.posZ + range));
  }

  @SubscribeEvent
  public static void onFingerprintViolation(FMLFingerprintViolationEvent event) {
    // https://tutorials.darkhax.net/tutorials/jar_signing/
    String source = (event.getSource() == null) ? "" : event.getSource().getName() + " ";
    String msg = ModTomb.MODID + "Invalid fingerprint detected! The file " + source + "may have been tampered with. This version will NOT be supported by the author!";
    System.out.println(msg);
  }
}
