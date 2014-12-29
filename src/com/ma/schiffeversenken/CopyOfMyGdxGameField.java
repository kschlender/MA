//package com.ma.schiffeversenken;
//
//import com.badlogic.gdx.ApplicationListener;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.PerspectiveCamera;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g3d.Environment;
//import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
//import com.ma.schiffeversenken.android.model.Player;
//
//public class CopyOfMyGdxGameField implements ApplicationListener {
//	public static final int size =320;
//	private PerspectiveCamera camera;
//	private Player player;
//	private TextureAtlas atlas;
//	// private TextureRegion background;
//	private Environment environment;
//	private SpriteBatch batch;
//	private int h;
//	// private Sprite sprite;// Sprite fügt eine Charakteristik hinzu mit Größe
//	// private Texture texture;
//	private int w;
//
//	/**
//	 * create initialisiert das Grundgerüst für das Zeichnen mit OpenGL ES 2.0
//	 * es wird nur einmal ausgeführt
//	 */
//	@Override
//	public void create() {
//		// graphics High and width
//		h = Gdx.graphics.getHeight();
//		w = Gdx.graphics.getWidth();
//		// Get Texture Pack
//		atlas = new TextureAtlas(Gdx.files.internal("graphics//textures.atlas"));
//		// Create camera sized to screens width/height with Field of View of 75
//		// degrees
//		camera = new PerspectiveCamera(75, w, h);
//		// Move the camera 3 units back along the z-axis and look at the origin
//		camera.position.set(0, 0, 0);
//		// camera.lookAt(0f, 0f, 0f);
//		camera.lookAt(0, 0, 0);
//		camera.translate(h, h+size, h*4);
//		// Near and Far (plane) repesent the minimum and maximum ranges of the
//		// camera in, um, units
//		camera.near = 0.1f;
//		camera.far = h*4;
//
//		// Zeichnet alles auf dem Screen
//		batch = new SpriteBatch();
//
//		// Creating Player and Game Fields
//		// ...TODO
//		player = new Player(atlas);
//
//		// Background
//		// texture = new Texture(Gdx.files.internal("graphics//ocean.png"));
//		// background = new TextureRegion(texture, 0, 0, texture.getWidth(),
//		// texture.getHeight());
//		// sprite = new Sprite(background);
//		// sprite.setSize(120, 120 * sprite.getHeight() / sprite.getWidth());
//		// sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
//		// sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
//		// Background Ende
//
//		// Finally we want some light, or we wont see our color. The environment
//		// gets passed in during
//		// the rendering process. Create one, then create an Ambient (
//		// non-positioned, non-directional ) light.
//		environment = new Environment();
//		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f,
//				0.8f, 0.8f, 1.0f));
//	}
//
//	@Override
//	public void dispose() {
//		atlas.dispose();
//		player.dispose();
//		batch.dispose();
//		// texture.dispose();
//	}
//
//	@Override
//	public void render() {
//
//		// You've seen all this before, just be sure to clear the
//		// GL_DEPTH_BUFFER_BIT when working in 3D
//		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
//				Gdx.graphics.getHeight());
//		Gdx.gl.glClearColor(1, 1, 1, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//		// When you change the camera details, you need to call update();
//		// Also note, you need to call update() at least once.
//		player.update();
//		camera.update();
//
//		// Setzt das Coordinatensystem wo wir unser OGL zeichnen.
//		batch.setProjectionMatrix(camera.combined);
//
//		// Zeichnen von spritebatch
//		batch.begin();// GLBegin(); Start drawing.
//		// sprite.draw(batch);
//		player.draw(batch);
//		batch.end();// GLEnd(); End drawing.
//
//	}
//
//	@Override
//	public void resize(int width, int height) {
//	}
//
//	@Override
//	public void pause() {
//		// TODO SPEICHERN
//
//	}
//
//	@Override
//	public void resume() {
//		// TODO LADEN
//
//	}
//
//}