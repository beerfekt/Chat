package mvc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{

    public void start(Stage primaryStage) throws Exception
    {

        Parent root = FXMLLoader.load(getClass().getResource("/mvc/ChatClientMessageWindow.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Chat-Client");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(350);
        // primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();

    }

    public static void main(String[] args)
    {
        launch(args);
    }

}