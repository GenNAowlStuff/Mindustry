package io.anuke.mindustry.world.blocks.types;

import io.anuke.mindustry.entities.TileEntity;
import io.anuke.mindustry.entities.effect.Shield;
import io.anuke.mindustry.world.Tile;

public class ShieldBlock extends PowerBlock{
	public float shieldRadius = 40f;
	public float powerDrain = 0.01f;

	public ShieldBlock(String name) {
		super(name);
	}
	
	@Override
	public void update(Tile tile){
		ShieldEntity entity = tile.entity();
		
		if(entity.shield == null){
			entity.shield = new Shield(tile);
			entity.shield.add();
		}
		
		/*
		if(entity.power > powerDrain * Timers.delta()){
			if(!entity.shield.active){
				entity.shield.add();
			}
			
			entity.power -= powerDrain * Timers.delta();
		}else{
			if(entity.shield.active){
				entity.shield.remove();
			}
		}
		*/
	}
	
	@Override
	public TileEntity getEntity(){
		return new ShieldEntity();
	}
	
	static class ShieldEntity extends PowerEntity{
		Shield shield;
	}
}
