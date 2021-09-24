/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import classes.MySQL;
import classes.Numero_Letras;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
/**
 * FXML Controller class
 *
 * @author Jorge
 */
public class LoginViewController implements Initializable {
    //Declaramos componentes
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnBypass;
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    //Declaramos eventos
    
        @FXML private void Login (ActionEvent event) throws IOException{
            Object evt = event.getSource();
            if(evt.equals(btnLogin)){
                if(!txtUser.getText().isEmpty() && !txtPassword.getText().isEmpty()){
        String user = txtUser.getText();
        String pass =txtPassword.getText();
        String query="SELECT password FROM usuarios WHERE usuario = \""+user+"\"";
            MySQL mysql = new MySQL();
            try {
            //Abrimos la conexión con la BD y creamos un statement donde busquemos al usuario ingresado para obtener su contraseña.
            mysql.conectar();
            ResultSet rs;
            rs = mysql.select("SELECT password FROM usuarios WHERE usuario = \""+user+"\"");
            //Verificamos que haya resultados y si los hay comparamos la cadena con la ingresada, en caso de ser igual se cierra la ventana de login y se abre el menú principal.va 
            if(rs.next()){
            String contra = rs.getString("password");
            if(contra.equals(pass)){
                System.out.println("Coincide");
                //Asignamos el Bean
                classes.Bean.setUser(user);
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
            else {
                JOptionPane.showMessageDialog(null, "Los datos ingresados no son correctos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
            mysql.desconectar();
            }else{
                JOptionPane.showMessageDialog(null, "Los datos ingresados no son correctos", "Advertencia", JOptionPane.WARNING_MESSAGE);
                mysql.desconectar();
            }

        } catch (SQLException ex) {
            Logger.getLogger(LoginViewController.class.getName()).log(Level.SEVERE, null, ex);
            mysql.desconectar();
        }
                }else{
                    JOptionPane.showMessageDialog(null, "Datos incompletos. Intente otra vez.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }    }


        @FXML private void Bypass (ActionEvent event) throws IOException{
            Object evt = event.getSource();
            if(evt.equals(btnBypass)){
                System.out.println("Bypass");
                //Asignamos el Bean
                classes.Bean.setUser("jglez");
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
        }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Código basura para testear convertir números a letras. 
        Numero_Letras test= new Numero_Letras();
        test.Convertir("1995.32",true);
        System.out.println(test.Convertir("3233321.32",true));
     }    
    
}
