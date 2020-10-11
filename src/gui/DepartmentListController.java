package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{
	
	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;	//Os departamento são carregados nessa ObservableList
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	//Em vez de dar o new DepartmentService na criação da variavel fazemos esse metodo par anão fazer um forte acoplamento
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));	//Padrão para iniciar o comportamento das colunas
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));	//Padrão para iniciar o comportamento das colunas
		
		Stage stage = (Stage)Main.getMainScene().getWindow();	//pega a referencia para janela no Scene e faz o casting
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); //Isso faz com que a tableView acompanhe a altura da janela
	}
	
	//Ele vai acessar o serviço carregar os departamento, inserir na ObservableList e carregar na tabela de Departmet
	public void updateTabelView() {
		if(service == null){	//Esse teste é pra evitar que o programador não esqueceu de injetar a dependencia
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}

}
