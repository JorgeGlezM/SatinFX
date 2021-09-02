/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import classes.MySQL;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class ConceptosViewController implements Initializable {
    @FXML Label lbl;
    @FXML TextField txtID;
    @FXML TextField txtClave;
    @FXML TextField txtDescripcion;
    @FXML ChoiceBox cmbActivo;

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
        System.out.println(edicion);
        cmbActivo.getItems().add("S");
        cmbActivo.getItems().add("N");
        cmbActivo.getSelectionModel().select("S");
        classes.MySQL mysql = new MySQL();
        mysql.conectar();
        if(edicion){
            lbl.setText("Editar Conceptos");
            String sql = "SELECT * FROM satin.conceptos where id_concepto='"+idEdicion+"';";
            ResultSet rs=mysql.select(sql);
            System.out.println(sql);
            try {
                rs.next();
                txtID.setText(rs.getString(1));
                txtClave.setText(rs.getString(2));
                txtDescripcion.setText(rs.getString(3));
                cmbActivo.getSelectionModel().select(rs.getString(4));


            } catch (SQLException ex) {
                Logger.getLogger(ConceptosViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            mysql.desconectar();

    }    
    @FXML public void btnRegistrar(ActionEvent event) throws IOException{
        String id = txtID.getText();
        String clave = txtClave.getText();
        String descripcion = txtDescripcion.getText();
        String activo = (String) cmbActivo.getValue();
        
        if(!id.equals("")&&!descripcion.equals("")&&!clave.equals("")&&!activo.equals("")){
            classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            if(edicion){
               String sql= "UPDATE satin.conceptos SET `id_concepto`='"+id+"',`clave_sat`='"+clave+"',`descripcion`='"+descripcion+"',`activo`='"+activo+"' WHERE `id_concepto`='"+idEdicion+"'";
               System.out.println("Entro");
               System.out.println(sql);
                if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha actualizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LConceptosView.fxml"));
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
                JOptionPane.showMessageDialog(null, "Error en la actualizacion, intente denuevo más tarde..", "Error", JOptionPane.WARNING_MESSAGE);
            }
            }else{
               String sql= "INSERT INTO satin.conceptos (`id_concepto`, `clave_sat`, `descripcion`, `activo`) VALUES ('"+id+"','"+clave+"','"+descripcion+"','"+activo+"')";
               System.out.println(sql);
            if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha realizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LConceptosView.fxml"));
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
