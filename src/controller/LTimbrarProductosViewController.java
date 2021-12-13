/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.ProductoTimbrable;
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
public class LTimbrarProductosViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML private TableView<ProductoTimbrable> tblTimbrables;
    @FXML private TableView<ProductoTimbrable> tblTimbrados;
    @FXML private TextField txtBusquedaTimbrados;
    @FXML private TextField txtBusquedaTimbrables;


    ObservableList<ProductoTimbrable> dataTimbrables=FXCollections.observableArrayList();
    ObservableList<ProductoTimbrable> dataTimbrados=FXCollections.observableArrayList();
    

        
                @FXML private void btnCancelarTimbrados (ActionEvent event) throws IOException{
                
                //Obtenemos el valor del campo "id" para pasarlo a la ventana de edición como parametro.
                ProductoTimbrable p = tblTimbrables.getSelectionModel().getSelectedItem();
                String delete=p.getProducto();
                classes.MySQL mysql= new classes.MySQL();
                mysql.conectar();
                String sql="CALL deleteProducto('"+delete+"');";
                int input = JOptionPane.showConfirmDialog(null, "¿Seguro que deseas eliminar el producto "+delete+" y todos los registros relacionados a éste?", "Confirmar acción",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
		if(input==0){
                    mysql.stmt(sql);
                    load(); 
                }
    }


        
            
        @FXML private void btnTimbrarProducto (ActionEvent event) throws IOException{
                //Obtenemos el valor del campo "id" para pasarlo a la ventana de edición como parametro.
                ProductoTimbrable p = tblTimbrables.getSelectionModel().getSelectedItem();
                DetallesTimbradoViewController.idProducto=p.getProducto();
                DetallesTimbradoViewController.fechaP=p.getFechaDePago();
                DetallesTimbradoViewController.totalP=p.getTotal();
                DetallesTimbradoViewController.totalR=p.getRegistros();

                


                System.out.println(p.getProducto());

                //Cambiamos la escena
                root = FXMLLoader.load(getClass().getResource("/view/DetallesTimbradoView.fxml"));
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
        dataTimbrables.clear();
        dataTimbrados.clear();
        //Abrimos conexión
        classes.MySQL mysql= new classes.MySQL();
        mysql.conectar();
        //Cargamos Timbrables
        ResultSet rs = mysql.select("SELECT * FROM satin.productos_timbrables");
        try {
            // Cargamos las columnas de manera dinámica. Lanza advertencia por no checar tipos de variables pero para nuestro uso no nos afecta.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new PropertyValueFactory<ProductoTimbrable,String>(rs.getMetaData().getColumnName(i+1)));
                tblTimbrables.getColumns().addAll(col); 
            }
            //Cargamos los registros a una lista. Se rompe con datos nulos, checar.
            while(rs.next()){
                //Iterate Row
                ProductoTimbrable row = new ProductoTimbrable(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7));
                dataTimbrables.add(row);
            }
            //Cargamos los resultados a la tabla
            tblTimbrables.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); 

            tblTimbrables.setItems(dataTimbrables);
            
        } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexión.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
        
        
        //Carga de puestos pendientes
        rs = mysql.select("SELECT * FROM satin.productos_timbrados");
        try {
            // Cargamos las columnas de manera dinámica. Lanza advertencia por no checar tipos de variables pero para nuestro uso no nos afecta.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new PropertyValueFactory<ProductoTimbrable,String>(rs.getMetaData().getColumnName(i+1)));
                tblTimbrados.getColumns().addAll(col); 
            }
            //Cargamos los registros a una lista. Se rompe con datos nulos, checar.
            while(rs.next()){
                //Iterate Row
                ProductoTimbrable row = new ProductoTimbrable(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7));
                dataTimbrados.add(row);
            }
            //Cargamos los resultados a la tabla
            tblTimbrados.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); 

            tblTimbrados.setItems(dataTimbrables);
            
        } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexión.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        
        mysql.desconectar();
        
        
        //Busqueda Pestaña Timbrables
        FilteredList<ProductoTimbrable> filteredDataTimbrables = new FilteredList<>(dataTimbrables, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		txtBusquedaTimbrables.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredDataTimbrables.setPredicate(timbrable -> {
				// If filter text is empty, display all persons.
								
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (timbrable.getProducto().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
					return true; // Un if por cada campo con su getter para compararlo
				} else if (timbrable.getProducto().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (timbrable.getAño().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (timbrable.getQuincena().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (timbrable.getFechaDePago().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
				else if (timbrable.getTotal().toLowerCase().indexOf(lowerCaseFilter) != -1){
				     return true;
                                }
                                else if (timbrable.getTipoNomina().toLowerCase().indexOf(lowerCaseFilter) != -1){
				     return true;
                                }else  
				    	 return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<ProductoTimbrable> sortedDataTimbrables = new SortedList<>(filteredDataTimbrables);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedDataTimbrables.comparatorProperty().bind(tblTimbrables.comparatorProperty());
		
		// 5. Add sorted (and filtered) dataTimbrables to the table.
                tblTimbrables.setItems(sortedDataTimbrables);
        
                
        
        //Busqueda Pestaña Puestos
        FilteredList<ProductoTimbrable> filteredDataTimbrados = new FilteredList<>(dataTimbrados, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		txtBusquedaTimbrados.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredDataTimbrados.setPredicate(timbrable -> {
				// If filter text is empty, display all persons.
								
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				
		if (timbrable.getProducto().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
					return true; // Un if por cada campo con su getter para compararlo
				} else if (timbrable.getProducto().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (timbrable.getAño().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (timbrable.getQuincena().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                else if (timbrable.getFechaDePago().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
				else if (timbrable.getTotal().toLowerCase().indexOf(lowerCaseFilter) != -1){
				     return true;
                                }
                                else if (timbrable.getTipoNomina().toLowerCase().indexOf(lowerCaseFilter) != -1){
				     return true;
                                }else  
				    	 return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<ProductoTimbrable> sortedDataTimbrados = new SortedList<>(filteredDataTimbrados);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedDataTimbrados.comparatorProperty().bind(tblTimbrados.comparatorProperty());
		
                tblTimbrados.setItems(sortedDataTimbrados);
                
        
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load();
    }    
    
}
