/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.AlertJeff;
import gui.util.Constraints;
import gui.util.Utils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

/**
 * FXML Controller class
 *
 * @author Invasor_zim
 */
public class SellerFormController implements Initializable {

    private Seller entity;
    private SellerService sellerService;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
    private DepartmentService departmentService;

    public void setServices(SellerService sellerService, DepartmentService departmentService) {
        this.sellerService = sellerService;
        this.departmentService = departmentService;
    }

    @FXML
    private TextField txtId;

    public void setEntity(Seller entity) {
        this.entity = entity;
    }

    public void subscribeDataChangerListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private DatePicker txtBirthDate;
    @FXML
    private TextField txtBaseSalary;
    @FXML
    private ComboBox<Department> comboBoxDepartment;
    @FXML
    private Label labelErroName;
    @FXML
    private Label labelErroEmail;
    @FXML
    private Label labelErroBirthDate;
    @FXML
    private Label labelErroBaseSalary;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

    private ObservableList<Department> obsList;

    @FXML
    public void onBtSaveAction(ActionEvent event) {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        if (departmentService == null) {
            throw new IllegalStateException("Service was null");
        }
        try {
            entity = getFormData();
            sellerService.saveOrUpdate(entity);
            notifyDataChengeListener();
            Utils.currentSatge(event).close();

        } catch (DbException e) {
            AlertJeff.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        } catch (ValidationException e) {
            setErrorMessages(e.getErros());
        }

    }

    @FXML
    public void onBtCancelAction(ActionEvent event) {
        Utils.currentSatge(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 70);
        Constraints.setTextFieldDouble(txtBaseSalary);
        Constraints.setTextFieldMaxLength(txtEmail, 60);
        Utils.formatDatePicker(txtBirthDate, "dd/MM/yyyy");
        initializeComboBoxDepartment();

    }

    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }

        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
        txtEmail.setText(entity.getEmail());
        Locale.setDefault(Locale.US);
        txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
        //txtBirthDate.setValue(LocalDateTime.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
        if (entity.getDepartment() == null) {
            comboBoxDepartment.getSelectionModel().selectFirst();
        }else{
            comboBoxDepartment.setValue(entity.getDepartment());
        }
        
    }

    public void loadAssociatedObjects() {
        List<Department> list = departmentService.findAll();
        obsList = FXCollections.observableArrayList(list);
        comboBoxDepartment.setItems(obsList);

    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };

        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }

    private Seller getFormData() {
        Seller obj = new Seller();
        ValidationException exception = new ValidationException("Validation Exception");
        obj.setId(Utils.tryParseToInt(txtId.getText()));
        if (txtName.getText() == null || txtName.getText().trim().equals("")) {
            exception.addError("name", "field can't be empty");
        }
        obj.setName(txtName.getText());
        if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
            exception.addError("Email", "field can't be empty");
        }
        obj.setName(txtEmail.getText());
        
//        Instant instant = Instant.from(txtBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
//        obj.setBirthDate(Date.from(instant));
         if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
            exception.addError("BaseSalary", "field can't be empty");
        }
        obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
        obj.setDepartment(comboBoxDepartment.getValue());
        
        if (exception.getErros().size() > 0) {
            throw exception;
        }

        return obj;
    }

    private void notifyDataChengeListener() {
        for (DataChangeListener list : dataChangeListeners) {
            list.onDataChanged();
        }
    }

    private void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();
        if (fields.contains("name")) {
            labelErroName.setText(errors.get("name"));
        }
        labelErroEmail.setText(errors.get("email"));
        labelErroBaseSalary.setText(errors.get("baseSalary"));
    }

}
