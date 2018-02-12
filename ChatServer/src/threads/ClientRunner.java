package threads;

//import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import anwendung.Nachricht;
import anwendung.Server;

public class ClientRunner implements Runnable
{

    private Server chatServer;

    // Basics
    private Socket socket;

    private boolean isRunning; // falls keine antwort versendet werden kann
                               // (flush())
    // evt. hier namen des clients übergeben

    private String nutzerName;

    // Senden/Empfangen
    private ObjectOutputStream oos;

    private ObjectInputStream ois;

    public ClientRunner(Socket serverSocket, Server chatServer) throws IOException
    {
        this.socket = serverSocket;
        this.chatServer = chatServer;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        isRunning = true;
    }// ClientRunner

    @Override
    public void run()
    {
        try
        {
            Nachricht nachricht = null;

            nachricht = (Nachricht) (ois.readObject());
            System.out.println("ClientRunner.run(): Nachricht eingelesen! name :" + nachricht.getName() + "->" + nachricht.getText());

            // Nutzernamen auf verwendbarkeit prüfen
            // Prüfung ob Thread mit gleichem Namen schon läuft
            if (nutzerName == null)
            {
                nutzerName = nachricht.getName();
                if (nameVorhanden(nutzerName))
                {
                    isRunning = false;
                } // if
            } // if

            while (!socket.isClosed() && isRunning)
            {
                // Empfangen
//                try
//                {
                    nachricht = (Nachricht) (ois.readObject());
                    System.out.println("ClientRunner.run(): Nachricht eingelesen! name :" + nachricht.getName() + "->" + nachricht.getText());
                //}
                //dauerschleife
//                catch (EOFException e)
//                {
//                    //this.chatServer.checkForActiveClients();
//                    System.out.println("Client Abgemeldet");
//                }

                // //Nutzernamen auf verwendbarkeit prüfen
                // //Prüfung ob Thread mit gleichem Namen schon läuft
                // if (nutzerName == null){
                // nutzerName = nachricht.getName();
                // if (nameVorhanden( nutzerName )){
                // isRunning = false;
                // }//if
                // }//if

                // Verarbeitung
                if (nachricht != null && !nachricht.getText().equals("$$$$$$7$"))
                {
                    if (chatServer.getClients() != null && chatServer.getClients().isEmpty())
                    {
                        System.out.println("keine Clients ");
                    }
                    else
                    {
                        for (ClientRunner dieserClient : chatServer.getClients())
                        {
                            System.out.println("Clientrunner.run(): for (clients): " + dieserClient.getName() + " ");
                            if (dieserClient != null && dieserClient.isRunning())
                            {
                                System.out.println("  Clientrunner.run(): for (clients): sende an:" + dieserClient.getName());
                                sendeNachricht(dieserClient, nachricht);
                            }
                            else
                            {
                                dieserClient.stop();
                            }
                        } // for
                    } // if/else
                } // if
            } // while
        }
        catch (IOException e)
        {
            isRunning = false;
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } // trycatch
    }// run()

    // TODO
    // Namensprüfung
    public boolean nameVorhanden(String name) throws IOException
    {
        if (chatServer.getClients() == null)
            return false;

        for (ClientRunner dieserClient : chatServer.getClients())
        {
            System.out.println("ClientRunner.run().nameVorhanden(): Prüfe:" + dieserClient.getName() + "<->" + nutzerName);
            if (!dieserClient.equals(this) && dieserClient.getName().equals(nutzerName))
            {
                System.out.println("ClientRunner.run().nameVorhanden(): Nutzername vorhanden, beende client:" + dieserClient.getName());
                sendeNachricht(this, new Nachricht("server", "$$$=§§§="));
                System.out.println("ClientRunner.run().nameVorhanden().syso(): server,§§§=§§§=");
                // Bescheid geben
                // muss nicht geprintet werdem, da Fehlermeldung
                // sendeNachricht(this, new Nachricht("server", "Nutzername
                // bereits vergeben, Bitte neu verbinden!"));
                // Beenden Code an Client schicken der sich dann beendet
                System.out.println("ClientRunner.run().nameVorhanden().syso(): server,Nutzername bereits vergeben, Bitte neu verbinden!");

                return true;
            }
            else
            {
                System.out.println("ClientRunner.run().nameVorhanden():Name nicht vorhanden - Übernehme diesen " + nutzerName);
                return false;
            } // if else
        } // for
        return false;
    }// nameVorhanden

    // versenden der Nachricht
    public void sendeNachricht(ClientRunner dieserClient, Nachricht nachricht) throws IOException
    {
        ObjectOutputStream clientOOS = dieserClient.getObjectOutput();
        clientOOS.writeObject(nachricht);
        clientOOS.flush();
    }

    public String getAddress()
    {
        return this.socket.getInetAddress().toString();
    }// getNamer()

    public String getName()
    {
        return this.nutzerName;
    }

    public ObjectOutputStream getObjectOutput()
    {
        return oos;
    }

    public boolean isRunning()
    {
        return isRunning;
    }// isConnected

    public void stop()
    {
        this.isRunning = false;
    }
}// class
