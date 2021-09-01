/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.listeners.DataChangeListener;
import gui.util.AlertJeff;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;
import gui.util.Utils;
import java.util.Optional;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;

/**
 * FXML Controller class
 *
 * @author Invasor_zim
 */
public class DepartmentListController implements Initializable, DataChangeListener {

    private DepartmentService service;

    public void setService(DepartmentService service) {
        this.service = service;
    }

    @FXML
    private TableView<Department> tableViewDepartment;
    @FXML
    private TableColumn<Department, Integer> tableColumId;
    @FXML
    private TableColumn<Department, String> tableColumName;
    @FXML
    TableColumn<Department, Department> tableColumnEDIT;
    @FXML
    TableColumn<Department, Department> tableColumnREMOVE;
    @FXML
    private Button btNew;

    private ObservableList<Department> obsList;

    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = Utils.currentSatge(event);
        Department obj = new Department();
        createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNode();
    }

    private void initializeNode() {
        tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        Stage stage = (Stage) projetocrudjeff.ProjetoCRUDJeff.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }

        List<Department> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewDepartment.setItems(obsList);
        initEditButtons();
        initRemoveButtons();

    }

    private void createDialogForm(Department obj, String absolutName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
            Pane pane = loader.load();
            DepartmentFormController controller = loader.getController();
            controller.setEntity(obj);
            controller.setDepartmentService(new DepartmentService());
            controller.subscribeDataChangerListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Department data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            AlertJeff.showAlert("IOException", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }

    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);

                if (obj == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentSatge(event)));
            }
        });
        
    }

    

    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("remove");

            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);

                if (obj == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }

        });

    }

    private void removeEntity(Department obj) {
        Optional<ButtonType> result = AlertJeff.showConfirmation("Confirmation", "Are you sure to delete?");
        if (result.get() == ButtonType.OK) {
            if (service == null) {
              throw  new IllegalStateException("Service was null");
            }
            try{
                service.remove(obj);
                updateTableView();
            }catch(IllegalStateException e){
                AlertJeff.showAlert("Error", null, e.getMessage(), Alert.AlertType.ERROR);
            }
            
        }
    }


}
