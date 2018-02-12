package anwendung;

import java.io.Serializable;

import javafx.scene.control.Label;
//import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/*
 * Einfache Nachrichtenklasse:
 * 
 * Wichtig: package muss das selbe sein wie beim Server 
 *  -> hier test
 * 
 * Die Schnittstelle Serializable wird implementiert:
 * 
 * Serializable bewirkt eine Speicherung des Objektes, welches 
 * sonst nur temporÃ¤re Lebensdauer hÃ¤tte. 
 * 
 * */

public class Nachricht implements Serializable
{

    // Serializable:
    // Id muss die gleiche sein wie beim Client, damit Klasse richtig
    // identifiziert werden kann
    private static final long serialVersionUID = 2881158309415505224L;

    // Test Attribut zum Auslesen, weitere Attribute mÃ¶glich auÃŸer Thread,
    // dieser ist nicht mÃ¶glich als Attribut
    private String name, text;

    // private ProgressIndicator progressIndicator;

    // private double progress;

    private VBox messageFrame;

    // Konstruktor
    public Nachricht(String name, String text)
    {
        this.name = name;
        this.text = text;
    }

    public VBox nachrichtInit()
    {

        // HBox whatMessage = new HBox();
        messageFrame = new VBox();
        Label nameLabel = new Label(name);
        Label messageLabel = new Label(text);

        // DEV
        // this.progressIndicator = new ProgressIndicator();

        // progressIndicator.setMaxHeight(10);
        // progressIndicator.setMaxWidth(10);

        // messageFrame.setMaxHeight(150);
        // messageFrame.setMaxWidth(250);

        nameLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));

        // setzt das Namelabel NUR falls zuletzt eine Nachricht empfangen wurde

        messageFrame.getChildren().addAll(nameLabel, messageLabel);

        // whatMessage.getChildren().add(messageFrame);

        return messageFrame;
    }

    public void setBackgroundColor(Boolean didISendIt)
    {
        // überprüft ob es eine gesendete- oder empfangene Nachricht ist
        if (didISendIt)
        {
            // Mint Grün für eigene Nachrichten
            messageFrame.setStyle("-fx-background-color: #9dffde;");
            // messageFrame.getChildren().add(progressIndicator);
        }
        else
        {
            // Beige für die Nachrichten anderer User
            messageFrame.setStyle("-fx-background-color: #ffeeba;");
        }
    }

    // public void setProgressStatus(double progress)
    // {
    // this.progress = progress;
    // }
    //
    // public double getProgressStatus()
    // {
    // return this.progressIndicator.getProgress();
    // }

    // getter
    public String getText()
    {
        return text;
    }

    public String getName()
    {
        return name;
    }

}// Nachricht
