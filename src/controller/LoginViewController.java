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
            MySQL mysql = new MySQL();
            try {
            //Abrimos la conexión con la BD y creamos un statement donde busquemos al usuario ingresado para obtener su contraseña.
            mysql.conectar();
            ResultSet rs;
            rs = mysql.select("SELECT password FROM usuarios WHERE usuario = \""+user+"\"");
            //Verificamos que haya resultados y si los hay comparamos la cadena con la ingresada, en caso de ser igual
            //se cierra la ventana de login y se abre el menú principal.va 
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
    //Cargamos los datos patronales (estáticos)
    loadDatos();
     }    
    public void loadDatos(){
        MySQL mysql = new MySQL();
        mysql.conectar();
        ResultSet rs= mysql.select("SELECT nombre,rfc,registro_patronal,calle,numero_ext,numero_int,poblacion,estado,cp,pais,riesgo_puesto,periodo_pago,email,'host',puerto,'ssl',usuario,'password',regimen, serie, entidad_sncf,codigo_esp FROM satin.datospatronales;");
        try {
            rs.next();
            classes.DatosPatronales.setNombre(rs.getString(1));
            classes.DatosPatronales.setRfc(rs.getString(2));
            classes.DatosPatronales.setRegistro_patronal(rs.getString(3));
            classes.DatosPatronales.setCalle(rs.getString(4));
            classes.DatosPatronales.setNumero_ext(rs.getString(5));
            classes.DatosPatronales.setNumero_int(rs.getString(6));
            classes.DatosPatronales.setPoblacion(rs.getString(7));
            classes.DatosPatronales.setEstado(rs.getString(8));
            classes.DatosPatronales.setCp(rs.getString(9));
            classes.DatosPatronales.setPais(rs.getString(10));
            classes.DatosPatronales.setRiesgo_puesto(rs.getString(11));
            classes.DatosPatronales.setPeriodo_pago(rs.getString(12));
            classes.DatosPatronales.setEmail(rs.getString(13));
            classes.DatosPatronales.setHost(rs.getString(14));
            classes.DatosPatronales.setPuerto(rs.getString(15));
            classes.DatosPatronales.setSsl(rs.getString(16));
            classes.DatosPatronales.setUsuario(rs.getString(17));
            classes.DatosPatronales.setPassword(rs.getString(18));
            classes.DatosPatronales.setRegimen(rs.getString(19));
            classes.DatosPatronales.setSerie(rs.getString(20));
            classes.DatosPatronales.setEntidad_sncf(rs.getString(21));
            classes.DatosPatronales.setCodigo_esp(rs.getString(22));    
            
            rs=mysql.select("SELECT fecha_vig_inicial,fecha_vig_final,version_cfdi,version_nomina,xml_xsi,xml_cfdi,xml_nomina,xml_esquema,password_key,num_cert,archivo_cert,archivo_key,timbres FROM satin.datostimbrado");
            rs.next();
            System.out.println(rs.getString(6));
            classes.DatosTimbrado.setFecha_vig_inicial(rs.getString(1));
            classes.DatosTimbrado.setFecha_vig_final(rs.getString(2));
            classes.DatosTimbrado.setVersion_cfdi(rs.getString(3));
            classes.DatosTimbrado.setVersion_nomina(rs.getString(4));
            classes.DatosTimbrado.setXml_xsi(rs.getString(5));
            classes.DatosTimbrado.setXml_cfdi(rs.getString(6));
            classes.DatosTimbrado.getXml_cfdi();
            classes.DatosTimbrado.setXml_nomina(rs.getString(7));
            classes.DatosTimbrado.setXml_esquema(rs.getString(8));
            classes.DatosTimbrado.setPassword_key(rs.getString(9));
            classes.DatosTimbrado.setNum_cert(rs.getString(10));
            classes.DatosTimbrado.setArchivo_cert(rs.getString(11));
            classes.DatosTimbrado.setArchivo_key(rs.getString(12));
            classes.DatosTimbrado.setTimbres(rs.getString(13));

            mysql.desconectar();
        } catch (SQLException ex) {
            Logger.getLogger(LoginViewController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Los datos patronales no han sido cargados. Favor de revisar su conexión y reiniciar el sistema.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            btnLogin.setVisible(false);
            btnBypass.setVisible(false);
        }
    }
    
}
