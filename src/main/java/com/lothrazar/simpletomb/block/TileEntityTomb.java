package com.lothrazar.simpletomb.block;

import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import com.lothrazar.simpletomb.ModTomb;
import com.lothrazar.simpletomb.data.MessageType;
import com.lothrazar.simpletomb.helper.EntityHelper;
import com.lothrazar.simpletomb.helper.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityTomb extends TileEntity implements ITickable {

  protected final ItemStackHandler inventory = new ItemStackHandler(120);
  protected String ownerName = "";
  protected long deathDate;
  public int timer = 0;
  protected UUID ownerId = null;
  //nothing in game sets this.  
  // a server command could set this to false to let admins or anyone in 
  //but in normal survival gameplay, it stays true and thus requires owners to access their graves
  private boolean onlyOwnersAccess = true;

  /****************
   * * TODO: fix slot numbers exaclty where they came from
   */
  public void giveInventory(@Nullable EntityPlayer player) {
    if (!this.world.isRemote && player != null && !(player instanceof FakePlayer)) {
      //first try to use the map. and nuke as we go
      //      IItemHandler playerInventory = new PlayerMainInvWrapper(player.inventory);
      //      for (int i = 0; i < this.inventory.getSlots(); i++) {
      //        ItemStack tombStack = this.inventory.getStackInSlot(i);
      //        if (tombStack.isEmpty()) {
      //          continue;
      //        }
      //        int hashCode = PlayerTombEvents.hashCode(tombStack);
      //        //now i need to FIND a slot number matching my hash code
      //        int playerSlot = PlayerTombEvents.matchingSlotForHashCode(player, hashCode);
      //        System.out.println(playerSlot + " slot should remap to " + tombStack + " with hashcode " + hashCode);
      //        if (playerSlot >= 0 && playerInventory.getStackInSlot(playerSlot).isEmpty()
      //            && playerSlot < playerInventory.getSlots()) {
      //          //
      //          System.out.println(playerSlot + " slot should remap to " + tombStack);
      //          //add to player slot
      //          //deduct by replace with empty
      //          // ItemHandlerHelper.giveItemToPlayer(player, this.inventory.getStackInSlot(i), playerSlot);
      //          playerInventory.insertItem(playerSlot, tombStack, false);
      //          this.inventory.setStackInSlot(i, ItemStack.EMPTY);
      //        }
      //      }
      //now the normal dump continues 
      for (int i = this.inventory.getSlots() - 1; i >= 0; --i) {
        if (EntityHelper.autoequip(this.inventory.getStackInSlot(i), player)) {
          this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
      }
      IntStream.range(0, this.inventory.getSlots()).forEach(ix -> {
        ItemStack stack = this.inventory.getStackInSlot(ix);
        if (!stack.isEmpty()) {
          ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
          this.inventory.setStackInSlot(ix, ItemStack.EMPTY);
        }
      });
      this.removeGraveBy(player);
      //      EntityHelper.capPotionDuration(player, ModEffects.ghostly_shape, 100);
      player.inventoryContainer.detectAndSendChanges();
      MessageType.MESSAGE_OPEN_GRAVE_SUCCESS.sendSpecialMessage(player);
    }
  }

  public boolean onlyOwnersCanAccess() {
    return this.onlyOwnersAccess;
  }

  private void removeGraveBy(@Nullable EntityPlayer player) {
    if (this.world != null) {
      WorldHelper.removeNoEvent(this.world, this.pos);
      if (player != null) {
        this.world.playSound(null,
            player.posX, player.posY, player.posZ,
            SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }
    }
  }

  public void initTombstoneOwner(EntityPlayer owner) {
    this.deathDate = System.currentTimeMillis();
    this.ownerName = owner.getDisplayName().getUnformattedText();
    this.ownerId = owner.getUniqueID();
  }

  public boolean isOwner(EntityPlayer owner) {
    if (ownerId == null || owner == null || !hasOwner()) {
      return false;
    }
    //dont match on name. id is always set anyway 
    return this.ownerId.equals(owner.getUniqueID());
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    double renderExtension = 1.0D;
    return new AxisAlignedBB(
        this.pos.getX() - renderExtension,
        this.pos.getY() - renderExtension,
        this.pos.getZ() - renderExtension,
        this.pos.getX() + 1 + renderExtension,
        this.pos.getY() + 1 + renderExtension,
        this.pos.getZ() + 1 + renderExtension);
  }

  @Override
  public void update() {
    if (this.getBlockType() instanceof BlockTomb) {
      ++this.timer;
      if (this.world.isRemote) {
        if (WorldHelper.atInterval(this.timer, 100)) {
          ModTomb.PROXY.produceGraveSoul(this.world, this.pos);
        }
        ModTomb.PROXY.produceGraveSmoke(this.world, this.pos);
      }
    }
  }

  String getOwnerName() {
    return this.ownerName;
  }

  boolean hasOwner() {
    return this.ownerName.length() > 0;
  }

  long getOwnerDeathTime() {
    return this.deathDate;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound.setString("ownerName", this.ownerName);
    compound.setLong("deathDate", this.deathDate);
    compound.setInteger("countTicks", this.timer);
    compound.setTag("inventory", this.inventory.serializeNBT());
    if (this.ownerId != null) {
      compound.setUniqueId("ownerid", this.ownerId);
    }
    compound.setBoolean("onlyOwnersAccess", this.onlyOwnersAccess);
    super.writeToNBT(compound);
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    this.ownerName = compound.getString("ownerName");
    this.deathDate = compound.getLong("deathDate");
    this.timer = compound.getInteger("countTicks");
    if (compound.hasKey("inventory", 10)) {
      this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }
    if (compound.hasUniqueId("ownerid")) {
      this.ownerId = compound.getUniqueId("ownerid");
    }
    this.onlyOwnersAccess = compound.getBoolean("onlyOwnersAccess");
    super.readFromNBT(compound);
  }

  @Override
  public NBTTagCompound getUpdateTag() {
    NBTTagCompound nbt = this.writeToNBT(new NBTTagCompound());
    if (nbt.hasKey("inventory", 10)) {
      nbt.removeTag("inventory");
    }
    return nbt;
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return oldState.getBlock() != newState.getBlock();
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    this.readFromNBT(pkt.getNbtCompound());
  }

  @Override
  public boolean receiveClientEvent(int id, int type) {
    return true;
  }

  @Override
  public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
    return this.getCapability(capability, facing) != null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return (T) (facing == null ? this.inventory : null);
    }
    else {
      return super.getCapability(capability, facing);
    }
  }

  @Override
  public void invalidate() {
    if (this.world != null && !this.world.isRemote) {
      if (this.world.getBlockState(this.pos).getBlock() instanceof BlockTomb) {
        return;
      }
      for (int i = 0; i < this.inventory.getSlots(); ++i) {
        ItemStack stack = this.inventory.getStackInSlot(i);
        if (!stack.isEmpty()) {
          InventoryHelper.spawnItemStack(
              this.world,
              this.pos.getX(),
              this.pos.getY(),
              this.pos.getZ(),
              this.inventory.extractItem(i, stack.getCount(), false));
        }
      }
    }
    super.invalidate();
  }
}
