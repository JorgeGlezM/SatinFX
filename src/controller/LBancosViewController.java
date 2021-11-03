/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.Bancos;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Jorge
 */
public class LBancosViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML private TableView<Bancos> tblRegistros;
    @FXML private TextField txtBusqueda;

    private final ObservableList<Bancos> data=FXCollections.observableArrayList();;

    @FXML Button btnAdd;
    @FXML Button bdnEdit;
    @FXML private void btnAdd (ActionEvent event) throws IOException{
                
    //Cambiamos la escena
                BancosViewController.edicion=false;
                root = FXMLLoader.load(getClass().getResource("/view/BancosView.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                //Creamos un rectángulo del tamaño de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                stage.show();
    }
        @FXML private void btnEdit (ActionEvent event) throws IOException{
                
                //Obtenemos el valor del campo "id" para pasarlo a la ventana de edición como parametro.
                Bancos b = tblRegistros.getSelectionModel().getSelectedItem();
                BancosViewController.idEdicion=b.getId();
                BancosViewController.edicion=true;

                //Cambiamos la escena
                root = FXMLLoader.load(getClass().getResource("/view/BancosView.fxml"));
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
        classes.MySQL mysql= new classes.MySQL();
        mysql.conectar();
        ResultSet rs = mysql.select("Select * from bancos");
        try {
            // Cargamos las columnas de manera dinámica. Lanza advertencia por no checar tipos de variables pero para nuestro uso no nos afecta.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                System.out.println(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new PropertyValueFactory<Bancos,String>(rs.getMetaData().getColumnName(i+1)));
                tblRegistros.getColumns().addAll(col); 
                System.out.println("Column ["+i+"] ");
            }
            //Cargamos los registros a una lista. Se rompe con datos nulos, checar.
            while(rs.next()){
                //Iterate Row
                Bancos row = new Bancos(rs.getString(1),rs.getString(2),rs.getString(3));
                System.out.println("Row [1] added "+row );
                data.add(row);
                System.out.println("Si lo agrega");
            }
            //Cargamos los resultados a la tabla
            tblRegistros.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); 

            tblRegistros.setItems(data);
            
            

        } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexión.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        
        mysql.desconectar();
        
                // Envolvemos la lista observable en una lista filtrada
        FilteredList<Bancos> filteredData = new FilteredList<>(data, b -> true);
		
		// Agregamos un listener al campo de texto de búsqueda
		txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(banco -> {
				// Si está vacío muestra todo
								
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				//Convierte a minúsculas todo para hacer la comparación
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (banco.getId().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
					return true; // Un if por cada campo con su getter para compararlo
				} else if (banco.getNombre().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; 
				}
				else if (banco.getDescripcion().toLowerCase().indexOf(lowerCaseFilter) != -1)
				     return true;
				     else  
				    	 return false; // Does not match.
			});
		});
		
		// Ordenamos la lista
		SortedList<Bancos> sortedData = new SortedList<>(filteredData);
		
		//Ligamos la lista ordenada a la tabla 
		sortedData.comparatorProperty().bind(tblRegistros.comparatorProperty());
		
		// 5. Agregamos los datos filtrados a la tabla
                tblRegistros.setItems(sortedData);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load();
    }    
    
}
