/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.MySQL;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Jorge
 */
public class PuestosViewController implements Initializable {
    @FXML Label lbl;
    @FXML TextField txtID;
    @FXML TextField txtDescripcion;
    @FXML ChoiceBox cmbCategoria;
    @FXML TextField txtRama;
    @FXML Button btnRegistrar;
    public static boolean edicion=false;
    public static String idEdicion;
    private Stage stage;
    private Scene scene;
    private Parent root;
    /**
     * Initializes the controller class.
     */
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbCategoria.getItems().add((Object)"A");
        cmbCategoria.getItems().add((Object)"B");
        cmbCategoria.getItems().add((Object)"C");


        if(edicion){
            lbl.setText("Editar Puesto");
            classes.MySQL mysql = new MySQL();
            mysql.conectar();
            String sql = "SELECT * FROM satin.puestos where id_puestos="+idEdicion+";";
            ResultSet rs=mysql.select(sql);
            try {
                rs.next();
                txtID.setText(rs.getString(1));
                txtDescripcion.setText(rs.getString(2));
                cmbCategoria.getSelectionModel().select(rs.getString(3));
                txtRama.setText(rs.getString(4));

            } catch (SQLException ex) {
                Logger.getLogger(PuestosViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(edicion==false&&!idEdicion.equals("")){
            txtID.setText(idEdicion);
            txtID.setEditable(false);

        }
    }    
        @FXML private void btnRegresar (ActionEvent event) throws IOException{

        //Cambiamos la escena
        root = FXMLLoader.load(getClass().getResource("/view/LPuestosView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        //Creamos un rectángulo del tamaño de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();
    }
    @FXML public void btnRegistrar(ActionEvent event) throws IOException{
        String id = txtID.getText();
        String descripcion = txtDescripcion.getText();
        String categoria=cmbCategoria.getValue().toString();
        String rama=txtRama.getText();
        if(!id.equals("")&&!descripcion.equals("")&!categoria.equals("")&!rama.equals("")){
            classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            if(edicion){
               String sql= "UPDATE satin.puestos SET `id_puestos`='"+id+"',`descripcion`='"+descripcion+"',`categoria`='"+categoria+"',`rama`='"+rama+"' WHERE `id_puestos`='"+idEdicion+"'";
               System.out.println("Entro");
               System.out.println(sql);
                if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha actualizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LPuestosView.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                //Creamos un rectángulo del tamaño de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                stage.show();
                mysql.desconectar();

            }else{
                mysql.desconectar();
                JOptionPane.showMessageDialog(null, "Error en la actualizacion, intente denuevo más tarde..", "Error", JOptionPane.WARNING_MESSAGE);
            }
            }else if(!edicion&&!idEdicion.equals("")){
                
               String sql= "INSERT INTO satin.puestos (id_puestos,descripcion,categoria,rama) VALUES('"+id+"','"+descripcion+"','"+categoria+"','"+rama+"')";
            if(mysql.stmt(sql)){
                //Si se inserta correctamente pasmos a validados todos los que tenían ese puesto pendiente 
                                  try {
                                    ResultSet rs= mysql.select("select id,rfc from satin.detalle_nomina where puesto='"+idEdicion+"'");
                                    PreparedStatement pstmtVConceptos=mysql.conn.prepareStatement("UPDATE `satin`.`detalle_nomina` SET `vpuesto` = 'V' WHERE (`id` = ?) and (`rfc` = ?)");
                                    while(rs.next()){
                                            pstmtVConceptos.setString(1, rs.getString(1));
                                            pstmtVConceptos.setString(2, rs.getString(2));
                                            pstmtVConceptos.addBatch();
                                    }
                                    pstmtVConceptos.executeBatch();
       
                                  } catch (SQLException ex) {
                                      Logger.getLogger(ConceptosViewController.class.getName()).log(Level.SEVERE, null, ex);
                                  }
                JOptionPane.showMessageDialog(null, "El registro se ha realizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                sql="";
                root = FXMLLoader.load(getClass().getResource("/view/ValidacionesPendientes.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                //Creamos un rectángulo del tamaño de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                stage.show();
                edicion=false;
                idEdicion="";
                mysql.desconectar();
            }else{
                    mysql.desconectar();
                    JOptionPane.showMessageDialog(null, "Error en el registro, intente denuevo más tarde..", "Error", JOptionPane.WARNING_MESSAGE);
                }
                
                
                
            }else{
               String sql= "INSERT INTO satin.puestos (id_puestos,descripcion,categoria,rama) VALUES('"+id+"','"+descripcion+"','"+categoria+"','"+rama+"')";
            if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha realizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LPuestosView.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                //Creamos un rectángulo del tamaño de la pantalla para obtener medidas y centrar la ventana antes de mostrarla
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                stage.show();
                mysql.desconectar();

            }else{
                mysql.desconectar();
                JOptionPane.showMessageDialog(null, "Error en el registro, intente denuevo más tarde..", "Error", JOptionPane.WARNING_MESSAGE);
            }
            }
            
        }else{
            JOptionPane.showMessageDialog(null, "Ingrese todos los datos.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
}
