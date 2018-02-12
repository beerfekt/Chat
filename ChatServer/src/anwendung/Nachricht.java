package anwendung;

import java.io.Serializable;

/*
 * Einfache Nachrichtenklasse:
 * 
 * Wichtig: package muss das selbe sein wie beim Server 
 *  -> hier test
 * 
 * Die Schnittstelle Serializable wird implementiert:
 * 
 * Serializable bewirkt eine Speicherung des Objektes, welches 
 * sonst nur temporäre Lebensdauer hätte. 
 * 
 * */

public class Nachricht implements Serializable {

	//Serializable:
	//Id muss die gleiche sein wie beim Client, damit Klasse richtig identifiziert werden kann
	private static final long serialVersionUID = 2881158309415505224L;
	
	//Test Attribut zum Auslesen, weitere Attribute möglich außer Thread, dieser ist nicht möglich als Attribut
	private String name,text;

	//Konstruktor
	public Nachricht (String name, String text){
		this.name = name;
		this.text = text;
	}

	//getter
	public String getText() {
		return text;
	}

	public String getName(){
		return name;
	}
	
	
}//Nachricht
