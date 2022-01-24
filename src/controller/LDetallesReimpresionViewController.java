/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.DetallesTimbrados;
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
public class LDetallesReimpresionViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML private TableView<DetallesTimbrados> tblRegistros;
    @FXML private TextField txtBusqueda;

    private final ObservableList<DetallesTimbrados> data=FXCollections.observableArrayList();;

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
        ResultSet rs = mysql.select("SELECT id,rfc,nombre,CONCAT('$', FORMAT(`total1`, 2)) AS `Percepciones`,CONCAT('$', FORMAT(`total2`, 2)) AS `Deducciones`,fechapago as \"Pagado\" FROM satin.detalles_timbrar;");
        try {
            // Cargamos las columnas de manera dinámica. Lanza advertencia por no checar tipos de variables pero para nuestro uso no nos afecta.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                System.out.println(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new PropertyValueFactory<DetallesTimbrados,String>(rs.getMetaData().getColumnName(i+1)));
                tblRegistros.getColumns().addAll(col); 
                System.out.println("Column ["+i+"] ");
            }
            //Cargamos los registros a una lista. Se rompe con datos nulos, checar.
            while(rs.next()){
                //Iterate Row
                DetallesTimbrados row = new DetallesTimbrados(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6));
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
        
     
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load();
    }    
    
}
