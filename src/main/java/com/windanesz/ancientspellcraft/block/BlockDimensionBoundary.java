package com.windanesz.ancientspellcraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockDimensionBoundary extends Block {

	public BlockDimensionBoundary() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		this.setBlockUnbreakable();
		this.setSoundType(SoundType.STONE);
		this.disableStats();
		setLightLevel(0.8F);
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.STONE);
	}
}
