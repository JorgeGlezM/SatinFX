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
public class CentrosTrabajoViewController implements Initializable {
    @FXML Label lbl;
    @FXML TextField txtID;
    @FXML TextField txtNombre;
    @FXML TextField txtDireccion;
    @FXML ChoiceBox cmbEstatus;
    @FXML TextField txtClave;
    @FXML TextField txtEdoMpo;

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
        cmbEstatus.getItems().add("Activo");
        cmbEstatus.getItems().add("Inactivo");
        cmbEstatus.getSelectionModel().select("Activo");
        if(edicion){
            lbl.setText("Editar Centro de Trabajo");
            classes.MySQL mysql = new MySQL();
            mysql.conectar();
            String sql = "SELECT * FROM satin.centrotrabajo where id_centrotrabajo='"+idEdicion+"';";
            ResultSet rs=mysql.select(sql);
            System.out.println(sql);
            try {
                rs.next();
                txtID.setText(rs.getString(1));
                txtNombre.setText(rs.getString(2));
                txtDireccion.setText(rs.getString(3));
                cmbEstatus.getSelectionModel().select(rs.getString(4));
                txtClave.setText(rs.getString(5));
                txtEdoMpo.setText(rs.getString(6));


            } catch (SQLException ex) {
                Logger.getLogger(CentrosTrabajoViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
    @FXML public void btnRegistrar(ActionEvent event) throws IOException{
        String id = txtID.getText();
        String nombre = txtNombre.getText();
        String direccion = txtDireccion.getText();
        String estatus = (String) cmbEstatus.getValue();
        String clave = txtClave.getText();
        String edompo = txtEdoMpo.getText();
        
        if(!id.equals("")&&!nombre.equals("")&&!direccion.equals("")&&!estatus.equals("")&&!clave.equals("")&&!edompo.equals("")){
            classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            if(edicion){
               String sql= "UPDATE satin.centrotrabajo SET `id_centrotrabajo`='"+id+"',`nombre`='"+nombre+"',`direccion`='"+direccion+"',`estatus`='"+estatus+"',`clave`='"+clave+"',`edo_mun`='"+edompo+"' WHERE `id_centrotrabajo`='"+idEdicion+"'";
               System.out.println("Entro");
               System.out.println(sql);
                if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha actualizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LCentrosTrabajoView.fxml"));
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
            }else{
               String sql= "INSERT INTO satin.centrotrabajo (`id_centrotrabajo`, `nombre`, `direccion`, `estatus`, `clave`, `edo_mun`) VALUES ('"+id+"','"+nombre+"','"+direccion+"','"+estatus+"','"+clave+"','"+edompo+"')";
               System.out.println(sql);
            if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha realizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LCentrosTrabajoView.fxml"));
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
