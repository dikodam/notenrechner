package de.ergodirekt.dualestudenten.notenrechner;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType;

public class GradeTableController {

    private static final int SAVE = 0;
    private static final int OPEN = 1;


    private ObservableList<Grade> tableData = FXCollections.observableArrayList();
    private FilteredList<Grade> filteredList = new FilteredList<>(tableData, p -> true);

    private File saveFile;

    @FXML
    private TableView<Grade> gradeTable;
    @FXML
    private TableColumn<Grade, String> fullNameColumn;
    @FXML
    private TableColumn<Grade, String> shortNameColumn;
    @FXML
    private TableColumn<Grade, Double> gradeColumn;
    @FXML
    private TableColumn<Grade, Integer> ectsColumn;
    @FXML
    private TableColumn<Grade, Integer> semesterColumn;
    @FXML
    private TableColumn<Grade, Double> loadColumn;
    @FXML
    private TextField tfSearchBar;
    @FXML
    private Label lblGradeMean;
    @FXML
    private Label lblName;
    @FXML
    private HBox inputFields;
    @FXML
    private TextField tfFullName;
    @FXML
    private TextField tfShortName;
    @FXML
    private TextField tfGrade;
    @FXML
    private TextField tfEcts;
    @FXML
    private TextField tfLoad;
    @FXML
    private TextField tfSemester;
    @FXML
    private Pane rootPane;

    public GradeTableController() {
    }

    @FXML
    @SuppressWarnings("unused")
    private void initialize() {
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        shortNameColumn.setCellValueFactory(new PropertyValueFactory<>("shortName"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        ectsColumn.setCellValueFactory(new PropertyValueFactory<>("ects"));
        loadColumn.setCellValueFactory(new PropertyValueFactory<>("load"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));

        tfSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Grade grade) -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (grade.getFullName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (grade.getShortName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (Double.toString(grade.getGrade()).contains(lowerCaseFilter)) {
                    return true;
                } else if (Double.toString(grade.getEcts()).contains(lowerCaseFilter)) {
                    return true;
                } else if (Double.toString(grade.getLoad()).contains(lowerCaseFilter)) {
                    return true;
                } else if (Integer.toString(grade.getSemester()).contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
            updateGradeMean();
        });

        SortedList<Grade> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(gradeTable.comparatorProperty());
        gradeTable.setItems(sortedList);

        updateGradeMean();
    }

    private void updateGradeMean() {
        double weightedGradeSum = 0.0;
        double loadSum = 0.0;
        double ectsSum = 0.0;

        for (Grade grade : filteredList) {
            if (grade.getGrade() != 0.0) {
                weightedGradeSum += grade.getGrade() * grade.getLoad();
                loadSum += grade.getLoad();
                ectsSum += grade.getEcts();
            }
        }
        DecimalFormat df = new DecimalFormat("#.0");
        df.setRoundingMode(RoundingMode.HALF_UP);

        String mean;

        if (loadSum != 0) {
            mean = df.format(weightedGradeSum / loadSum);
        } else {
            mean = "0.0";
        }
        df.applyPattern("##0.#");
        String ects = df.format(ectsSum);
        Platform.runLater(() -> lblGradeMean.setText(String.format("Notendurchschnitt: %s | ECTS: %s", mean, ects)));
    }

    private void showAlertBox(String titel, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titel);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void processEditButton() {
        Grade selectedGrade = gradeTable.getSelectionModel().getSelectedItem();

        if (selectedGrade != null) {
            tfFullName.setText(selectedGrade.getFullName());
            tfShortName.setText(selectedGrade.getShortName());
            tfGrade.setText(String.valueOf(selectedGrade.getGrade()));
            tfEcts.setText(String.valueOf(selectedGrade.getEcts()));
            tfLoad.setText(String.valueOf(selectedGrade.getLoad()));
            tfSemester.setText(String.valueOf(selectedGrade.getSemester()));
        }
    }

    public void processSaveButton() {
        try {
            Grade grade = validateInputFields();
            Grade selectedGrade = gradeTable.getSelectionModel().getSelectedItem();

            if (selectedGrade != null) {

                selectedGrade.setFullName(grade.getFullName());
                selectedGrade.setShortName(grade.getShortName());
                selectedGrade.setGrade(grade.getGrade());
                selectedGrade.setEcts(grade.getEcts());
                selectedGrade.setLoad(grade.getLoad());
                selectedGrade.setSemester(grade.getSemester());

                gradeTable.refresh();
                updateGradeMean();
                persist();
            }
        } catch (IllegalArgumentException e) {
            showAlertBox("Eingabefehler", e.getMessage());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void processAddButton() {
        try {
            if (saveFile == null) {
                showAlertBox("Fehler", "Keine Speicherdatei hinterlegt, bitte erst neues Notenblatt anlegen!");
            } else {
                Grade newGrade = validateInputFields();
                tableData.add(newGrade);
                updateGradeMean();
                persist();
            }
        } catch (IllegalArgumentException e) {
            showAlertBox("Eingabefehler", e.getMessage());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void processClearButton() {
        for (Node child : inputFields.getChildren()) {
            ((TextField) child).setText("");
        }
    }

    public void processDeleteButton() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Löschen bestätigen");
        alert.setHeaderText(null);
        alert.setContentText("Soll der ausgewählte Datensatz wirklich gelöscht werden?");

        Grade selectedGrade = gradeTable.getSelectionModel().getSelectedItem();

        if (selectedGrade != null) {

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                tableData.remove(selectedGrade);
                updateGradeMean();
            }
        }
        try {
            persist();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void persist() throws JAXBException {
        try {
            JAXBContext context = JAXBContext.newInstance(GradeCollection.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setSchema(getSchema());
            marshaller.setProperty("jaxb.formatted.output", true);

            GradeCollection collection = new GradeCollection();
            collection.setName(lblName.getText());
            collection.setGradelist(tableData);

            marshaller.marshal(collection, saveFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw e;
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    private Grade validateInputFields() {
        boolean fullNameValid = true;
        boolean gradeValid = true;
        boolean ectsValid = true;
        boolean loadValid = true;
        boolean semesterValid = true;

        // parse full name
        String fullName = tfFullName.getText();
        if ("".equals(fullName)) {
            fullNameValid = false;
        }

        // parse short name
        String shortName = tfShortName.getText();

        // parse grade
        String tfGradeText = tfGrade.getText();
        double grade = -1.0;
        if ("".equals(tfGradeText)) {
            grade = 0.0;
        } else {
            try {
                grade = Double.parseDouble(tfGradeText);
            } catch (NumberFormatException e) {
                gradeValid = false;
            }
        }
        if (grade < 1.0 || grade > 5) {
            gradeValid = false;
        }

        // parse ects
        String tfEctsText = tfEcts.getText();
        double ects = -1.0;
        try {
            ects = Double.parseDouble(tfEctsText);
        } catch (NumberFormatException e) {
            ectsValid = false;
        }
        if (ects < 0) {
            ectsValid = false;
        }

        // parse load
        String tfLoadText = tfLoad.getText();
        double load = -1.0;
        if ("".equals(tfLoadText)) {
            load = 0.0;
        } else {
            try {
                load = Double.parseDouble(tfLoadText);
            } catch (NumberFormatException e) {
                loadValid = false;
            }
        }
        if (load < 0) {
            loadValid = false;
        }

        // parse semester
        String tfSemesterText = tfSemester.getText();
        int semester = -1;
        try {
            semester = Integer.parseInt(tfSemesterText);
        } catch (NumberFormatException e) {
            semesterValid = false;
        }
        if (semester < 0) {
            semesterValid = false;
        }

        // validation
        if (!fullNameValid || !gradeValid || !ectsValid || !loadValid || !semesterValid) {
            StringBuilder builder = new StringBuilder("Falsche Eingabe bei folgenden Eingabefeldern:\n");
            if (!fullNameValid) {
                builder.append("voller Name\n");
            }
            if (!gradeValid) {
                builder.append("Note\n");
            }
            if (!ectsValid) {
                builder.append("ECTS\n");
            }
            if (!loadValid) {
                builder.append("Gewichtung\n");
            }
            if (!semesterValid) {
                builder.append("Semester\n");
            }
            throw new IllegalArgumentException(builder.toString());
        } else {
            Grade newGrade;
            if (load <= 0) {
                newGrade = new Grade(fullName, shortName, grade, ects, semester);
            } else {
                newGrade = new Grade(fullName, shortName, grade, ects, semester, load);
            }
            return newGrade;
        }
    }

    /**
     * @param option GradeTableControlller.OPEN or GradeTableControlller.SAVE
     * @return true if a file was chosen
     */
    private boolean createSaveFile(int option) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File chosenFile = null;
        switch (option) {
            case OPEN:
                chosenFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
                break;
            case SAVE:
                chosenFile = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
                break;
            default:
                break;
        }
        if (chosenFile != null) {
            saveFile = chosenFile;
            return true;
        }
        return false;
    }


    private Schema getSchema() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(new File("target/classes/schema/GradeCollectionSchema.xsd"));
    }

    private void fillTableFromFile() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(GradeCollection.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(getSchema());
            GradeCollection gradeCollection = (GradeCollection) unmarshaller.unmarshal(saveFile);
            String name = gradeCollection.getName();
            List<Grade> grades = gradeCollection.getGradelist();
            tableData.setAll(grades);
            Platform.runLater(() -> lblName.setText(name));
            updateGradeMean();
        } catch (JAXBException | SAXException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void createNewGradeCollection() {
        Optional<String> result = inputName();
        if (result.isPresent()) {
            if (createSaveFile(SAVE)) {
                Platform.runLater(() -> lblName.setText(result.get()));
                tableData.clear();
            }
        }
    }

    private Optional<String> inputName() {
        TextInputDialog inputDialog = new TextInputDialog("");
        inputDialog.getEditor().setPromptText("Name...");
        inputDialog.setTitle("Name eingeben");
        inputDialog.setHeaderText(null);
        inputDialog.setContentText("Name der neuen Notentabelle eingeben:");
        Optional<String> name = inputDialog.showAndWait();
        return name;
    }

    public void openExistingGradeCollection() {
        if (createSaveFile(OPEN)) {
            tableData.clear();
            fillTableFromFile();
        }
    }

    public void saveAs() {
        if ("".equals(lblName.getText())) {
            Optional<String> name = inputName();
            if (!name.isPresent()) {
                return;
            }
            // FIXME: 14.03.2017 "" als name abfangen
            Platform.runLater(() -> lblName.setText(name.get()));
        }
        try {
            if (createSaveFile(SAVE)) {
                persist();
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
