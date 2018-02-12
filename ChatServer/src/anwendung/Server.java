package anwendung;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


import threads.ClientRunner;


public class Server {

	private int portnummer;
	private List<ClientRunner> clients;
	//private List<ClientRunner> neueCLients;
	
	
//Singleton Entwurfsmuster
	
    private static final Server instance = new Server();

    private Server() {
    	this.portnummer = 55555;
    }
    
    public static Server getInstance(){
        return instance;
    }
	
//Singleton
	
	public List<ClientRunner> getClients() {
		return clients;
	}	

	void starte() {		
		//1. Clientlist und ServerSocket erstellen		
		clients = new ArrayList<ClientRunner>();	
		try {
			//2. ServerSocket nutzen um Clients anzunehmen
			try {
				acceptClients( new ServerSocket(portnummer) );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}//try catch
	}//starte
		

	//Clients annahme - und je Client einen Thread erstellen/starten und in liste speichern
	private void acceptClients(ServerSocket serverSocket) throws InterruptedException {
		while( true ) {
			try {
				System.out.println("Server startet - port: " + serverSocket.getLocalSocketAddress());
				//1. Liste aktualisieren und inaktive Threads rauswerfen
				checkForActiveClients();					

				//3.Socket des Anfragenden Clients erhalten
				Socket clientSocket = serverSocket.accept();					
				//4. mit erhaltenem Socket und Chatserver einen Client-Runner erstellen
				ClientRunner empfaengerRunner = new ClientRunner(clientSocket, this);
				//6. Clientrunner an neu erstellten Thread übergeben
				Thread empfaengerThread = new Thread(empfaengerRunner);	
				//7.name noch frei: Thread starten
				empfaengerThread.start();					
				//8. und an Liste übergeben
				clients.add(empfaengerRunner);				
				//8. Warten bis nötige Infos über den Thread eintrudeln
				Thread.sleep(1000);
				
				//9.Wiederholt Liste aktualisieren und inaktive Threads rauswerfen
				//checkForActiveClients();				
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Konnte Client nicht annehmen - Serverport :" + portnummer);
			}//try catch
		}//while		
	}//acceptClients	
	

	
	//Nach Anmeldung Liste auf Inaktive ClientRunner überprüfen und geg. aus Liste entfernen
	private void checkForActiveClients() {
		//falls liste leer
		if (clients.isEmpty()) {
			System.out.println("Keine Clients angemeldet!");
			return;
		}		
		//wenn liste Clients enthält:
		System.out.println("\n --------- Angemeldete Clients:-------");		
		ClientRunner currentClient = null;		
		for (int i = 0; i < clients.size(); i++) {
			currentClient = clients.get(i);			
			if (currentClient == null) break;
			if (currentClient.isRunning()) {
				System.out.print("- Client: is Running? : " + currentClient.isRunning());	
				System.out.println(" - Client" + i + " " + currentClient.getName());	
			} else {
				System.out.println("Entferne Client:" + currentClient);
				clients.remove(i);		
			}//ifelse		
		}//for	
		System.out.println("------------------------------------");
	}//checkForActiveClients
	
}//class Server

