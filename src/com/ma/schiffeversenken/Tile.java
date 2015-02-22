package com.ma.schiffeversenken;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tile {
	
	float x,y,width,height;
	Texture texture;
	@Deprecated
	public Tile(float x, float y, float width, float height, Texture texture) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.texture = texture;
	}
	
	public void render(SpriteBatch batch){
		batch.draw(texture, x, y, width, height);
		
	}
	

}
