/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.Puestos;
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
public class LPuestosViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML private TableView<Puestos> tblRegistros;
    @FXML private TextField txtBusqueda;

    private final ObservableList<Puestos> data=FXCollections.observableArrayList();;

    @FXML Button btnAdd;
    @FXML Button bdnEdit;
    @FXML private void btnAdd (ActionEvent event) throws IOException{
                
    //Cambiamos la escena
                PuestosViewController.edicion=false;
                root = FXMLLoader.load(getClass().getResource("/view/PuestosView.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                //Creamos un rect??ngulo del tama??o de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                stage.show();
    }
        @FXML private void btnEdit (ActionEvent event) throws IOException{
                
                //Obtenemos el valor del campo "id" para pasarlo a la ventana de edici??n como parametro.
                Puestos b = tblRegistros.getSelectionModel().getSelectedItem();
                PuestosViewController.idEdicion=b.getId_puestos();
                PuestosViewController.edicion=true;

                //Cambiamos la escena
                root = FXMLLoader.load(getClass().getResource("/view/PuestosView.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                //Creamos un rect??ngulo del tama??o de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
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
        //Creamos un rect??ngulo del tama??o de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();
    }
    public void load(){
        classes.MySQL mysql= new classes.MySQL();
        mysql.conectar();
        ResultSet rs = mysql.select("Select * from puestos");
        try {
            // Cargamos las columnas de manera din??mica. Lanza advertencia por no checar tipos de variables pero para nuestro uso no nos afecta.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                System.out.println(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new PropertyValueFactory<Puestos,String>(rs.getMetaData().getColumnName(i+1)));
                tblRegistros.getColumns().addAll(col); 
                System.out.println("Column ["+i+"] ");
            }
            //Cargamos los registros a una lista. Se rompe con datos nulos, checar.
            while(rs.next()){
                //Iterate Row
                Puestos row = new Puestos(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4));
                System.out.println("Row [1] added "+row );
                data.add(row);
                System.out.println("Si lo agrega");
            }
            //Cargamos los resultados a la tabla
            tblRegistros.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); 

            tblRegistros.setItems(data);
            
            

        } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexi??n.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        
        mysql.desconectar();
        
                // Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Puestos> filteredData = new FilteredList<>(data, b -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(puesto -> {
				// If filter text is empty, display all persons.
								
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (puesto.getId_puestos().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
					return true; // Un if por cada campo con su getter para compararlo
				} else if (puesto.getDescripcion().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
                                 else if (puesto.getCategoria().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				}
				else if (puesto.getRama().toLowerCase().indexOf(lowerCaseFilter) != -1)
				     return true;
				     else  
				    	 return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<Puestos> sortedData = new SortedList<>(filteredData);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(tblRegistros.comparatorProperty());
		
		// 5. Add sorted (and filtered) data to the table.
                tblRegistros.setItems(sortedData);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load();
    }    
    
}
