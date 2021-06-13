package com.lothrazar.simpletomb.block;

import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomStateMapper extends StateMapperBase {

  protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
    EnumFacing facing = state.getValue(BlockTomb.FACING);
    if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
      state = state.withProperty(BlockTomb.FACING, EnumFacing.NORTH);
    }
    Map<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(
        state.getProperties());
    String s = Block.REGISTRY.getNameForObject(state.getBlock()).toString();
    return new ModelResourceLocation(s, this.getPropertyString(map));
  }
}
