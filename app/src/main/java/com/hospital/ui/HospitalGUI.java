package com.hospital.ui;

import com.hospital.*;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class HospitalGUI extends Application {

  private final ObservableList<Doctor> allDoctors = FXCollections.observableArrayList();
  private final ObservableList<Paitent> allPatients = FXCollections.observableArrayList();

  private ListView<Doctor> doctorListView;
  private ComboBox<Doctor> doctorSelectionBox;
  private ComboBox<Paitent> patientSelectionBox;

  private ListView<Appointment> apptListView;
  private ComboBox<String> dateFilter;
  private DatePicker specificDatePicker;
  private Timeline guiRefreshTimer;
  private LocalDateTime formOpenedTime = null;

  private DatePicker apptStartDate;
  private TextField apptStartTime;
  private DatePicker apptEndDate;
  private TextField apptEndTime;
  private TextField apptIllness;
  private CheckBox apptEmergencyCheck;
  private ComboBox<String> apptTypeCombo;
  private ComboBox<Operationtype> apptSurgeryTypeCombo;
  private Label apptSurgeryTypeLabel;
  private Button openAddApptBtn;

  private void setUpTimer(int durationInSec) {
    this.guiRefreshTimer = new Timeline(new KeyFrame(Duration.seconds(durationInSec), e -> {
      updateAppointmentStatuses();
      if (apptListView != null)
        apptListView.refresh();
      if (doctorListView != null)
        doctorListView.refresh();
    }));
    System.out.println("timer guiRef is init,duration " + durationInSec);
    guiRefreshTimer.setCycleCount(Timeline.INDEFINITE);
    guiRefreshTimer.play();

  }

  private void updateAppointmentStatuses() {
    LocalDateTime now = LocalDateTime.now();
    for (Doctor doctor : allDoctors) {
      for (Appointment appt : doctor.getAppointments()) {
        if (appt.getStatus() == Status.canceled || appt.getStatus() == Status.completed) {
          continue;
        }

        if (now.isAfter(appt.getEndTime())) {
          appt.setStatus(Status.completed);
        } else if (now.isAfter(appt.getStartTime()) || now.isEqual(appt.getStartTime())) {
          appt.setStatus(Status.onGoing);
        }
      }
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    TabPane tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

    tabPane.getTabs().add(createManagementTab());
    tabPane.getTabs().add(createAppointmentsTab());

    BorderPane root = new BorderPane();

    HBox topBar = new HBox();
    topBar.setAlignment(Pos.CENTER_RIGHT);
    topBar.setPadding(new Insets(5, 15, 5, 15));

    ToggleButton darkModeToggle = new ToggleButton("Dark Mode");
    darkModeToggle.setOnAction(e -> {
      if (darkModeToggle.isSelected()) {
        root.setStyle("-fx-base: #2b2b2b;");
        darkModeToggle.setText("Light Mode");
      } else {
        root.setStyle("");
        darkModeToggle.setText("Dark Mode");
      }
    });
    topBar.getChildren().add(darkModeToggle);

    setUpTimer(5);

    root.setTop(topBar);
    root.setCenter(tabPane);
    Scene scene = new Scene(root, 950, 650);
    primaryStage.setTitle("Hospital Management System");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void setupDocTab(Tab tab) {
    SplitPane docSplit = new SplitPane();

    VBox docListPanel = new VBox(10);
    docListPanel.setPadding(new Insets(10));
    ListView<Doctor> mgmtDocList = new ListView<>(allDoctors);
    mgmtDocList.setCellFactory((param) -> new ListCell<Doctor>() {
      @Override
      protected void updateItem(Doctor doc, boolean empty) {
        super.updateItem(doc, empty);
        if (empty || doc == null)
          setText(null);
        else {
          String type = doc instanceof SurgeonDoctor ? "Surgeon"
              : doc instanceof SpecialistDoctor ? "Specialist" : "";
          setText(doc.getFormattedName() + " | " + doc.getSpeciality() + " " + type + " (" + doc.getId() + ")");

        }
      }
    });

    Button addDocBtn = new Button("+ Add Doctor");
    Button editDocBtn = new Button("✎ Edit");
    HBox docToolbar = new HBox(5, addDocBtn, editDocBtn);

    docListPanel.getChildren().addAll(docToolbar, mgmtDocList);
    VBox.setVgrow(mgmtDocList, Priority.ALWAYS);

    VBox docInfoPanel = new VBox(15);
    docInfoPanel.setPadding(new Insets(20));
    docInfoPanel.setVisible(false);
    docInfoPanel.setManaged(false);

    Label docFormTitle = new Label("Add New Doctor");
    docFormTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 20));

    GridPane docGrid = new GridPane();
    docGrid.setHgap(10);
    docGrid.setVgap(10);

    TextField docName = new TextField();
    docName.setPromptText("Name");
    TextField docId = new TextField();
    docId.setPromptText("ID");
    TextField docAge = new TextField();
    docAge.setPromptText("Age");
    ComboBox<String> docSpecialty = new ComboBox<>(
        FXCollections.observableArrayList("General", "Surgeon", "Specialist"));

    ComboBox<String> subSpecialtyCombo = new ComboBox<>();
    subSpecialtyCombo.setPromptText("Sub-Specialty");
    subSpecialtyCombo.setDisable(true);

    docSpecialty.valueProperty().addListener((obs, oldVal, newVal) -> {
      Map<String, Object[]> specialtyMap = Map.of(
          "Surgeon", SurgeonDoctor.Speciality.values(),
          "Specialist", SpecialistDoctor.Speciality.values());

      Object[] values = specialtyMap.get(newVal);

      if (values != null) {
        List<String> names = Arrays.stream(values)
            .map(obj -> ((Enum<?>) obj).name())
            .collect(Collectors.toList());

        subSpecialtyCombo.setItems(FXCollections.observableArrayList(names));
        subSpecialtyCombo.setDisable(false);
        subSpecialtyCombo.setVisible(true);
        subSpecialtyCombo.setManaged(true);
      } else {
        subSpecialtyCombo.setItems(FXCollections.observableArrayList());
        subSpecialtyCombo.setDisable(true);
        subSpecialtyCombo.setVisible(false);
        subSpecialtyCombo.setManaged(false);
      }
    });

    Button saveDocBtn = new Button("Save Doctor");
    Button cancelDocBtn = new Button("Cancel");

    docGrid.addRow(0, new Label("Name:"), docName);
    docGrid.addRow(1, new Label("ID:"), docId);
    docGrid.addRow(2, new Label("Age:"), docAge);
    docGrid.addRow(3, new Label("Specialty:"), new HBox(10, docSpecialty, subSpecialtyCombo));
    docGrid.add(new HBox(30, saveDocBtn, cancelDocBtn), 0, 4);

    docInfoPanel.getChildren().addAll(docFormTitle, docGrid);

    editDocBtn.setOnAction(e -> {
      Doctor newVal = mgmtDocList.getSelectionModel().getSelectedItem();
      if (newVal != null) {
        docInfoPanel.setVisible(true);
        docInfoPanel.setManaged(true);
        docFormTitle.setText("Edit Doctor");
        docName.setText(newVal.getFormattedName());
        docId.setText(String.valueOf(newVal.getId()));
        docAge.setText(String.valueOf(newVal.getAge()));
        docId.setDisable(true);

        if (newVal instanceof SurgeonDoctor) {
          docSpecialty.setValue("Surgeon");
          subSpecialtyCombo.setValue(((SurgeonDoctor) newVal).getSpeciality());
        } else if (newVal instanceof SpecialistDoctor) {
          docSpecialty.setValue("Specialist");
          subSpecialtyCombo.setValue(((SpecialistDoctor) newVal).getSpeciality());
        } else {
          docSpecialty.setValue("General");
          subSpecialtyCombo.setValue(null);
        }

        docSpecialty.setDisable(true);
        subSpecialtyCombo.setDisable(true);
      } else {
        showAlert(Alert.AlertType.WARNING, "Select a Doctor to edit.");
      }
    });

    addDocBtn.setOnAction(e -> {
      mgmtDocList.getSelectionModel().clearSelection();
      docInfoPanel.setVisible(true);
      docInfoPanel.setManaged(true);
      docFormTitle.setText("Add New Doctor");
      docName.clear();
      docId.clear();
      docId.setDisable(false);
      docAge.clear();
      docSpecialty.getSelectionModel().clearSelection();
      docSpecialty.setDisable(false);
      subSpecialtyCombo.getSelectionModel().clearSelection();
      subSpecialtyCombo.setItems(FXCollections.observableArrayList());
      subSpecialtyCombo.setDisable(true);
    });

    cancelDocBtn.setOnAction(e -> {
      docInfoPanel.setVisible(false);
      docInfoPanel.setManaged(false);
    });

    saveDocBtn.setOnAction(e -> {
      if (docName.getText().isEmpty() || docId.getText().isEmpty() ||
          docAge.getText().isEmpty() || docSpecialty.getValue() == null) {
        showAlert(Alert.AlertType.WARNING, "Please fill all fields.");
        return;
      }

      if (("Surgeon".equals(docSpecialty.getValue()) || "Specialist".equals(docSpecialty.getValue()))
          && subSpecialtyCombo.getValue() == null) {
        showAlert(Alert.AlertType.WARNING, "Please select a sub-specialty.");
        return;
      }

      int id, age;
      try {
        id = Integer.parseInt(docId.getText());
        age = Integer.parseInt(docAge.getText());
      } catch (NumberFormatException ex) {
        showAlert(Alert.AlertType.ERROR, "ID and Age must be numbers.");
        return;
      }

      Doctor selected = mgmtDocList.getSelectionModel().getSelectedItem();
      String successMsg;

      if (selected != null) {
        // Update
        selected.setName(docName.getText());
        selected.setAge(age);
        successMsg = "Doctor updated successfully!";
      } else {
        boolean exist = false;
        for (Doctor d : allDoctors) {
          if (d.getId() == id) {
            exist = true;
            break;
          }
        }
        if (exist) {
          showAlert(Alert.AlertType.ERROR, "Doctor with ID " + id + " already exists.");
          return;
        }

        Doctor newDoc;
        if ("Surgeon".equals(docSpecialty.getValue())) {
          newDoc = new SurgeonDoctor(age, id, docName.getText(),
              SurgeonDoctor.Speciality.valueOf(subSpecialtyCombo.getValue()));
        } else if ("Specialist".equals(docSpecialty.getValue())) {
          newDoc = new SpecialistDoctor(age, id, docName.getText(),
              SpecialistDoctor.Speciality.valueOf(subSpecialtyCombo.getValue()));
        } else {
          newDoc = new GeneralDoctor(age, id, docName.getText());
        }

        allDoctors.add(newDoc);
        successMsg = "Doctor added successfully!";
      }

      refreshDoctorList();
      mgmtDocList.refresh();
      showAlert(Alert.AlertType.INFORMATION, successMsg);

      docInfoPanel.setVisible(false);
      docInfoPanel.setManaged(false);
    });

    docSplit.getItems().addAll(docListPanel, docInfoPanel);
    docSplit.setDividerPositions(0.4);
    tab.setContent(docSplit);

  }

  private void setupPatientTab(Tab tab) {
    SplitPane patSplit = new SplitPane();

    VBox patListPanel = new VBox(10);
    patListPanel.setPadding(new Insets(10));
    ListView<Paitent> mgmtPatList = new ListView<>(allPatients);
    mgmtPatList.setCellFactory(param -> new ListCell<Paitent>() {
      @Override
      protected void updateItem(Paitent item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null)
          setText(null);
        else
          setText(item.getName() + " (" + item.getId() + ")");
      }
    });
    Button addPatBtn = new Button("+ Add Patient");
    HBox patToolbar = new HBox(5, addPatBtn);

    patListPanel.getChildren().addAll(new Label("Patients"), patToolbar, mgmtPatList);
    VBox.setVgrow(mgmtPatList, Priority.ALWAYS);

    StackPane patStack = new StackPane();
    patStack.setPadding(new Insets(20));

    VBox patHistoryPanel = new VBox(15);
    patHistoryPanel.setVisible(false);
    Label historyTitle = new Label("Patient Details");
    historyTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
    Label patInfoLbl = new Label();
    ListView<Appointment> historyListView = new ListView<>();
    historyListView.setCellFactory(param -> new ListCell<Appointment>() {
      @Override
      protected void updateItem(Appointment item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null)
          setText(null);
        else {
          DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
          setText(item.getStartTime().format(dtf) + " | " + item.getStatus() + " | " + item.getIllness());
        }
      }
    });

    Button editPatBtn = new Button("✎ Edit Details");
    patHistoryPanel.getChildren().addAll(historyTitle, patInfoLbl, editPatBtn, new Label("Visit History:"),
        historyListView);
    VBox.setVgrow(historyListView, Priority.ALWAYS);

    VBox patFormPanel = new VBox(15);
    patFormPanel.setVisible(false);
    Label patFormTitle = new Label("Add New Patient");
    patFormTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

    GridPane patGrid = new GridPane();
    patGrid.setHgap(10);
    patGrid.setVgap(10);
    TextField patName = new TextField();
    TextField patId = new TextField();
    TextField patAge = new TextField();
    TextField patContact = new TextField();
    Button savePatBtn = new Button("Save Patient");
    Button cancelPatBtn = new Button("Cancel");
    patGrid.addRow(0, new Label("Name:"), patName);
    patGrid.addRow(1, new Label("ID:"), patId);
    patGrid.addRow(2, new Label("Age:"), patAge);
    patGrid.addRow(3, new Label("Contact:"), patContact);
    patGrid.add(new HBox(10, savePatBtn, cancelPatBtn), 1, 4);
    patFormPanel.getChildren().addAll(patFormTitle, patGrid);

    patStack.getChildren().addAll(patHistoryPanel, patFormPanel);

    mgmtPatList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal != null) {
        patHistoryPanel.setVisible(true);
        patFormPanel.setVisible(false);
        patInfoLbl.setText(String.format("Name: %s | ID: %s | Age: %d | Contact: %s",
            newVal.getName(), newVal.getId(), newVal.getAge(), newVal.getPhoneNumber()));
        historyListView.setItems(FXCollections.observableArrayList(newVal.getVisits()));
      } else {
        patHistoryPanel.setVisible(false);
        patFormPanel.setVisible(false);
      }
    });
    editPatBtn.setOnAction(e -> {
      Paitent selected = mgmtPatList.getSelectionModel().getSelectedItem();
      if (selected != null) {
        patHistoryPanel.setVisible(false);
        patFormPanel.setVisible(true);
        patFormTitle.setText("Edit Patient");
        patName.setText(selected.getName());
        patId.setText(selected.getId());
        patId.setDisable(true);
        patAge.setText(String.valueOf(selected.getAge()));
        patContact.setText(selected.getPhoneNumber());
      }
    });

    addPatBtn.setOnAction(e -> {
      mgmtPatList.getSelectionModel().clearSelection();
      patHistoryPanel.setVisible(false);
      patFormPanel.setVisible(true);
      patFormTitle.setText("Add New Patient");
      patName.clear();
      patId.clear();
      patId.setDisable(false);
      patAge.clear();
      patContact.clear();
    });

    cancelPatBtn.setOnAction(e -> {
      if (mgmtPatList.getSelectionModel().getSelectedItem() != null) {
        patFormPanel.setVisible(false);
        patHistoryPanel.setVisible(true);
      } else {
        patFormPanel.setVisible(false);
      }
    });

    savePatBtn.setOnAction(e -> {
      if (patName.getText().isEmpty() || patId.getText().isEmpty() || patAge.getText().isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Please fill all required fields.");
        return;
      }
      try {
        int age = Integer.parseInt(patAge.getText());
        Paitent selected = mgmtPatList.getSelectionModel().getSelectedItem();

        if (selected != null) {
          selected.setName(patName.getText());
          selected.setAge(age);
          selected.setPhoneNumber(patContact.getText());
          mgmtPatList.refresh();
          patInfoLbl.setText(String.format("Name: %s | ID: %s | Age: %d | Contact: %s",
              selected.getName(), selected.getId(), selected.getAge(), selected.getPhoneNumber()));

          patientSelectionBox.setItems(null);
          patientSelectionBox.setItems(allPatients);
          showAlert(Alert.AlertType.INFORMATION, "Patient updated successfully!");
          patFormPanel.setVisible(false);
          patHistoryPanel.setVisible(true);
        } else {
          Paitent newPat = new Paitent(patId.getText(), patName.getText(), age, patContact.getText());
          allPatients.add(newPat);
          showAlert(Alert.AlertType.INFORMATION, "Patient added successfully!");
          mgmtPatList.getSelectionModel().select(newPat);
        }
      } catch (NumberFormatException ex) {
        showAlert(Alert.AlertType.ERROR, "Age must be a number.");
      }
    });

    patSplit.getItems().addAll(patListPanel, patStack);
    patSplit.setDividerPositions(0.35);
    tab.setContent(patSplit);
  }

  private Tab createManagementTab() {
    Tab tab = new Tab("Management");

    VBox layout = new VBox();

    TabPane subTabs = new TabPane();
    subTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

    Tab docTab = new Tab("Doctors");
    setupDocTab(docTab);

    Tab patTab = new Tab("Patients");
    setupPatientTab(patTab);

    subTabs.getTabs().addAll(docTab, patTab);

    layout.getChildren().add(subTabs);
    VBox.setVgrow(subTabs, Priority.ALWAYS);
    tab.setContent(layout);
    return tab;
  }

  private VBox setupDoctorAvailabilityPanel() {
    VBox leftPanel = new VBox(10);
    leftPanel.setPadding(new Insets(10));

    HBox listHeader = new HBox(10);
    listHeader.setAlignment(Pos.CENTER_LEFT);
    Label listTitle = new Label("Doctor Availability");
    listTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

    Button showAllBtn = new Button("Show All");
    showAllBtn.setOnAction(e -> doctorListView.getSelectionModel().clearSelection());

    listHeader.getChildren().addAll(listTitle, showAllBtn);

    doctorListView = new ListView<>();
    doctorListView.setCellFactory(param -> new DoctorStatusCell());
    refreshDoctorList();
    VBox.setVgrow(doctorListView, Priority.ALWAYS);

    leftPanel.getChildren().addAll(listHeader, doctorListView);
    return leftPanel;
  }

  private Tab createAppointmentsTab() {
    Tab tab = new Tab("Appointments");
    SplitPane splitPane = new SplitPane();

    VBox leftPanel = setupDoctorAvailabilityPanel();

    StackPane appointmentContentStack = new StackPane();
    appointmentContentStack.setPadding(new Insets(20));

    VBox appointmentListContainer = new VBox(10);
    Label apptListTitle = new Label("Appointments");
    VBox addAppointmentFormContainer = new VBox(15);

    setupAppointmentListPanel(appointmentListContainer, apptListTitle, addAppointmentFormContainer);
    setupAddAppointmentForm(addAppointmentFormContainer, appointmentListContainer);

    appointmentContentStack.getChildren().addAll(appointmentListContainer, addAppointmentFormContainer);

    doctorListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal != null) {
        apptListTitle.setText("Appointments for " + newVal.getFormattedName());
        ObservableList<Appointment> docAppts = FXCollections.observableArrayList(newVal.getAppointments());
        docAppts.sort(Comparator.comparing(Appointment::getStartTime));
        apptListView.setItems(docAppts);
      } else {
        apptListTitle.setText("All Appointments");
        ObservableList<Appointment> allAppts = FXCollections.observableArrayList();
        for (Doctor d : allDoctors) {
          allAppts.addAll(d.getAppointments());
        }
        allAppts.sort(Comparator.comparing(Appointment::getStartTime));
        apptListView.setItems(allAppts);
      }
    });

    splitPane.getItems().addAll(leftPanel, appointmentContentStack);
    splitPane.setDividerPositions(0.4);
    tab.setContent(splitPane);

    refreshAppointmentList();

    return tab;
  }

  private void setupAppointmentListPanel(VBox appointmentListContainer, Label apptListTitle, VBox addApptFormPanel) {
    apptListTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

    dateFilter = new ComboBox<>(
        FXCollections.observableArrayList("Today", "Specific Day", "This Month", "All Time"));
    dateFilter.setValue("Today");

    specificDatePicker = new DatePicker(LocalDate.now());
    specificDatePicker.setVisible(false);
    specificDatePicker.setManaged(false);

    dateFilter.setOnAction(e -> {
      boolean eq = "Specific Day".equals(dateFilter.getValue());
      specificDatePicker.setVisible(eq);
      specificDatePicker.setManaged(eq);
      refreshAppointmentList();
    });
    specificDatePicker.setOnAction(e -> refreshAppointmentList());

    apptListView = new ListView<>();
    apptListView.setCellFactory(param -> new ListCell<Appointment>() {
      @Override
      protected void updateItem(Appointment item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
          DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
          String filterValue = dateFilter.getValue();
          String timeDisplay;
          if ("Today".equals(filterValue) || "Specific Day".equals(filterValue)) {
            timeDisplay = item.getStartTime().format(timeFormatter) + " to " + item.getEndTime().format(timeFormatter);
          } else {
            timeDisplay = item.getStartTime().format(dateFormatter) + " to " + item.getEndTime().format(timeFormatter);
          }
          String statusStr = "[" + item.getStatus().name().toUpperCase() + "]";
          setText(
              timeDisplay + " | " + item.getPaitent().getName() + " | " + statusStr + " "
                  + (item.isEmergency() ? "[EMERGENCY] " : "") + item.getIllness());
        }
      }
    });

    openAddApptBtn = new Button("+ Add Appointment");
    Button cancelApptBtn = new Button("Cancel Selected");

    cancelApptBtn.setOnAction(e -> {
      Appointment selected = apptListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        if (selected.getStatus() != Status.canceled || selected.getStatus() != Status.onGoing
            || selected.getStatus() != Status.completed) {
          selected.cancel();
          apptListView.refresh();
          refreshDoctorList();
          showAlert(Alert.AlertType.INFORMATION, "Appointment canceled.");
        } else {
          showAlert(Alert.AlertType.INFORMATION, "Can't Cancel this Appointment");
        }
      } else {
        showAlert(Alert.AlertType.WARNING, "Select an appointment to cancel.");
      }
    });

    HBox filterToolbar = new HBox(10, apptListTitle, dateFilter, specificDatePicker);
    filterToolbar.setAlignment(Pos.CENTER_LEFT);

    HBox apptToolbar = new HBox(15, openAddApptBtn, cancelApptBtn);
    apptToolbar.setAlignment(Pos.CENTER_LEFT);

    appointmentListContainer.getChildren().addAll(filterToolbar, apptToolbar, apptListView);
    VBox.setVgrow(apptListView, Priority.ALWAYS);
  }

  private void setupAddAppointmentForm(VBox addAppointmentForm, VBox appointmentListContainer) {
    Label formTitle = new Label("Schedule Appointment");
    formTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

    GridPane form = new GridPane();
    form.setHgap(10);
    form.setVgap(15);

    patientSelectionBox = new ComboBox<>(allPatients);
    patientSelectionBox.setCellFactory(param -> new ListCell<Paitent>() {
      @Override
      protected void updateItem(Paitent item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null)
          setText(null);
        else
          setText(item.getName() + " (" + item.getId() + ")");
      }
    });
    patientSelectionBox.setButtonCell(new ListCell<Paitent>() {
      @Override
      protected void updateItem(Paitent item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null)
          setText(null);
        else
          setText(item.getName());
      }
    });

    apptStartDate = new DatePicker(LocalDate.now());
    apptStartTime = new TextField("08:00");
    apptStartTime.setPromptText("HH:mm");
    apptEndDate = new DatePicker(LocalDate.now());
    apptEndTime = new TextField("09:00");
    apptEndTime.setPromptText("HH:mm");

    apptIllness = new TextField();
    apptEmergencyCheck = new CheckBox("Is Emergency?");

    apptTypeCombo = new ComboBox<>(FXCollections.observableArrayList("Checkup", "Operation"));
    apptSurgeryTypeCombo = new ComboBox<>(FXCollections.observableArrayList(Operationtype.values()));
    apptSurgeryTypeLabel = new Label("Surgery Type:");
    apptSurgeryTypeCombo.setVisible(false);
    apptSurgeryTypeCombo.setManaged(false);
    apptSurgeryTypeLabel.setVisible(false);
    apptSurgeryTypeLabel.setManaged(false);

    doctorSelectionBox = new ComboBox<>(allDoctors);

    doctorSelectionBox.setCellFactory(param -> new ListCell<Doctor>() {
      @Override
      protected void updateItem(Doctor item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null)
          setText(null);
        else
          setText(item.getFormattedName() + " (" + item.getClass().getSimpleName().replace("Doctor", "") + ")");
      }
    });
    doctorSelectionBox.setButtonCell(new ListCell<Doctor>() {
      @Override
      protected void updateItem(Doctor item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null)
          setText(null);
        else
          setText(item.getFormattedName() + " (" + item.getClass().getSimpleName().replace("Doctor", "") + ")");
      }
    });

    apptTypeCombo.setOnAction(e -> {
      boolean isOperation = "Operation".equals(apptTypeCombo.getValue());
      apptSurgeryTypeCombo.setVisible(isOperation);
      apptSurgeryTypeCombo.setManaged(isOperation);
      apptSurgeryTypeLabel.setVisible(isOperation);
      apptSurgeryTypeLabel.setManaged(isOperation);

      if (isOperation) {
        doctorSelectionBox.setItems(FXCollections.observableArrayList(
            allDoctors.stream().filter(d -> d instanceof SurgeonDoctor).collect(Collectors.toList())));
      } else {
        doctorSelectionBox.setItems(allDoctors);
      }
      updateSurgeryTypeCombo(doctorSelectionBox.getValue());
    });

    doctorSelectionBox.valueProperty().addListener((obs, oldDoc, newDoc) -> {
      updateSurgeryTypeCombo(newDoc);
    });

    Button bookBtn = new Button("Book Appointment");
    bookBtn.setStyle("-fx-base: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
    Button cancelBookBtn = new Button("Cancel");

    form.addRow(0, new Label("Patient:"), patientSelectionBox);
    form.addRow(1, new Label("Start Time:"), new HBox(5, apptStartDate, apptStartTime));
    form.addRow(2, new Label("End Time:"), new HBox(5, apptEndDate, apptEndTime));
    form.addRow(3, new Label("Illness/Reason:"), apptIllness);
    form.addRow(4, new Label("Appt Type:"), apptTypeCombo, apptEmergencyCheck);
    form.addRow(5, new Label("Doctor:"), doctorSelectionBox);
    form.addRow(6, apptSurgeryTypeLabel, apptSurgeryTypeCombo);
    form.add(new HBox(10, bookBtn, cancelBookBtn), 1, 7);

    addAppointmentForm.getChildren().addAll(formTitle, form);
    addAppointmentForm.setVisible(false);

    openAddApptBtn.setOnAction(e -> {
      formOpenedTime = LocalDateTime.now();
      apptStartDate.setValue(formOpenedTime.toLocalDate());
      apptStartTime.setText(String.format("%02d:%02d", formOpenedTime.getHour(), formOpenedTime.getMinute()));
      apptEndDate.setValue(formOpenedTime.toLocalDate());
      apptEndTime.setText(
          String.format("%02d:%02d", formOpenedTime.plusHours(1).getHour(), formOpenedTime.plusHours(1).getMinute()));

      Doctor selected = doctorListView.getSelectionModel().getSelectedItem();
      doctorSelectionBox.setValue(selected);

      appointmentListContainer.setVisible(false);
      addAppointmentForm.setVisible(true);
    });

    cancelBookBtn.setOnAction(e -> {
      addAppointmentForm.setVisible(false);
      appointmentListContainer.setVisible(true);
    });

    bookBtn.setOnAction(e -> {
      try {
        LocalDateTime start = LocalDateTime.of(apptStartDate.getValue(), LocalTime.parse(apptStartTime.getText()));
        LocalDateTime end = LocalDateTime.of(apptEndDate.getValue(), LocalTime.parse(apptEndTime.getText()));

        if (start.isBefore(formOpenedTime.minusMinutes(1))) {
          showAlert(Alert.AlertType.ERROR,
              "Start time cannot be set before the time you started booking (" + formOpenedTime.toLocalTime() + ")!");
          return;
        }

        if (!start.isBefore(end)) {
          showAlert(Alert.AlertType.ERROR, "End time must be after Start time!");
          return;
        }

        Doctor selectedDoctor = doctorSelectionBox.getValue();
        Paitent selectedPatient = patientSelectionBox.getValue();
        String apptType = apptTypeCombo.getValue();

        if (selectedDoctor == null || selectedPatient == null || apptType == null) {
          showAlert(Alert.AlertType.WARNING, "Please select Appt Type, Patient, and Doctor.");
          return;
        }

        Appointment newAppt = null;
        if ("Operation".equals(apptType)) {
          if (!(selectedDoctor instanceof SurgeonDoctor)) {
            showAlert(Alert.AlertType.ERROR, "Selected doctor is not a surgeon.");
            return;
          }
          Operationtype opType = apptSurgeryTypeCombo.getValue();
          if (opType == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a Surgery Type.");
            return;
          }
          newAppt = new Operation(start.toString(), end.toString(), Status.scheduled, true,
              apptEmergencyCheck.isSelected(), opType, selectedPatient, apptIllness.getText());
        } else if ("Checkup".equals(apptType)) {
          newAppt = new CheckUp(start.toString(), end.toString(), apptEmergencyCheck.isSelected(),
              selectedPatient, apptIllness.getText());
        }

        if (newAppt == null) {
          showAlert(Alert.AlertType.ERROR, "Unsupported appointment type.");
          return;
        }

        if (selectedDoctor.addAppointment(newAppt)) {
          selectedPatient.addVisit(newAppt);
          refreshDoctorList();
          refreshAppointmentList();
          showAlert(Alert.AlertType.INFORMATION, "Appointment successfully booked!");
          addAppointmentForm.setVisible(false);
          appointmentListContainer.setVisible(true);
        } else {
          showAlert(Alert.AlertType.ERROR, "Doctor has a scheduling conflict.");
        }
      } catch (DateTimeParseException ex) {
        showAlert(Alert.AlertType.ERROR, "Invalid time format. Please use HH:mm (e.g. 08:30).");
      }
    });
  }

  private void refreshAppointmentList() {
    if (apptListView == null || dateFilter == null)
      return;

    Doctor selectedDoc = doctorListView.getSelectionModel().getSelectedItem();
    ObservableList<Appointment> apptsToFilter = FXCollections.observableArrayList();

    if (selectedDoc != null) {
      apptsToFilter.addAll(selectedDoc.getAppointments());
    } else {
      for (Doctor d : allDoctors) {
        apptsToFilter.addAll(d.getAppointments());
      }
    }

    String filterType = dateFilter.getValue();
    LocalDate now = LocalDate.now();

    ObservableList<Appointment> filteredAppts = FXCollections.observableArrayList();

    for (Appointment a : apptsToFilter) {
      LocalDate apptDate = a.getStartTime().toLocalDate();
      boolean matches = false;

      if ("All Time".equals(filterType)) {
        matches = true;
      } else if ("Today".equals(filterType)) {
        matches = apptDate.isEqual(now);
      } else if ("Specific Day".equals(filterType)) {
        LocalDate target = specificDatePicker.getValue();
        matches = (target != null && apptDate.isEqual(target));
      } else if ("This Month".equals(filterType)) {
        matches = (apptDate.getMonth() == now.getMonth() && apptDate.getYear() == now.getYear());
      }

      if (matches) {
        filteredAppts.add(a);
      }
    }

    filteredAppts.sort(Comparator.comparing(Appointment::getStartTime));
    apptListView.setItems(filteredAppts);
  }

  private void refreshDoctorList() {
    Comparator<Doctor> sortLogic = (d1, d2) -> {
      boolean em1 = isHandlingEmergency(d1);
      boolean em2 = isHandlingEmergency(d2);
      if (em1 && !em2)
        return 1;
      if (!em1 && em2)
        return -1;
      return Integer.compare(d1.getAppointments().size(), d2.getAppointments().size());
    };

    ObservableList<Doctor> sortedList = FXCollections.observableArrayList(allDoctors);
    sortedList.sort(sortLogic);
    if (doctorListView != null) {
      doctorListView.setItems(sortedList);
    }
  }

  private boolean isHandlingEmergency(Doctor doc) {
    LocalDateTime now = LocalDateTime.now();
    for (Appointment a : doc.getAppointments()) {
      if (a.isEmergency() && a.getStartTime().isBefore(now) && a.getEndTime().isAfter(now)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasOngoingAppointment(Doctor doc) {
    LocalDateTime now = LocalDateTime.now();
    for (Appointment a : doc.getAppointments()) {
      if (!a.isEmergency() && a.getStartTime().isBefore(now) && a.getEndTime().isAfter(now)) {
        return true;
      }
    }
    return false;
  }

  private class DoctorStatusCell extends ListCell<Doctor> {
    @Override
    protected void updateItem(Doctor doctor, boolean empty) {
      super.updateItem(doctor, empty);
      if (empty || doctor == null) {
        setText(null);
        setGraphic(null);
      } else {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);

        String specialty = doctor.getClass().getSimpleName().replace("Doctor", "");
        Label nameLbl = new Label(
            doctor.getFormattedName() + " (" + specialty + ") - " + doctor.getAppointments().size() + " appts");
        box.getChildren().add(nameLbl);

        if (isHandlingEmergency(doctor)) {
          Label tag = new Label("EMERGENCY");
          tag.setTextFill(Color.WHITE);
          tag.setStyle("-fx-background-color: red; -fx-padding: 2 5; -fx-background-radius: 3;");
          box.getChildren().add(tag);
        } else if (hasOngoingAppointment(doctor)) {
          Label tag = new Label("ONGOING");
          tag.setTextFill(Color.WHITE);
          tag.setStyle("-fx-background-color: orange; -fx-padding: 2 5; -fx-background-radius: 3;");
          box.getChildren().add(tag);
        }
        setGraphic(box);
      }
    }
  }

  private void updateSurgeryTypeCombo(Doctor selectedDoctor) {
    if ("Operation".equals(apptTypeCombo.getValue())) {
      if (selectedDoctor instanceof SurgeonDoctor) {
        SurgeonDoctor surgeon = (SurgeonDoctor) selectedDoctor;
        List<Operationtype> validOps = Arrays.stream(Operationtype.values())
            .filter(op -> op.getRequiredSpeciality().name().equals(surgeon.getSpeciality()))
            .collect(Collectors.toList());
        apptSurgeryTypeCombo.setItems(FXCollections.observableArrayList(validOps));
        if (!validOps.isEmpty()) {
          apptSurgeryTypeCombo.setValue(validOps.get(0));
        } else {
          apptSurgeryTypeCombo.setValue(null);
        }
      } else {
        apptSurgeryTypeCombo.setItems(FXCollections.observableArrayList());
        apptSurgeryTypeCombo.setValue(null);
      }
    } else {
      apptSurgeryTypeCombo.setItems(FXCollections.observableArrayList(Operationtype.values()));
    }
  }

  private void showAlert(Alert.AlertType type, String msg) {
    Alert alert = new Alert(type, msg);
    alert.showAndWait();
  }

}
