package de.ergodirekt.dualestudenten.notenrechner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent mainMenuView = FXMLLoader.load(getClass().getResource("/layout/mainMenu.fxml"));

        primaryStage.setTitle("Notenverwaltung");
        primaryStage.setScene(new Scene(mainMenuView, 250, 200));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
