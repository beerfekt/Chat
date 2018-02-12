package anwendung;

public class ServerApp {

	public static void main(String[] args) {			
		//Beenden
		if (args.length > 0 && args[0].equals("exitserver")) {
			System.out.println("Server beendet!");
			System.exit(0);
		}//if		
		//Server erstellen
		Server server  = Server.getInstance();
		server.starte();
	}//main
}//ServerApp



