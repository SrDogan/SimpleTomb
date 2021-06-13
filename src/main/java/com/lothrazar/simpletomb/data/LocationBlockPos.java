package com.lothrazar.simpletomb.data;

import com.google.common.base.MoreObjects;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LocationBlockPos {

  public int x;
  public int y;
  public int z;
  public int dim;
  public static final BlockPos ORIGIN_POS = new BlockPos(0, Integer.MIN_VALUE, 0);
  public static final LocationBlockPos ORIGIN = new LocationBlockPos(ORIGIN_POS, Integer.MIN_VALUE);

  public LocationBlockPos(BlockPos pos, int dim) {
    this(pos.getX(), pos.getY(), pos.getZ(), dim);
  }

  public LocationBlockPos(BlockPos pos, World world) {
    this(pos.getX(), pos.getY(), pos.getZ(), world.provider.getDimension());
  }

  public LocationBlockPos(int x, int y, int z, int dim) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.dim = dim;
  }

  public LocationBlockPos(Entity entity) {
    this(entity.getPosition(), entity.world);
  }

  public BlockPos toBlockPos() {
    return new BlockPos(this.x, this.y, this.z);
  }

  public boolean equals(LocationBlockPos loc) {
    return (loc.x == this.x &&
        loc.y == this.y &&
        loc.z == this.z &&
        loc.dim == this.dim);
  }

  public boolean isOrigin() {
    return this.equals(ORIGIN);
  }

  public double getDistance(BlockPos pos) {
    double deltX = this.x - pos.getX();
    double deltY = this.y - pos.getY();
    double deltZ = this.z - pos.getZ();
    return Math.sqrt(deltX * deltX + deltY * deltY + deltZ * deltZ);
  }

  @Override
  public String toString() {
    return MoreObjects
        .toStringHelper(this)
        .add("x", this.x)
        .add("y", this.y)
        .add("z", this.z)
        .add("dim", this.dim)
        .toString();
  }
}
