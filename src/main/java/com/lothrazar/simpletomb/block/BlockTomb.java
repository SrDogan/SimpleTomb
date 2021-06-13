package com.lothrazar.simpletomb.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import com.lothrazar.simpletomb.ModTomb;
import com.lothrazar.simpletomb.TombRegistry;
import com.lothrazar.simpletomb.data.LocationBlockPos;
import com.lothrazar.simpletomb.data.MessageType;
import com.lothrazar.simpletomb.helper.EntityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTomb extends Block {

  public static final PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
  public static final PropertyInteger MODEL_TEXTURE = PropertyInteger.create("model_texture", 0, 1);
  public static final PropertyBool IS_ENGRAVED = PropertyBool.create("is_engraved");
  public static final PropertyBool HAS_SOUL = PropertyBool.create("has_soul");
  protected static final AxisAlignedBB ground_bounds = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);
  protected final String name;
  protected final ModelTomb graveModel;
  protected final HashMap<EnumFacing, List<AxisAlignedBB>> collisions = new HashMap<>();

  public BlockTomb(ModelTomb graveModel) {
    super(Material.ROCK);
    this.setCreativeTab(null);
    this.setLightLevel(0.7F);
    this.setBlockUnbreakable();
    this.setHardness(4.0F);
    this.setResistance(1.8E7F);
    this.name = graveModel.getName();
    this.graveModel = graveModel;
    this.setTranslationKey(name);
    this.setRegistryName(name);
    this.useNeighborBrightness = true;
    this.setLightLevel(0.4F);
    this.setLightOpacity(255);
    this.blockSoundType = SoundType.STONE;
    this.setDefaultState(this.blockState.getBaseState()
        .withProperty(FACING, EnumFacing.NORTH)
        .withProperty(MODEL_TEXTURE, Integer.valueOf(0))
        .withProperty(HAS_SOUL, false));
    this.loadCollisions();
  }

  @Override
  public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
    return -1.0F;
  }

  @Override
  public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
    return false;
  }

  public ModelTomb getGraveType() {
    return this.graveModel;
  }

  @Override
  public boolean canDropFromExplosion(Explosion explosionIn) {
    return false;
  }

  @Override
  public String getTranslationKey() {
    return ModTomb.MODID + ".grave." + this.name;
  }

  @Override
  public boolean isToolEffective(String type, IBlockState state) {
    return false;
  }

  @Override
  public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    ItemStack heldStack = placer.getHeldItem(hand);
    return this.getDefaultState()
        .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
        .withProperty(IS_ENGRAVED, false)
        .withProperty(MODEL_TEXTURE, MathHelper.clamp(heldStack.getMetadata(), 0, 1));
  }

  @Override
  public int quantityDropped(Random random) {
    return 0;
  }

  @Override
  protected boolean canSilkHarvest() {
    return false;
  }

  @Nullable
  public TileEntityTomb getTileEntity(IBlockAccess world, BlockPos pos) {
    TileEntity tile = world.getTileEntity(pos);
    return tile instanceof TileEntityTomb
        ? (TileEntityTomb) tile
        : null;
  }

  @Override
  public TileEntityTomb createTileEntity(World world, IBlockState state) {
    return new TileEntityTomb();
  }

  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING, HAS_SOUL, IS_ENGRAVED, MODEL_TEXTURE);
  }

  @Override
  public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
    if (!world.isRemote && entity.isSneaking() && !entity.isDead &&
        EntityHelper.isValidPlayer(entity)) {
      this.activatePlayerGrave(world, pos, state, (EntityPlayerMP) entity);
    }
  }

  private void activatePlayerGrave(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
    TileEntityTomb tile = this.getTileEntity(world, pos);
    if (tile != null && player.isEntityAlive()) {
      if (tile.onlyOwnersCanAccess() && !tile.isOwner(player)) {
        MessageType.MESSAGE_OPEN_GRAVE_NEED_OWNER.sendSpecialMessage(player);
        //        player.sendMessage(new TextComponentTranslation(MessageType.MESSAGE_OPEN_GRAVE_NEED_OWNER.getKey()));
      }
      else {
        //either you are the owner, or it has setting that says anyone can access
        tile.giveInventory(player);
        TombRegistry.grave_key.removeKeyForGraveInInventory(player, new LocationBlockPos(pos, world));
      }
    }
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntityTomb tile = this.getTileEntity(world, pos);
    return state.withProperty(IS_ENGRAVED, tile != null && tile.hasOwner());
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState()
        .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
        .withProperty(MODEL_TEXTURE, (meta & 4) == 0 ? 0 : 1)
        .withProperty(HAS_SOUL, (meta & 8) != 0);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return (state.getValue(FACING).getHorizontalIndex() +
        (state.getValue(MODEL_TEXTURE).intValue() == 0 ? 0 : 4) +
        (state.getValue(HAS_SOUL).booleanValue() ? 8 : 0));
  }

  @Override
  public int damageDropped(IBlockState state) {
    return state.getValue(MODEL_TEXTURE).intValue();
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return ItemStack.EMPTY.getItem();
  }

  @Override
  public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    return side == EnumFacing.DOWN;
  }

  @Override
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing facing) {
    return facing == EnumFacing.DOWN
        ? BlockFaceShape.SOLID
        : BlockFaceShape.UNDEFINED;
  }

  @Override
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
    EnumFacing facing = state.getValue(FACING);
    if (facing.getAxis() == Axis.Y) {
      facing = EnumFacing.NORTH;
    }
    List<AxisAlignedBB> collisionList = this.collisions.get(facing);
    for (AxisAlignedBB collision : collisionList) {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, collision);
    }
    addCollisionBoxToList(pos, entityBox, collidingBoxes, ground_bounds);
  }

  private void loadCollisions() {
    switch (this.graveModel) {
      case GRAVE_CROSS:
        this.addCollision(EnumFacing.NORTH,
            new AxisAlignedBB(0.40625D, 0.125D, 0.6875D,
                0.59375D, 0.9375D, 0.875D));
        this.addCollision(EnumFacing.SOUTH,
            new AxisAlignedBB(0.40625D, 0.125D, 0.125D,
                0.59375D, 0.9375D, 0.3125D));
        this.addCollision(EnumFacing.WEST,
            new AxisAlignedBB(0.6875D, 0.125D, 0.40625D,
                0.875D, 0.9375D, 0.59375D));
        this.addCollision(EnumFacing.EAST,
            new AxisAlignedBB(0.125D, 0.125D, 0.40625D,
                0.3125D, 0.9375D, 0.59375D));
        this.addCollision(EnumFacing.NORTH,
            new AxisAlignedBB(0.21875D, 0.5625D, 0.6875D,
                0.78125D, 0.75D, 0.875D));
        this.addCollision(EnumFacing.SOUTH,
            new AxisAlignedBB(
                0.21875D, 0.5625D, 0.125D,
                0.78125D, 0.75D, 0.3125D));
        this.addCollision(EnumFacing.WEST,
            new AxisAlignedBB(0.6875D, 0.5625D, 0.21875D,
                0.875D, 0.75D, 0.78125D));
        this.addCollision(EnumFacing.EAST,
            new AxisAlignedBB(0.125D, 0.5625D, 0.21875D,
                0.3125D, 0.75D, 0.78125D));
        this.addCollision(EnumFacing.NORTH,
            new AxisAlignedBB(0.28125D, 0.0625D, 0.5625D,
                0.71875D, 0.125D, 1.0D));
        this.addCollision(EnumFacing.SOUTH,
            new AxisAlignedBB(0.28125D, 0.0625D, 0.0D, 0.71875D, 0.125D, 0.4375D));
        this.addCollision(EnumFacing.WEST, new AxisAlignedBB(0.5625D, 0.0625D, 0.28125D, 1.0D, 0.125D, 0.71875D));
        this.addCollision(EnumFacing.EAST,
            new AxisAlignedBB(0.0D, 0.0625D, 0.28125D, 0.4375D, 0.125D, 0.71875D));
        this.addCollision(EnumFacing.NORTH,
            new AxisAlignedBB(0.34375D, 0.125D, 0.625D, 0.65625D, 0.1875D, 0.9375D));
        this.addCollision(EnumFacing.SOUTH,
            new AxisAlignedBB(0.34375D, 0.125D, 0.0625D, 0.65625D, 0.1875D, 0.375D));
        this.addCollision(EnumFacing.WEST,
            new AxisAlignedBB(0.625D, 0.125D, 0.34375D, 0.9375D, 0.1875D, 0.65625D));
        this.addCollision(EnumFacing.EAST,
            new AxisAlignedBB(0.0625D, 0.125D, 0.34375D, 0.375D, 0.1875D, 0.65625D));
      break;
      case GRAVE_NORMAL:
        this.addCollision(EnumFacing.NORTH,
            new AxisAlignedBB(0.1875D, 0.0625D, 0.0D, 0.8125D, 0.28125D, 1.0D));
        this.addCollision(EnumFacing.SOUTH,
            new AxisAlignedBB(0.1875D, 0.0625D, 0.0D, 0.8125D, 0.28125D, 1.0D));
        this.addCollision(EnumFacing.WEST,
            new AxisAlignedBB(0.0D, 0.0625D, 0.1875D, 1.0D, 0.28125D, 0.8125D));
        this.addCollision(EnumFacing.EAST,
            new AxisAlignedBB(0.0D, 0.0625D, 0.1875D, 1.0D, 0.28125D, 0.8125D));
        this.addCollision(EnumFacing.NORTH,
            new AxisAlignedBB(0.1875D, 0.25D, 0.875D, 0.8125D, 0.875D, 1.0D));
        this.addCollision(EnumFacing.SOUTH,
            new AxisAlignedBB(0.1875D, 0.25D, 0.0D, 0.8125D, 0.875D, 0.125D));
        this.addCollision(EnumFacing.WEST,
            new AxisAlignedBB(0.875D, 0.25D, 0.1875D, 1.0D, 0.875D, 0.8125D));
        this.addCollision(EnumFacing.EAST,
            new AxisAlignedBB(0.0D, 0.25D, 0.1875D, 0.125D, 0.875D, 0.8125D));
      break;
      case GRAVE_SIMPLE:
        this.addCollision(EnumFacing.NORTH,
            new AxisAlignedBB(0.1875D, 0.0625D, 0.8125D, 0.8125D, 0.875D, 1.0D));
        this.addCollision(EnumFacing.SOUTH,
            new AxisAlignedBB(0.1875D, 0.0625D, 0.0D, 0.8125D, 0.875D, 0.1875D));
        this.addCollision(EnumFacing.WEST,
            new AxisAlignedBB(0.8125D, 0.0625D, 0.1875D, 1.0D, 0.875D, 0.8125D));
        this.addCollision(EnumFacing.EAST,
            new AxisAlignedBB(0.0D, 0.0625D, 0.1875D, 0.1875D, 0.875D, 0.8125D));
      break;
      case GRAVE_TOMB:
      default:
        this.addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.03125D,
            0.0625D,
            0.21875D,
            0.96875D,
            0.64375D,
            0.96875D));
        this.addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.03125D,
            0.0625D,
            0.03125D,
            0.96875D,
            0.64375D,
            0.78125D));
        this.addCollision(EnumFacing.WEST, new AxisAlignedBB(0.21875D,
            0.0625D,
            0.03125D,
            0.96875D,
            0.64375D,
            0.96875D));
        this.addCollision(EnumFacing.EAST, new AxisAlignedBB(0.03125D,
            0.0625D,
            0.03125D,
            0.78125D,
            0.64375D,
            0.96875D));
        this.addCollision(EnumFacing.NORTH, new AxisAlignedBB(0.0625D,
            0.0625D,
            0.0625D,
            0.9375D,
            0.09375D,
            0.21875D));
        this.addCollision(EnumFacing.SOUTH, new AxisAlignedBB(0.0625D,
            0.0625D,
            0.78125D,
            0.9375D,
            0.09375D,
            0.9375D));
        this.addCollision(EnumFacing.WEST, new AxisAlignedBB(0.0625D,
            0.0625D,
            0.0625D,
            0.21875D,
            0.09375D,
            0.9375D));
        this.addCollision(EnumFacing.EAST, new AxisAlignedBB(0.78125D,
            0.0625D,
            0.0625D,
            0.9375D,
            0.09375D,
            0.9375D));
    }
  }

  private void addCollision(EnumFacing facing, AxisAlignedBB bounds) {
    List<AxisAlignedBB> listCollisions = this.collisions.computeIfAbsent(facing, k -> new ArrayList<>());
    listCollisions.add(bounds);
    this.collisions.put(facing, listCollisions);
  }

  @Override
  @Nullable
  public EnumFacing[] getValidRotations(World world, BlockPos pos) {
    return EnumFacing.HORIZONTALS;
  }
}
