package com.ma.schiffeversenken.android.model;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.OpenableColumns;

public class GamePreferences implements Parcelable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String GAME_PREFERENCES_TAG = "GamePreferences";
	
	public static final int MAX_ZERSTOERER_NUMBER = 2;
	public static final int MIN_ZERSTOERER_NUMBER = 0;
	public static final int MAX_SCHLACHTSCHIFF_NUMBER = 1;
	public static final int MIN_SCHLACHTSCHIFF_NUMBER = 0;
	public static final int MAX_UBOOT_NUMBER = 3;
	public static final int MIN_UBOOT_NUMBER = 0;
	public static final int MAX_KREUZER_NUMBER = 4;
	public static final int MIN_KREUZER_NUMBER = 0;
	public static final int MAX_SCHWIERIGKEIT_NUMBER = 3;
	public static final int MIN_SCHWIERIGKEIT_NUMBER = 1;
	
	public static final int DEFAULT_ZERSTOERER_NUMBER = 2;
	public static final int DEFAULT_SCHLACHTSCHIFF_NUMBER = 1;
	public static final int DEFAULT_UBOOT_NUMBER = 3;
	public static final int DEFAULT_KREUZER_NUMBER = 4;
	public static final int DEFAULT_SCHWIERIGKEIT_NUMBER = 1;
	
	private int mZerstoererNumber;
	private int mSchlachtschiffNumber;
	private int mUbootNumber;
	private int mKreuzerNumber;
	private int mSchwierigkeitStufe;
	
	public GamePreferences () {
		mZerstoererNumber = DEFAULT_ZERSTOERER_NUMBER;
		mSchlachtschiffNumber = DEFAULT_SCHLACHTSCHIFF_NUMBER;
		mUbootNumber = DEFAULT_UBOOT_NUMBER;
		mKreuzerNumber = DEFAULT_KREUZER_NUMBER;
		mSchwierigkeitStufe = DEFAULT_SCHWIERIGKEIT_NUMBER;
	}
	
	public GamePreferences (Integer z,Integer s,Integer u,Integer k,Integer ki) {
		mZerstoererNumber = z;
		mSchlachtschiffNumber = s;
		mUbootNumber = u;
		mKreuzerNumber = k;
		mSchwierigkeitStufe=ki;
	}
	
	private GamePreferences (Parcel in) {
		mZerstoererNumber = in.readInt();
		mSchlachtschiffNumber = in.readInt();
		mUbootNumber = in.readInt();
		mKreuzerNumber = in.readInt();
		mSchwierigkeitStufe = in.readInt();
	}

	public int getZerstoererNumber() {
		return mZerstoererNumber;
	}

	public void setZerstoererNumber(int s) {
		this.mZerstoererNumber = s;
	}
	
	

	public int getSchlachtschiffNumber() {
		return mSchlachtschiffNumber;
	}

	public void setSchlachtschiffNumber(int s) {
		this.mSchlachtschiffNumber = s;
	}

	public int getUbootNumber() {
		return mUbootNumber;
	}

	public void setUbootNumber(int s) {
		this.mUbootNumber = s;
	}

	public int getKreuzerNumber() {
		return mKreuzerNumber;
	}

	public void setKreuzerNumber(int s) {
		this.mKreuzerNumber = s;
	}

	public int getSchwierigkeitStufe() {
			return mSchwierigkeitStufe;
	}

	public void setSchwierigkeitStufe(int s) {
		this.mSchwierigkeitStufe = s;
	}

	public void writeToParcel(Parcel out, int flags)  {
		out.writeInt(mZerstoererNumber);
		out.writeInt(mSchlachtschiffNumber);
		out.writeInt(mUbootNumber);
		out.writeInt(mKreuzerNumber);
		out.writeInt(mSchwierigkeitStufe);
	}
	
	public static final Parcelable.Creator<GamePreferences> CREATOR
		= new Parcelable.Creator<GamePreferences>() {
			
			public GamePreferences createFromParcel (Parcel in) {
				return new GamePreferences(in);
			}

			public GamePreferences[] newArray(int size) {
				return new GamePreferences[size];
			}
		};
		
	public int describeContents() {
		return 0;
	}
	
	public int getTotalShips() {
		return mZerstoererNumber +
				mSchlachtschiffNumber + 
				mUbootNumber + 
				mKreuzerNumber;
	}
	
	public static void saveGamePreferences(GamePreferences p) throws IOException {
		if(Gdx.files.isLocalStorageAvailable()){
			ArrayList<Integer> map = new ArrayList<Integer>();
			map.add(p.getZerstoererNumber());
			map.add(p.getSchlachtschiffNumber());
			map.add(p.getUbootNumber());
			map.add(p.getKreuzerNumber());
			map.add(p.getSchwierigkeitStufe());
			
		FileHandle file = Gdx.files.local("preferences.bin");
		try {
			file.writeBytes(serialize(map), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}

	public static GamePreferences readGamePreferences() throws IOException,
			ClassNotFoundException {
		GamePreferences pref = null;
		if(Gdx.files.isLocalStorageAvailable()){
			
		FileHandle file = Gdx.files.local("preferences.bin");
		ArrayList<Integer> map = (ArrayList<Integer>) deserialize(file.readBytes());
		int i=0;
		pref = new GamePreferences(map.get(i++),map.get(i++),map.get(i++),map.get(i++),map.get(i++));
		}
		return pref;
	}

	/**
	 * Methode dient der serialisierung eines Objectes zu einem ByteArray.
	 * 
	 * @param obj
	 *            Das zu serialisierende Object.
	 * @return ByteArray der das serialiserte Object hält.
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(obj);
		return b.toByteArray();
	}

	/**
	 * Methode dient der deserialisierung eines Objectes aus einem ByteArray.
	 * 
	 * @param bytes
	 *            ByteArray eines Objectes.
	 * @return deserialisiertes Objekt wird zurückgeliefert.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}

		
}
