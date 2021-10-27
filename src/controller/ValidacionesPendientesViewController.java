/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.ConceptosPendientes;
import classes.PuestosPendientes;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Jorge
 */
public class ValidacionesPendientesViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML private TableView<PuestosPendientes> tblPuestos;
    @FXML private TableView<ConceptosPendientes> tblConceptos;
    @FXML private TextField txtBusquedaConceptos;
    @FXML private TextField txtBusquedaPuestos;


    private final ObservableList<ConceptosPendientes> dataConceptos=FXCollections.observableArrayList();
    private final ObservableList<PuestosPendientes> dataPuestos=FXCollections.observableArrayList();
    


    
        @FXML private void btnAdd (ActionEvent event) throws IOException{
                
                //Obtenemos el valor del campo "id" para pasarlo a la ventana de edición como parametro.
                ConceptosPendientes p = tblConceptos.getSelectionModel().getSelectedItem();
                ConceptosViewController.idEdicion=p.getConcepto();
                ConceptosViewController.edicion=false;

                //Cambiamos la escena
                root = FXMLLoader.load(getClass().getResource("/view/ConceptosView.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                //Creamos un rectángulo del tamaño de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                stage.show();
    }
            @FXML private void btnRegresar (ActionEvent event) throws IOException{

        //Cambiamos la escena
        root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        //Creamos un rectángulo del tamaño de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();
    }
    public void load(){
        //Abrimos conexión
        classes.MySQL mysql= new classes.MySQL();
        mysql.conectar();
        //Cargamos pendientes de conceptos
        ResultSet rs = mysql.select("SELECT * FROM satin.conceptos_pendientes");
        try {
            // Cargamos las columnas de manera dinámica. Lanza advertencia por no checar tipos de variables pero para nuestro uso no nos afecta.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new PropertyValueFactory<ConceptosPendientes,String>(rs.getMetaData().getColumnName(i+1)));
                tblConceptos.getColumns().addAll(col); 
            }
            //Cargamos los registros a una lista. Se rompe con datos nulos, checar.
            while(rs.next()){
                //Iterate Row
                ConceptosPendientes row = new ConceptosPendientes(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6));
                dataConceptos.add(row);
            }
            //Cargamos los resultados a la tabla
            tblConceptos.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); 

            tblConceptos.setItems(dataConceptos);
            
        } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexión.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
        
        
        //Carga de puestos pendientes
        rs = mysql.select("SELECT * FROM satin.puestos_pendientes");
        try {
            // Cargamos las columnas de manera dinámica. Lanza advertencia por no checar tipos de variables pero para nuestro uso no nos afecta.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                System.out.println(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new PropertyValueFactory<PuestosPendientes,String>(rs.getMetaData().getColumnName(i+1)));
                tblPuestos.getColumns().addAll(col); 
                System.out.println("Column ["+i+"] ");
            }
            //Cargamos los registros a una lista. Se rompe con datos nulos, checar.
            while(rs.next()){
                //Iterate Row
                PuestosPendientes row = new PuestosPendientes(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6));
                dataPuestos.add(row);
            }
            //Cargamos los resultados a la tabla
            tblPuestos.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); 

            tblPuestos.setItems(dataPuestos);
            
        } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexión.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        
        mysql.desconectar();
        
        
        //Busqueda Pestaña Conceptos
        FilteredList<ConceptosPendientes> fiteredDataConceptos = new FilteredList<>(dataConceptos, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		txtBusquedaConceptos.textProperty().addListener((observable, oldValue, newValue) -> {
			fiteredDataConceptos.setPredicate(pendiente -> {
				// If filter text is empty, display all persons.
								
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (pendiente.getId().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
					return true; // Un if por cada campo con su getter para compararlo
				} else if (pendiente.getProducto().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (pendiente.getRfc().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (pendiente.getMovimiento().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (pendiente.getFecha().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
				else if (pendiente.getConcepto().toLowerCase().indexOf(lowerCaseFilter) != -1)
				     return true;
				     else  
				    	 return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<ConceptosPendientes> sortedDataConceptos = new SortedList<>(fiteredDataConceptos);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedDataConceptos.comparatorProperty().bind(tblConceptos.comparatorProperty());
		
		// 5. Add sorted (and filtered) dataConceptos to the table.
                tblConceptos.setItems(sortedDataConceptos);
        
                
        
        //Busqueda Pestaña Puestos
        FilteredList<PuestosPendientes> fiteredDataPuestos = new FilteredList<>(dataPuestos, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		txtBusquedaPuestos.textProperty().addListener((observable, oldValue, newValue) -> {
			fiteredDataPuestos.setPredicate(pendiente -> {
				// If filter text is empty, display all persons.
								
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (pendiente.getId().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
					return true; // Un if por cada campo con su getter para compararlo
				} else if (pendiente.getProducto().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (pendiente.getRfc().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (pendiente.getMovimiento().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (pendiente.getFecha().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
				else if (pendiente.getPuesto().toLowerCase().indexOf(lowerCaseFilter) != -1)
				     return true;
				     else  
				    	 return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<PuestosPendientes> sortedDataPuestos = new SortedList<>(fiteredDataPuestos);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedDataPuestos.comparatorProperty().bind(tblPuestos.comparatorProperty());
		
                tblPuestos.setItems(sortedDataPuestos);
                
        
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load();
    }    
    
}
