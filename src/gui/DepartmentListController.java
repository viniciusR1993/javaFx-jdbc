package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;
	@FXML
	private Button btNew;

	private ObservableList<Department> obsList; // Os departamento s�o carregados nessa ObservableList

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	// Em vez de dar o new DepartmentService na cria��o da variavel fazemos esse
	// metodo par an�o fazer um forte acoplamento
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id")); // Padr�o para iniciar o comportamento das
																				// colunas
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name")); // Padr�o para iniciar o comportamento
																					// das colunas

		Stage stage = (Stage) Main.getMainScene().getWindow(); // pega a referencia para janela no Scene e faz o casting
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); // Isso faz com que a tableView acompanhe
																				// a altura da janela
	}

	// Ele vai acessar o servi�o carregar os departamento, inserir na ObservableList
	// e carregar na tabela de Departmet
	public void updateTabelView() {
		if (service == null) { // Esse teste � pra evitar que o programador n�o esqueceu de injetar a
								// dependencia
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
		initEditButtons(); // Esse acrescenta um bot�o Edit em cada lista da tabela
		initRemoveButtons();
	}

	public void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService()); // injeta a dependencia DepartmentService
			controller.subscribeDataChangeListener(this); // Se escreve para escrever o evento
			controller.updateFormData(); // Carrega os dados do objeto no formulario

			Stage dialogStage = new Stage(); // Novo palco para ter uma janela na frente da outra
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false); // Nesse caso informamos que a janela n�o pode ser redimensionada
			dialogStage.initOwner(parentStage); // Informa quem � o pai dessa stage
			dialogStage.initModality(Modality.WINDOW_MODAL); // Nesse caso informamos que ela � modal. Enquanto n�o
																// fechar n�o segue com a sessao anterior
			dialogStage.showAndWait(); // Executa a scena
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTabelView();
	}

	// Esse codigo foi retirado na apostila da Udemy, serve para inserir o bot�o de edi��o nas colunas
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
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	// Esse codigo foi retirado na apostila da Udemy, serve para inserir o bot�o de edi��o nas colunas
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
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Area you shure to delete?");
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTabelView();
			}catch(DbException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	}

}
