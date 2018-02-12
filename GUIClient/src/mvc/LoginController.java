package mvc;

import java.net.URL;
import java.util.ResourceBundle;

import anwendung.Client;
import anwendung.HostData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
//import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

public class LoginController implements Initializable
{

    @FXML
    private Label label;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField serverIPTextField;

    @FXML
    private Model model;

    private Client client;

    public void setModel(Model model)
    {
        this.model = model;
    }

    public Model getModel()
    {
        return model;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }

    public Client getClient()
    {
        return client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // model = new Model();
    }

    public void manageButton()
    {

        label.setText(model.getIP());
    }

    @FXML
    protected void setOnLoginClicked(ActionEvent event) throws Exception
    {
        String ip, username;
        String serverIP;

        // uebergibt den Username an das Model
        username = usernameTextField.getText();
        model.setUsername(username);

        // uebergibt die User-IP an das Model
        // HostData hostData = new HostData();
        ip = HostData.getHost();
        System.out.println(ip);
        model.setIP(ip);

        // uebergibt die ServerIP an das Model
        serverIP = serverIPTextField.getText();
        model.setServerIP(serverIP);

        // setzt den Connection Wert auf true
        model.setConnected(true);

        // Schliesst das Login Pop-UP
        ((Node) (event.getSource())).getScene().getWindow().hide();

    }

}