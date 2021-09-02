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
public class ConceptosSATViewController implements Initializable {
    @FXML Label lbl;
    @FXML TextField txtClave;
    @FXML TextField txtDescripcion;
    @FXML TextField txtClaveSAT;
    @FXML TextField txtTipo;

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
        classes.MySQL mysql = new MySQL();
        mysql.conectar();
        if(edicion){
            lbl.setText("Editar Conceptos SAT");
            String sql = "SELECT * FROM satin.conceptos_sat where clave='"+idEdicion+"';";
            ResultSet rs=mysql.select(sql);
            System.out.println(sql);
            try {
                rs.next();
                txtClave.setText(rs.getString(1));
                txtDescripcion.setText(rs.getString(2));
                txtClaveSAT.setText(rs.getString(3));
                txtTipo.setText(rs.getString(4));

            } catch (SQLException ex) {
                Logger.getLogger(ConceptosSATViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            mysql.desconectar();

    }    
    @FXML private void btnRegresar (ActionEvent event) throws IOException{

        //Cambiamos la escena
        root = FXMLLoader.load(getClass().getResource("/view/LConceptosSATView.fxml"));
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
        String clave = txtClave.getText();
        String descripcion = txtDescripcion.getText();
        String claveSAT = txtClaveSAT.getText();
        String tipo = txtTipo.getText();
        
        if(!clave.equals("")&&!descripcion.equals("")&&!claveSAT.equals("")&&!tipo.equals("")){
            classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            if(edicion){
               String sql= "UPDATE satin.conceptos_sat SET `clave`='"+clave+"',`descripcion`='"+descripcion+"',`clave_sat`='"+claveSAT+"',`tipo`='"+tipo+"' WHERE `clave`='"+idEdicion+"'";
               System.out.println("Entro");
               System.out.println(sql);
                if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha actualizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LConceptosSATView.fxml"));
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
               String sql= "INSERT INTO satin.conceptos_sat (`clave`, `descripcion`, `clave_sat`, `tipo`) VALUES ('"+clave+"','"+descripcion+"','"+claveSAT+"','"+tipo+"')";
               System.out.println(sql);
            if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha realizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LConceptosSATView.fxml"));
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
