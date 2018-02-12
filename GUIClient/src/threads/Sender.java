package threads;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import anwendung.Nachricht;
import mvc.Controller;

public class Sender implements Runnable
{

    // Daten für Client
    // TODO
    // Objekt übergeben oder nutzername entfernen ( Im String enthalten)
    private Socket socket;

    private boolean running;

    // Zu versendende Nachrichten
    private LinkedList<Nachricht> nachrichten;

    private ObjectOutputStream oos;

    private Controller controller;

    // Konstruktor mit Namen und Socket
    public Sender(Socket socket, ObjectOutputStream oos)
    {
        this.socket = socket;
        this.oos = oos;
        nachrichten = new LinkedList<Nachricht>();
        running = true;
    }// ServerRunner

    // 1. Nachrichten an Liste anhängen, LinkedList da effizienter?
    public void addNachricht(Nachricht nachricht)
    {
        synchronized (nachrichten)
        {
            nachrichten.push(nachricht);
        } // synchronized
    }// addNachricht

    //
    @Override
    public void run()
    {

        try
        {
            while (!socket.isClosed() && running)
            {

                // ------------- Nachrichten-Versand: -------------------------
                if (nachrichten != null && !nachrichten.isEmpty())
                {
                    // synchronized (nachrichten) { //stellt sicher dass die
                    // befehle innerhalb des synchronized blocks nacheinander
                    // ohne durchwürfelung abgearbeitet werden
                    synchronized (nachrichten)
                    {
                        Nachricht naechsteNachricht = nachrichten.pop();
                        System.out.println("Sender.run(): Nachricht wurde aus Liste gepopt");
                        oos.writeObject(naechsteNachricht);

                        System.out.println("Sender.run(): Nachricht wurde in Objekt geschrieben");
                        oos.flush();
                        System.out.println("Sender.run(): Nachricht wurde geflusht");
                    } // synchronized
                } // if (nachrichten vorhanden)

            } // while
        }
        catch (IOException e)
        {
            controller.errorDialog(e);
            e.printStackTrace();
        }
        // Disabled die UI falls die Verbidnung eines fr�heren Versuchs
        // funktioniert hat und nun fehlschl�gt
        catch (NullPointerException e)
        {
            controller.errorDialog(e);
            System.out.println("Nullpointer gefangen");
            controller.disconnect();
            e.printStackTrace();
        }
    }// run()

    public Socket getSocket()
    {
        return socket;
    }// getSocket

    public void stop()
    {
        this.running = false;
    }

    public boolean isRunning()
    {
        return running;
    }// connected
     // ---------

    public void setController(Controller controller)
    {
        this.controller = controller;

    }

}// class
