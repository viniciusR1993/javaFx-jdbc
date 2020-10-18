package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbount;
	
	@FXML
	public void onMenuItemSellerAction() {
		//Colocamos a fun��o lambda para setar as tabelas (Dessa forma n�o precisamos ter dois atributos para loadView)
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTabelView();
		});
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		//Colocamos a fun��o lambda para setar as tabelas (Dessa forma n�o precisamos ter dois atributos para loadView)
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTabelView();
		});
	}
	
	@FXML
	public void onMenuItemAbountAction() {
		loadView("/gui/About.fxml", x -> {});	//Chama o metodo que carrega as paginas 
	}
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	//Esse metodo carrega as paginas na sua scene
	//O synchronized faz com que � metodo n�o seja interrompido
	//Colocamos uma fun��o parametrizado com um tipo qualquer para implementar a atualiza��odas tabelas
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVbox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox)(((ScrollPane)mainScene.getRoot()).getContent());	//Pega o primeiro elemento da View, faz o casting para ScrollPane e faz referencia o que tiver dentro do ScrollPane (VBox), faz o casting para vbox
			Node mainMenu = mainVBox.getChildren().get(0);	//pega o primeiro filho do VBox (que � o menu)
			mainVBox.getChildren().clear(); //Limpa todos os filhos do VBox
			mainVBox.getChildren().add(mainMenu);//adiciona o menu
			mainVBox.getChildren().addAll(newVbox.getChildren());//adiciona os filhos de newVbox
			
			T controller = loader.getController();	//retorna o controlador do tipo que for inputado na chamada da fun��o
			initializingAction.accept(controller);  //Executa a fun��o que foi inputada na chamada da fun��o
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
