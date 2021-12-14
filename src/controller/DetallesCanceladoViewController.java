/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.Bancos;
import classes.DetalleTimbrable;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import classes.CalcularXML;
import classes.GenerarXML;
import classes.Timbrar;
import static controller.DetallesTimbradoViewController.fmt;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * FXML Controller class
 *
 * @author Jorge
 */
public class DetallesCanceladoViewController implements Initializable {
    static NumberFormat fmt = NumberFormat.getInstance(Locale.US);

    /**
     * Initializes the controller class.
     */
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML private TableView<DetalleTimbrable> tblRegistros;
    @FXML private TextField txtBusqueda;
    public static String idProducto;
    public static String fechaP;
    public static String totalP;
    public static String totalR;
    PreparedStatement pstmtCancelar;
    String[] uuid;

    List<CalcularXML> xmlList=new ArrayList<CalcularXML>();
    List<DetalleTimbrable> detallesList=new ArrayList<DetalleTimbrable>();







    private final ObservableList<DetalleTimbrable> data=FXCollections.observableArrayList();;

    @FXML Label txtProducto;
    @FXML Label txtFechaI;
    @FXML Label txtFechaF;
    @FXML Label txtFechaP;
    @FXML Label txtTotal;
    @FXML Label txtTotalR;

    @FXML private void btnCancelar(ActionEvent event) throws IOException, Exception{
        classes.MySQL mysql=new classes.MySQL();
        mysql.conectar();
        String motivo=JOptionPane.showInputDialog("Ingrese el motivo de la cancelación:");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String fechaC=sdf.format(ts);
        String updateC="UPDATE `satin`.`detalle_nomina` SET `fechacancelacion` = ?, `motivo_can` = ? WHERE (`id` = ?);";
        pstmtCancelar=mysql.conn.prepareStatement(updateC);
        for(DetalleTimbrable dt : data) {
            pstmtCancelar.setString(1, fechaC);
            pstmtCancelar.setString(2, motivo);
            pstmtCancelar.setString(3, dt.getID());
            pstmtCancelar.addBatch();
        }
        pstmtCancelar.executeBatch();
        String update="UPDATE `satin`.`producto_nomina` SET `estado` = 'CANCELADO', `timbrados` = '0', `cancelados` = '"+totalR+"' WHERE (`clave` = '"+idProducto+"');";
        mysql.stmt(update);
        Timbrar t=new Timbrar();
        t.cancelar(uuid);
        JOptionPane.showMessageDialog(null,"El producto se ha cancelado exitosamente.");
        //Cambiamos la escena
        root = FXMLLoader.load(getClass().getResource("/view/LTimbrarProductosView.fxml"));
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
        fmt.setMaximumFractionDigits(2);
        fmt.setMinimumFractionDigits(2);
        classes.MySQL mysql= new classes.MySQL();
        mysql.conectar();
        ResultSet rs = mysql.select("Select * from detalles_timbrar where producto='"+idProducto+"'");
        try {
            // Cargamos las columnas de manera dinámica. Lanza advertencia por no checar tipos de variables pero para nuestro uso no nos afecta.
                TableColumn col1 = new TableColumn("ID");
                col1.setCellValueFactory(new PropertyValueFactory<DetalleTimbrable,String>("ID"));
                tblRegistros.getColumns().addAll(col1); 
                TableColumn col2 = new TableColumn("RFC");
                col2.setCellValueFactory(new PropertyValueFactory<DetalleTimbrable,String>("RFC"));
                tblRegistros.getColumns().addAll(col2); 
                TableColumn col3 = new TableColumn("Nombre");
                col3.setCellValueFactory(new PropertyValueFactory<DetalleTimbrable,String>("Nombre"));
                tblRegistros.getColumns().addAll(col3); 
                TableColumn col4 = new TableColumn("Percepciones");
                col4.setCellValueFactory(new PropertyValueFactory<DetalleTimbrable,String>("Percepciones"));
                tblRegistros.getColumns().addAll(col4); 
                TableColumn col5 = new TableColumn("Deducciones");
                col5.setCellValueFactory(new PropertyValueFactory<DetalleTimbrable,String>("Deducciones"));
                tblRegistros.getColumns().addAll(col5); 
                TableColumn col6 = new TableColumn("TotalFactura");
                col6.setCellValueFactory(new PropertyValueFactory<DetalleTimbrable,String>("TotalFactura"));
                tblRegistros.getColumns().addAll(col6); 
                TableColumn col7 = new TableColumn("Puesto");
                col7.setCellValueFactory(new PropertyValueFactory<DetalleTimbrable,String>("Puesto"));
                tblRegistros.getColumns().addAll(col7); 
                TableColumn col8 = new TableColumn("Contrato");
                col8.setCellValueFactory(new PropertyValueFactory<DetalleTimbrable,String>("Contrato"));
                tblRegistros.getColumns().addAll(col8); 
                
            uuid=new String[Integer.valueOf(totalR)];
            CalcularXML c=new CalcularXML();
            //Cargamos los registros a una lista. Se rompe con datos nulos, checar.
            int i=0;
            while(rs.next()){
                uuid[i]=rs.getString(32);
                System.out.println(uuid[i]);
                c=new CalcularXML(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19),rs.getString(20),rs.getString(21),rs.getString(22),rs.getString(23),rs.getString(24),rs.getString(25),rs.getString(26),rs.getString(27),rs.getString(28),rs.getString(29),rs.getString(30),rs.getString(31));
                xmlList.add(c);
                String total="$"+fmt.format(c.getSubtotal()-c.getDescuentoFactura());

                DetalleTimbrable d=new DetalleTimbrable(c.getId(),c.getRfc(),c.getNombre(),"$"+fmt.format(c.getTotalGravado()+c.getTotalExento()),"$"+fmt.format(c.getDescuentoFactura()),total,c.getDescripcionPuesto(),c.getContrato());
                
                //Iterate Row
                DetalleTimbrable row = d;
                data.add(row);
            }
            txtFechaI.setText(txtFechaI.getText()+c.getFechai());
            txtFechaF.setText(txtFechaF.getText()+c.getFechaf());
            //Cargamos los resultados a la tabla
            tblRegistros.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); 

            tblRegistros.setItems(data);
            
            

        } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexión.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        
        mysql.desconectar();
        
                // Envolvemos la lista observable en una lista filtrada
        FilteredList<DetalleTimbrable> filteredData = new FilteredList<>(data, b -> true);
		
		// Agregamos un listener al campo de texto de búsqueda
		txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(detalle -> {
				// Si está vacío muestra todo
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				//Convierte a minúsculas todo para hacer la comparación
				String lowerCaseFilter = newValue.toLowerCase();
				if (detalle.getID().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
					return true; // Un if por cada campo con su getter para compararlo
				} else if (detalle.getRFC().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; 
				} else if (detalle.getNombre().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; 
				} else if (detalle.getPercepciones().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; 
				} else if (detalle.getDeducciones().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; 
				}
				else if (detalle.getTotalFactura().toLowerCase().indexOf(lowerCaseFilter) != -1)
				     return true;
				     else  
				    	 return false; // Does not match.
                                });
		});

		
		// Ordenamos la lista
		SortedList<DetalleTimbrable> sortedData = new SortedList<>(filteredData);
		
		//Ligamos la lista ordenada a la tabla 
		sortedData.comparatorProperty().bind(tblRegistros.comparatorProperty());
		
		// 5. Agregamos los datos filtrados a la tabla
                tblRegistros.setItems(sortedData);

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("El producto es "+idProducto);
        load();
        txtProducto.setText(txtProducto.getText()+idProducto);
        txtFechaP.setText(txtFechaP.getText()+fechaP);
        txtTotal.setText(txtTotal.getText()+totalP);
        txtTotalR.setText(txtTotalR.getText()+totalR);
    }    
    
}
