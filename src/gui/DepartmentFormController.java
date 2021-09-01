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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

/**
 * FXML Controller class
 *
 * @author Invasor_zim
 */
public class DepartmentFormController implements Initializable {

    private Department entity;
    private DepartmentService departmentService;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
    
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @FXML
    private TextField txtId;

    public void setEntity(Department entity) {
        this.entity = entity;
    }
    
    public void subscribeDataChangerListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }
    
    @FXML
    private TextField txtName;
    @FXML
    private Label labelErroName;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

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
            departmentService.saveOrUpdate(entity);
            notifyDataChengeListener();
            Utils.currentSatge(event).close();
            
        }catch(DbException e){
            AlertJeff.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
        catch(ValidationException e){
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
        Constraints.setTextFieldMaxLength(txtName, 30);
    }

    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }

        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
    }

    private Department getFormData() {
        Department obj = new Department();
        ValidationException exception = new ValidationException("Validation Exception");
        obj.setId(Utils.tryParseToInt(txtId.getText()));
        if (txtName.getText() == null || txtName.getText().trim().equals("")) {
            exception.addError("name", "field can't be empty" );
        }
        obj.setName(txtName.getText());
        if (exception.getErros().size() > 0) {
            throw exception;
        }
        
        
        return obj;
    }

    private void notifyDataChengeListener() {
        for(DataChangeListener list: dataChangeListeners){
            list.onDataChanged();
        }
    }
    
    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();
        labelErroName.setText(errors.get("name"));
    }

}
