package com.ma.schiffeversenken.view;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class GLRenderer implements Renderer{
	
// FLASH BG COLOR TEST	
//	long lastTime = 0;
//	final int FPS = 1;
//	final float period = 1000/FPS;
//	Random r = new Random();
	
	@Override //like init() when surface created
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
	}


	@Override //when surface is changed
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//reset x and y coordinates to the size of screen
		gl.glViewport(0, 0, width, height); 
	}


	
	@Override //every fps
	public void onDrawFrame(GL10 gl) {
		
// FLASH BG COLOR TEST
//		if(System.currentTimeMillis() - lastTime>=period){
//			lastTime = System.currentTimeMillis();
//			gl.glClearColor(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1.0f);
//		}
		
		
		//Every time rendered we clear BG
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
	}

}
