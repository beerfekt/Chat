package anwendung;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Platform;
import mvc.Controller;
import threads.Empfaenger;
//TODO
//import threads.HeartBeatThread;
import threads.Sender;

public class Client
{

    // Grunddaten
    private String clientName;
    private String serverIP;
    private int portnummer;

    // Connection
    private Socket clientSocket;

    // Streams
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    // Threads
    // Sender
    private Sender senderRunner;
    private Thread senderThread;

    // Empf�nger
    private Empfaenger empfaengerRunner;
    private Thread empfaengerThread;

    // Heartbeat - Test ob Server lebt

    // TODO Heartbeat dev
    // private HeartBeatThread heartBeatThread;
    private boolean tryToReconnect;
    private Thread heartBeatThread;
    private Controller controller;

    

    
    // Konstruktor
    public  Client(String name, String serverIP, int portnummer, Controller controller)
    {
        this.clientName = name;
        this.serverIP = serverIP;
        this.portnummer = portnummer;
        // dev
        this.controller = controller;
        //
        tryToReconnect = true;
        // Verbindung aufbauen
        verbinde();
        // Threads starten

        // 2 Threads: Sender und Empf�nger und Heartbeat
        starteThreads();       
    }// Construktor
    


    // Verbindung aufbauen
    public void verbinde()
    {
        try
        {
            closeConnection();
            System.out.println("------------------Client.verbinde()----------");
            System.out.println("Client.verbinde():            Versuche clientSocket zu erstellen:");
            clientSocket = new Socket(serverIP, portnummer);
            System.out.println("Client.verbinde():            Socket erstellt");
            ois = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Client.verbinde():            ObjectInputStrem erstellt");
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("Client.verbinde():            ObjectOutputStream erstellt");
            System.out.println("-------------------- verbunden! --------------");
            System.out.println("Servus :  " + clientName);
            System.out.println("Lokaler Port :" + clientSocket.getLocalPort());
            System.out.println("Server = " + clientSocket.getRemoteSocketAddress() + ":" + clientSocket.getPort());
            System.out.println("-------------------- Chat startet--------------");
        }
        catch (UnknownHostException e)
        {
            controller.errorDialog(e);
            e.printStackTrace();
            System.err.println("Client-Verbinde:            Keine Verbindung-unbekannter Host");
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Client-Verbinde:            Keine Verbindung-IO Exception");
            controller.errorDialog(e);
        } // try catch

    }// verbinde

    // Nachrichten einlesen und in Liste adden
    public void nutzeVerbindung(String br)
    {
        if (senderThread.isAlive() && clientSocket != null)
        {
            Nachricht nachricht;
            try
            {
                nachricht = new Nachricht(clientName, br);
                if (nachricht.getText().equals("exit"))
                {
                    System.out.println("Programm wird beendet");
                    exit();
                } // if
                System.out.println("Client.nutzeVerbindung: Nachricht wurde erstellt");
                senderRunner.addNachricht(nachricht);
                System.out.println("Client.nutzeVerbindung: und in Liste geadded");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            } // trycatch
        } // if
    }// nutzeVerbindung

    public void neuerNutzername()
    {
        controller.neuerNutzerName();
    }

    // CLient Einstiegspunkt
    void starte(String br)
    {
        while (heartBeatThread.isAlive())
        {
            if (clientSocket != null && !clientSocket.isClosed())
                nutzeVerbindung(br);
        } // if
        exit();
    }// starte

    // Threads - Sender/Empf�nger starten
    public void starteThreads()
    {
        stopThreads();
        // SenderThread erstellen
        senderRunner = new Sender(clientSocket, oos); // Runner wegen
                                                      // Vorlesungsbeispiel
                                                      // "SomethingtoRun"
        senderRunner.setController(this.controller);

        senderThread = new Thread(senderRunner); // ServerThread mit neuem
                                                 // Runner (ServerEmpfang) f�r
                                                 // Anweisungen f�r die
                                                 // Kommunikation mit Server
        // EmpfaengerThread erstellen
        empfaengerRunner = new Empfaenger(ois, clientSocket);
        empfaengerRunner.setController(this.controller);
        empfaengerThread = new Thread(empfaengerRunner);

        senderThread.start(); // ServerHandlerThread zum Leben erwecken
        empfaengerThread.start();
        
        
        

        //Heartbeat auslagern
        heartBeatThread = new Thread()
        {

            public void run()
            {
                Nachricht testNachricht = new Nachricht(clientName, "$$$$$$7$");

                while (tryToReconnect)
                {
                    // send a test signal
                    try
                    {
                        oos.writeObject(testNachricht);
                        oos.flush();
                        // System.out.println("Heartbeat.run(): Testsignal
                        // versand!");
                        sleep(6000);
                    }
                    catch (InterruptedException e)
                    {
                        controller.errorDialog(e);
                        System.err.println("Verbindung unterbrochen - Timeout");
                        exit();
                    }
                    catch (IOException e)
                    {
                        controller.errorDialog(e);
                        System.err.println("Server kaputt");
                        // Verbindung resetten
                        //test
                        //stopThreads();
                        // entblockt den UI_Thread zum Updaten der Oberfl�che
                        Platform.runLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                verbinde();

                            }
                        });

                        starteThreads();
                        try
                        {
                            sleep(2000);
                        }
                        catch (InterruptedException e1)
                        {
                            e1.printStackTrace();
                        } // trycatch
                    } // try catch
                } // while
                System.out.println("HearbeatThread.run(): beendet");
            } //run
        };//Heartbeat

        heartBeatThread.start();
        

    }// starteThreads

    public boolean stopThreads()
    {
        try
        {
            if (senderRunner != null && senderRunner.isRunning())
                senderRunner.stop();
            if (empfaengerRunner != null && empfaengerRunner.isRunning())
                empfaengerRunner.stop();
            if (heartBeatThread != null && tryToReconnect)
            	tryToReconnect = false;
            System.out.println("Client.stopThreads(): Threads wurden gestoppt");
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Client.stopThreads(): Konnte Runner (Sender/Empf�nger) nicht stoppen");
            return false;
        } // trycatch
    }// stopThreads

    // Verbindung schlie�en
    private boolean closeConnection()
    {
        try
        {
            // Outputstreams schlie�en
            if (ois != null)
                ois = null;
            if (oos != null)
                oos = null;
            // SOcket schlie�en
            
            //DEV
            if (clientSocket != null)
                clientSocket.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        } // trycatch
    }// closeConnection

    // komplettes Beenden des Programmes
    public void exit()
    {
        stopThreads();
        closeConnection();
    }// exit

    // Getter

    // Nutzername auslesen
    public String getName()
    {
        return this.clientName;
    }// getName

    // ObjectOutputStream
    public ObjectOutputStream getObjectOutputStream()
    {
        return this.oos;
    }// getObjectOutputStream


}// Client Class
