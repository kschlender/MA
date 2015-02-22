package com.ma.schiffeversenken;

import java.io.Serializable;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

//public class EntityShip extends Sprite {
	public class EntityShip implements Serializable {
	
	private static final long serialVersionUID = 1L;
	//Texturkoordinaten
	private Vector2 position,size;
	Rectangle bounds;

	/** Bewegung */
	private Vector2 velocity = new Vector2();

	private float speed = 60 * 2, gravity = 60 * 1.8f;

//	TiledMapTileLayer collisionLayer;
	private String textureName;

	
	public EntityShip(String textureName,Vector2 pos, Vector2 size) {
		// uebergabe des Sprite, wie das Schiff aussehen soll.
		// uebergabe des ColissionLayer
//		collisionLayer = c;
		this.position = pos;
		this.size = size;
		this.textureName=textureName;
		this.bounds = new Rectangle(position.x, position.y, size.x, size.y);
	}

	private void update(float deltaTime) {
		bounds.set(position.x, position.y, size.x, size.y);
//		// Bewegung erzeugen, Gravity nach unten mal die zeit
//		velocity.y -= gravity * deltaTime;
//		// Gravity abfangen um nicht ins extreme zu gehen.
//		// Wenn zu schnell dann auf speedlimit setzen
//		if (velocity.y > speed) {
//			velocity.y = speed;
//		} else if (velocity.y < speed) {
//			velocity.y = -speed;
//		}

		// IDEA of colision detection
		/*
		 * alte koordinaten und neue koordinaten die neuen koordinaten werden
		 * geprueft auf kolisition wenn die neuen Koordinaten ungueltig sind dann
		 * sollen die alten koordinaten genommen werden andernfalls werden die
		 * neuen ueberprueften Koordinaten uebergeben als aktuelle Position.
		 * 
		 * Fuer die performance geht man hin und betrachtet nur die Bereiche von
		 * der Map die jeweils in der Richtung in der gegangen wird liegen.
		 * undzwar die 3 direkt angrenzenden tiles
		 */

//		// alte pos speichern
//		float oldX = getX(), oldY = getY(), tileWidth = collisionLayer
//				.getTileWidth(), tileHeight = collisionLayer.getTileHeight();
//		boolean collisionX = false, collisionY = false;

		// zu neuer Position x wandern.
		setX(getX() + velocity.x * deltaTime);

//		// pruefen ob wir nach rechts oder links gehen
//		if (velocity.x < 0) {
//			// richtung links
//			// top left
//			// true/false |= true/false, getCell at position of
//			// Entity,getTile,getpropertys,contains(blocked)
//			collisionX = collisionLayer
//					.getCell((int) (getX() / tileWidth),
//							(int) (getY() + getHeight() / tileHeight))
//					.getTile().getProperties().containsKey("blocked");
//
//			// middle left
//			if (!collisionX)
//				collisionX = collisionLayer
//						.getCell((int) (getX() / tileWidth),
//								(int) ((getY() + getHeight() / 2) / tileHeight))
//						.getTile().getProperties().containsKey("blocked");
//
//			// bottom left
//			if (!collisionX)
//				collisionX = collisionLayer
//						.getCell((int) (getX() / tileWidth),
//								(int) (getY() / tileHeight)).getTile()
//						.getProperties().containsKey("blocked");
//		} else if (velocity.x > 0) {
//			// richtung rechts
//			// top rigth
//			collisionX = collisionLayer
//					.getCell((int) ((getX() + getWidth()) / tileWidth),
//							(int) ((getY() + getHeight()) / tileHeight))
//					.getTile().getProperties().containsKey("blocked");
//			// middle right
//			if (!collisionX)
//				collisionX = collisionLayer
//						.getCell((int) ((getX() + getWidth()) / tileWidth),
//								(int) ((getY() + getHeight() / 2) / tileHeight))
//						.getTile().getProperties().containsKey("blocked");
//
//			// bottom right
//			if (!collisionX)
//				collisionX = collisionLayer
//						.getCell((int) ((getX() + getWidth()) / tileWidth),
//								(int) ((getY()) / tileHeight)).getTile()
//						.getProperties().containsKey("blocked");
//		}
//
//		//Reaktion auf collisionX. Uebergeben wir bei Kollision die alte xpos
//		if(collisionX){
//			setX(oldX);
//			velocity.x=0;//um nicht gegen die Wand zu fahren
//		}
		
		
		// zu neuer Position > wandern.
		setY(getY() + velocity.y * deltaTime);

//		// Pruefen ob wir nach oben oder unten gehen
//		if (velocity.y < 0) {
//			// richtung unten
//			// bottom left
//			collisionY = collisionLayer
//					.getCell((int) (getX() / tileWidth),
//							(int) (getY() / tileHeight)).getTile()
//					.getProperties().containsKey("blocked");
//			// bottom middle
//			if (!collisionY)
//				collisionY = collisionLayer
//						.getCell((int) ((getX() + getWidth() / 2) / tileWidth),
//								(int) (getY() / tileHeight)).getTile()
//						.getProperties().containsKey("blocked");
//			// bottom right
//			if (!collisionY)
//				collisionY = collisionLayer
//						.getCell((int) ((getX() + getWidth()) / tileWidth),
//								(int) (getY() / tileHeight)).getTile()
//						.getProperties().containsKey("blocked");
//		} else if (velocity.y > 0) {
//			// richtung oben
//			// top left
//			collisionY = collisionLayer
//					.getCell((int) (getX() / tileWidth),
//							(int) ((getY() + getHeight()) / tileHeight))
//					.getTile().getProperties().containsKey("blocked");
//			// top middle
//			if (!collisionY)
//				collisionY = collisionLayer
//						.getCell((int) ((getX() + getWidth() / 2) / tileWidth),
//								(int) ((getY() + getHeight()) / tileHeight))
//						.getTile().getProperties().containsKey("blocked");
//			// top right
//			if (!collisionY)
//				collisionY = collisionLayer
//						.getCell((int) ((getX() + getWidth()) / tileWidth),
//								(int) ((getY() + getHeight()) / tileHeight))
//						.getTile().getProperties().containsKey("blocked");
//
//		}
//		
//
//		//Reaktion auf collisionY. Uebergeben wir bei Kollision die alte xpos
//		if(collisionY){
//			setY(oldY);
//			velocity.x=0;//um nicht gegen die Wand zu fahren
//		}
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getGravity() {
		return gravity;
	}

	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

//	public TiledMapTileLayer getCollisionLayer() {
//		return collisionLayer;
//	}
//
//	public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
//		this.collisionLayer = collisionLayer;
//	}
	

	public float getX() {
		return position.x;
	}
	
	
	public float getY() {
		return position.y;
	}

	public void setX(float x) {
		this.position.x=x;
	}
	

	public void setY(float y) {
		this.position.y=y;
	}

	public void render(Batch batch,boolean attacked,TreeMap<String, TextureRegion> shipTextures) {
		// Beim Zeichnen wird update vorher aufgerufen mit xpos und ypos
		// aktualisiert
		update(Gdx.graphics.getDeltaTime());
		if(!attacked){
			batch.draw(shipTextures.get(textureName),position.x, position.y, size.x, size.y);
		}else{
			batch.draw(shipTextures.get(textureName+"a"),position.x, position.y, size.x, size.y);
		}
//		batch.draw(new Sprite(texture.getTextureRegion().getTexture()), x, y, texture.getTextureRegion().getTexture().getWidth(), texture.getTextureRegion().getTexture().getHeight());	
	
	}

	public void setPosition(float xpos, float ypos) {
		this.position.x=xpos;
		this.position.y=ypos;
		
	}

	public void setShipTextureRegion(String textureName) {
		this.textureName=textureName;
	}
	
	@Override
	public String toString(){
		return textureName;
	}
	
	
}
