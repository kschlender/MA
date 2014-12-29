package com.ma.schiffeversenken.android.controller;

/**
 * Interface fuer die drei verschiedenen KI Strategien:
 * Einfach, Normal, Schwierig
 * @author Maik Steinborn
 */
public interface KIStrategy {
	/**
	 * Den Gegner attackieren
	 * @return ID des Feldes, das attackiert werden soll
	 */
	public int attack();
}
