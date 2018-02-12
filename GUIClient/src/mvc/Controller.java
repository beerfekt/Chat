package mvc;

import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
//import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import anwendung.Client;
import anwendung.Nachricht;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
//import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller implements Initializable
{

    @FXML
    private Label label;

    @FXML
    private Button send;

    @FXML
    private TextField message;

    @FXML
    private Model model;

    @FXML
    private VBox vBox;

    @FXML
    private VBox messageArea;

    @FXML
    private Label serverIPLabel;

    @FXML
    private MenuItem disconnect;

    @FXML
    private Label clientIPLabel;

    @FXML
    private ScrollPane scrollPane;

    private int counter = 0;

    private Client client;

    private boolean verbindung = false;

    // private ArrayList<Nachricht> nachrichtenStack = new ArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        model = new Model();
        init();
        initDisableUI();
        // clientApp = new ClientApp(model);
    }

    // setzt den EventFilter zum Senden durch Druck auf ENTER
    private void init()
    {
        scrollPane.setVvalue(0.0);

        vBox.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
        {
            public void handle(KeyEvent ke)
            {
                if (ke.getCode() == KeyCode.ENTER)
                {
                    setOnSendClicked(null);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });

        // Meldet einen Listener an die Heigh-Property des Message Felds an
        messageArea.heightProperty().addListener(new ChangeListener<Object>()
        {
            // wird bei einer �nderung der Property aufgerufen
            @Override
            public void changed(ObservableValue<?> observable, Object oldvalue, Object newValue)
            {
                // Bei 4 oder mehr Nachrichten soll das ScrollPane zum Boden
                // scrollen
                if (counter >= 4)
                {
                    // Scrollt zum letzten Eintrag des Feldes
                    scrollPane.setVvalue(1.0);
                }
            }
        });
    }

    // Graut beim start die Oberfl�che aus
    public void initDisableUI()
    {
        messageArea.setDisable(true);
        send.setDisable(true);
        message.setDisable(true);
        clientIPLabel.setDisable(true);
        serverIPLabel.setDisable(true);

        clientIPLabel.setText("Verbunden als: ");
        serverIPLabel.setText("Verbunden mit: ");

        disconnect.setDisable(true);
        messageArea.getChildren().clear();
    }

    // Setzt Enabled Werte nach Login auf true
    public void enableUI()
    {
        messageArea.getChildren().clear();
        messageArea.getChildren().clear();
        messageArea.setDisable(false);
        send.setDisable(false);
        message.setDisable(false);
        clientIPLabel.setDisable(false);
        serverIPLabel.setDisable(false);

        // zeigt dem User seinen Namen mit seiner IP Adresse
        clientIPLabel.setText("Verbunden als: " + model.getUsername() + " - IP:" + model.getIP());

        // zeigt dem User auf welchem Server er eingeloggt ist
        serverIPLabel.setText("Verbunden mit: " + model.getServerIP());

        // Erlaubt das ausloggen
        disconnect.setDisable(false);
    }

    // Methode zum L�schen des Nachrichten-Textfields
    public void clearTextField()
    {
        message.clear();
    }

    @FXML
    public void connect() throws Exception
    {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mvc/ClientLoginFXML.fxml"));
        VBox vBox = loader.load();
        // Get the Controller from the FXMLLoader
        LoginController loginController = loader.getController();

        Scene scene = new Scene(vBox);
        Stage stage = new Stage();

        // Set data in the controller
        loginController.setModel(model);

        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Chat-Client");
        stage.setScene(scene);
        stage.showAndWait();

        try
        {
            tryConnection();

        }
        catch (ConnectException e)
        {
            System.out.println("disable");
            errorDialog(e);
        }

        if (verbindung)
        {
            enableUI();
        }

    }

    public void tryConnection() throws ConnectException
    {
        // //Singleton anwendung
        // client = Client.getInstance(model.getUsername(), model.getServerIP(),
        // 55555, this);
        client = new Client(model.getUsername(), model.getServerIP(), 55555, this);
        System.out.println("tryConnection");
        verbindung = true;
    }

    // loggt den Nutzer vom Server aus
    @FXML
    public void disconnect()
    {
        counter = 0;
        initDisableUI();
        client.exit();
    }

    @FXML
    public void close()
    {
        try
        {
            client.exit();
        }
        catch (Exception e)
        {
            System.err.println("Client wurde nicht erstellt, Programm wird dennoch beendet!");
        }
        Platform.exit();
    }

    // public void showMessage(Nachricht nachricht)
    // {
    //
    //
    // for (int i = 0; i < nachrichtenStack.size(); i++)
    // {
    // if (nachrichtenStack.get(i).getName().equals(nachricht.getName()) &&
    // nachrichtenStack.get(i).getText().equals(nachricht.getText()))
    // {
    // System.err.println("reached ShowMessage");
    // nachricht.setProgressStatus(100);
    // nachrichtenStack.remove(i);
    // }
    // else
    // {
    // messageArea.getChildren().addAll(nachricht.nachrichtInit());
    //
    // if (nachricht.getName().equals(model.getUsername()))
    // {
    // nachricht.setBackgroundColor(true);
    // }
    // else
    // {
    // nachricht.setBackgroundColor(false);
    // }
    //
    // nachrichtenStack.add(nachricht);
    // System.err.println("inkrementiere stack");
    // }
    // }
    //
    // }

    public void showMessage(Nachricht nachricht)
    {
        // if (!(nachricht.getText().equals("server")) &&
        // (nachricht.getText().equals("$$$=§§§=")))
        // {
        System.out.println("reached ShowMessage");
        // nachricht.setProgressStatus(100);
        messageArea.getChildren().addAll(nachricht.nachrichtInit());
        // Legt Nachricht auf nachrichtenstack falls sie sich noch nicht
        // darauf
        // befindet
        // nachrichtenStack.add(nachricht);

        if (nachricht.getName().equals(model.getUsername()))
        {
            nachricht.setBackgroundColor(true);
        }
        else
        {
            nachricht.setBackgroundColor(false);
        }
        counter++;
    }

    public void errorDialog(Exception e)
    {
        System.out.println("Error Called");

        if (e instanceof ConnectException || e instanceof UnknownHostException)
        {
            System.err.println("Problem!");
            Alert alert = new Alert(AlertType.ERROR, "Sever nicht erreichbar!", ButtonType.OK);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK)
            {
                disconnect();
                verbindung = false;
            }
        }
        if (e instanceof NamingException)
        {
            Alert alert = new Alert(AlertType.ERROR, "Nutzername bereits vergeben. \nBitte w�hlen Sie einen anderen.", ButtonType.OK);
            verbindung = false;
            disconnect();
            alert.showAndWait();
        }
    }

    @FXML
    protected void setOnSendClicked(ActionEvent event)
    {
        String messageString = message.getText();

        // uebergibt den Message String an den Client
        client.nutzeVerbindung(messageString);


        this.message.setOnAction(e -> clearTextField());
        clearTextField();

    }

    public void generiereNachricht(Nachricht nachricht)
    {
        showMessage(nachricht);
    }

    public void neuerNutzerName()
    {
        verbindung = false;
        NamingException ne = new NamingException();
        errorDialog(ne);
        // initDisableUI();

    }

}
