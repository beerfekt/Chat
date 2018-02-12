package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import anwendung.Nachricht;
import javafx.application.Platform;
import mvc.Controller;

public class Empfaenger implements Runnable
{

    private Socket socket;

    private ObjectInputStream ois;

    private boolean running;

    private Controller controller;


    public Empfaenger(ObjectInputStream ois, Socket socket)
    {
        this.ois = ois;
        this.socket = socket;
        this.running = true;
    }

    @Override
    public void run()
    {
        Nachricht nachrichtEmpfangen = null;
        System.out.println("Receiver.run(): warte auf Nachricht...");
        while (!socket.isClosed() && running && ois != null)
        {
            try
            {
                // Empfang der Nachricht
                try
                {
                    nachrichtEmpfangen = (Nachricht) (ois.readObject());
                }
                catch (SocketException e)
                {

                }
                // StoppNachricht
                // Wenn stopp nachricht eintrudelt (weil name schon vergeben
                // etc.) dann stoppe programm
                // TODO: System.exit(0) ist etwas hart, evt. andere L�sung
                //
                if (nachrichtEmpfangen.getName().equals("server") && nachrichtEmpfangen.getText().equals("$$$=§§§="))
                {
                    System.out.println("server hat nutzernamen geworfen");
                    Platform.runLater(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            controller.neuerNutzerName();

                        }
                        

                    });
                    return;
                }
                else
                {

                    // Verarbeitung der Nachricht
                    System.out.println("Receiver.run()-1: ");
                    System.out.println("     " + nachrichtEmpfangen.getName() + "->" + nachrichtEmpfangen.getText());

                    // neues NachrichtenObjekt erzeugen um an Controller zu
                    // �bergeben
                    Nachricht returnNachricht = nachrichtEmpfangen;

                    Platform.runLater(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            controller.generiereNachricht(returnNachricht);

                        }

                    });

                }
            }
            catch (ClassNotFoundException | IOException e)
            {
                e.printStackTrace();
                System.out.println("Empfaenger.run() stoppe Thread");
                // amoklauf des Threads verhindern
                running = false;
            } // try catch

        } // while
    }// run

    // Sehen ob Thread l�uft
    public boolean isRunning()
    {
        return running;
    }

    // Stopper f�r Thread
    public void stop()
    {
        this.running = false;
    }

    public void setController(Controller controller)
    {
        this.controller = controller;

    }

}
