package de.ergodirekt.dualestudenten.notenrechner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    Pane rootPane;

    public void processLoadExisting() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../layout/gradeTable.fxml"));
            Parent gradeTable = loader.load();
            GradeTableController gradeTableController = loader.getController();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(gradeTable, 650, 500));

            gradeTableController.openExistingGradeCollection();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processNewGradeCollection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../layout/gradeTable.fxml"));
            Parent gradeTable = loader.load();
            GradeTableController gradeTableController = loader.getController();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(gradeTable, 650, 500));

            gradeTableController.createNewGradeCollection();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
