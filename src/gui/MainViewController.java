/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import gui.util.AlertJeff;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import model.services.DepartmentService;
import model.services.SellerService;
import projetocrudjeff.ProjetoCRUDJeff;
/**
 * FXML Controller class
 *
 * @author Invasor_zim
 */
public class MainViewController implements Initializable {
    @FXML
    private MenuItem menuItemSeller;
    @FXML
    private MenuItem menuItemDepartment;
    @FXML
    private MenuItem menuItemAbout;
    
    @FXML
    public void onMenuItemSellerAction(){
        loadView("/gui/SellerList.fxml", (SellerListController controller) ->{
            controller.setService(new SellerService());
            controller.updateTableView();
        });  
    }
    
    @FXML
    public void onMenuItemDepartmentAction(){
        loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) ->{
            controller.setService(new DepartmentService());
            controller.updateTableView();
        });  
    }
    
    @FXML
    public void onMenuItemAboutAction(){
        loadView("/gui/About.fxml", x -> {} );
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
    private synchronized <T> void loadView(String absolutName, Consumer<T> initializingAction){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
            VBox vbox = loader.load();
            Scene mainScene = ProjetoCRUDJeff.getMainScene();
            VBox mainVbox = (VBox)((ScrollPane)mainScene.getRoot()).getContent();
            Node mainMenu = mainVbox.getChildren().get(0);
            mainVbox.getChildren().clear();
            mainVbox.getChildren().add(mainMenu);
            mainVbox.getChildren().addAll(vbox.getChildren());
            T controller = loader.getController();
            initializingAction.accept(controller);
            
        } catch (IOException ex) {
            AlertJeff.showAlert("Erro",null, ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    
}
