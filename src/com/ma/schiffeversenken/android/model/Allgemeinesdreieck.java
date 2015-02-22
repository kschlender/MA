package com.ma.schiffeversenken.android.model;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;


/**
 * Klasse Handelt Touchevents
 * 
 * @author Klaus Schlender
 */
@Deprecated
public class Allgemeinesdreieck {
	private double seitea = 0.0;
	private double seiteb = 0.0;
	private double seitec = 0.0;
	private double flinhalt = 0.0; // Flaecheninhalt
	private ArrayList<float[]> pfeilPolygone;

	// Konstruktor
	public Allgemeinesdreieck(double x1, double y1, double x2, double y2, double x3, double y3){
		//a^2*b^2=c^2 Satz des Pythagoras
		double c2 = java.lang.Math.sqrt( ( ( (x1+y1) * (x1+y1) ) + ( (x2+y2) * (x2+y2) ) ));
		double b2 = java.lang.Math.sqrt( ( ( (x1+y1) * (x1+y1) ) + ( (x3+y3) * (x3+y3) ) ));
		double a2 = java.lang.Math.sqrt( ( ( (x2+y2) * (x2+y2) ) + ( (x3+y3) * (x3+y3) ) ));
	//den allgemeinen Seiten zuweisen
	seitea = a2;
	seiteb = b2;
	seitec = c2;
	
	//Fl�che berechnen
	flinhalt=flaecheninhalt();
	}

	/**
	 * Methode liefert den Fl�cheninhalt eines Dreiecks
	 * @return flaeche Fl�cheninhalt
	 */
	public double flaecheninhalt(){
		double s;
		double flaeche;
		if(seitea==seiteb&&seiteb==seitec){
			flaeche=(java.lang.Math.sqrt(3)/4)*(seitea*seitea);
		}else{
		//Satz des Heron anwenden:
		s = (seitea+seiteb+seitec)/2;
		flaeche = java.lang.Math.sqrt(s*(s-seitea)*(s-seiteb)*(s-seitec));
		}
		return flaeche;
	}

	public double getSeitea() {
		return seitea;
	}

	public double getSeiteb() {
		return seiteb;
	}

	public double getSeitec() {
		return seitec;
	}

	public double getFlinhalt() {
		return flinhalt;
	}
	
	public void getTriangles(TiledMap map){
		//Pfeil Generieren fuer das spaetere Zeichnen
		pfeilPolygone = new ArrayList<float[]>();
		MapObject object = map.getLayers().get("GameField").getObjects().get("arrow");
		Polygon pfeil = ((PolygonMapObject) object).getPolygon();
		float[] vert = pfeil.getTransformedVertices();
		boolean vertEnd=false;
		while(!vertEnd){
			double flaeche = new Allgemeinesdreieck(vert[0], vert[1], vert[2], vert[3], vert[4], vert[5]).getFlinhalt();
			if(flaeche<2148000||pfeilPolygone.size()>45900){
				vertEnd=true;
			}else{
				if((int) flaeche==2147483647){
					break;
				}
				System.out.println((int)flaeche);
			vert[0]=vert[0]-1;
			vert[1]=vert[1]-1;
			vert[2]=vert[2]-1;
			vert[3]=vert[3]+1;
			vert[4]=vert[4]+1;
			pfeilPolygone.add(vert);
			}
		}
	}

	public void draw(ShapeRenderer sr){
		sr.setColor(Color.RED);
		sr.begin(ShapeType.Line);
		for(int i=0;i<pfeilPolygone.size();i++){
			sr.point(pfeilPolygone.get(i)[0], pfeilPolygone.get(i)[1], 0);
		}
		sr.end();
	}
}
