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
public class EmpleadosViewController implements Initializable {
    @FXML Label lbl;
    @FXML TextField txtClave;
    @FXML TextField txtNombre;
    @FXML TextField txtApellidoP;
    @FXML TextField txtApellidoM;
    @FXML TextField txtRFC;
    @FXML TextField txtCURP;
    @FXML TextField txtNSS;
    @FXML DatePicker dpFecha;
    @FXML ChoiceBox cmbActivo;
    @FXML TextField txtRepss;
    @FXML TextField txtCorreo;
    @FXML TextField txtContrasena;
    @FXML ChoiceBox cmbJornada;
    @FXML TextField txtAnio;
    @FXML TextField txtUltimoS;
    @FXML TextField txtIngresoA;
    @FXML TextField txtIngresoN;
    @FXML TextField txtTurno;
    @FXML TextField txtCedula;
    @FXML TextField txtTipoC;
    @FXML TextField txtEntidadC;
    @FXML TextField txtEspecialidad;
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
        this.cmbActivo.getItems().add((Object)"S");
        this.cmbActivo.getItems().add((Object)"N");
        
        //Jornadas temporales
        this.cmbJornada.getItems().add((Object)"Jornada1");
        this.cmbJornada.getItems().add((Object)"Jornada2");


        if(edicion){
            lbl.setText("Editar Empleado");
            classes.MySQL mysql = new MySQL();
            mysql.conectar();
            String sql = "SELECT * FROM satin.empleados where clave="+idEdicion+";";
            ResultSet rs=mysql.select(sql);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                rs.next();
                txtClave.setText(rs.getString(1));
                txtNombre.setText(rs.getString(2));
                txtApellidoP.setText(rs.getString(3));
                txtApellidoM.setText(rs.getString(4));
                txtRFC.setText(rs.getString(5));
                txtCURP.setText(rs.getString(6));
                txtNSS.setText(rs.getString(7));
                System.out.println(rs.getString(8));
                //Damos formato a la fecha para ponerla en el datePicker
                LocalDate localDate = LocalDate.parse(rs.getString(8), formatter);
                dpFecha.setValue(localDate);
                txtCorreo.setText(rs.getString(9));
                cmbActivo.getSelectionModel().select(rs.getString(10));
                txtContrasena.setText(rs.getString(11));
                txtRepss.setText(rs.getString(12));
                cmbJornada.getSelectionModel().select(rs.getString(13));
                txtAnio.setText(rs.getString(14));
                txtUltimoS.setText(rs.getString(15));
                txtIngresoA.setText(rs.getString(16));
                txtIngresoN.setText(rs.getString(17));
                txtTurno.setText(rs.getString(18));
                txtCedula.setText(rs.getString(19));
                txtTipoC.setText(rs.getString(20));
                txtEntidadC.setText(rs.getString(21));
                txtEspecialidad.setText(rs.getString(22));

            } catch (SQLException ex) {
                Logger.getLogger(EmpleadosViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
        @FXML private void btnRegresar (ActionEvent event) throws IOException{

        //Cambiamos la escena
        root = FXMLLoader.load(getClass().getResource("/view/LEmpleadosView.fxml"));
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
        String nombre = txtNombre.getText();
        String apellidoP=txtApellidoP.getText();
        String apellidoM=txtApellidoM.getText();
        String RFC=txtRFC.getText();
        String CURP=txtCURP.getText();
        String NSS=txtNSS.getText();
        String fecha=dpFecha.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String email=txtCorreo.getText();
        String activo=cmbActivo.getValue().toString();
        String pass=txtContrasena.getText();
        String repss=txtRepss.getText(); 
        String jornada =cmbJornada.getValue().toString();
        String anio = txtAnio.getText();
        String ultimoS=txtUltimoS.getText();
        String ingresoA=txtIngresoA.getText();
        String ingresoN=txtIngresoN.getText();
        String turno =txtTurno.getText();
        String cedula=txtCedula.getText();
        String tipoC=txtTipoC.getText();
        String entidadC=txtEntidadC.getText();
        String especialidad=txtEspecialidad.getText();
        
       if(!clave.equals("")&&!nombre.equals("")&&!apellidoP.equals("")&&!apellidoM.equals("")
       &&!RFC.equals("")&&!CURP.equals("")&&!NSS.equals("")&&!fecha.equals("")
       &&!email.equals("")&&!activo.equals("")&&!pass.equals("")&&!repss.equals("")
       &&!jornada.equals("")&&!anio.equals("")&&!ultimoS.equals("")&&!ingresoA.equals("")
       &&!ingresoN.equals("")&&!turno.equals("")&&!cedula.equals("")&&!tipoC.equals("")
       &&!entidadC.equals("")&&!especialidad.equals("")){
            classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            if(edicion){
               String sql= "UPDATE satin.empleados SET `clave`='"+clave+"',`nombre`='"+nombre+"',`apaterno`='"+apellidoP+"',`amaterno`='"+apellidoM+"',`rfc`='"+RFC+
               "',`curp`='"+CURP+"',`nss`='"+NSS+"',`fecha_ingreso`='"+fecha+"',`email`='"+email+"',`activo`='"+activo+"',`pass`='"+pass+"',`repss`='"+repss+
               "',`jornada`='"+jornada+"',`anio`='"+anio+"',`ultimo_sueldo`='"+ultimoS+"',`ingreso_acumulable`='"+ingresoA+"',`ingreso_no_acumulable`='"+ingresoN+"',`turno`='"+turno+"',`cedula`='"+cedula+
               "',`tipo_cedula`='"+tipoC+"',`entidad_cedula`='"+entidadC+"',`especialidad`='"+especialidad+"' WHERE `clave`='"+idEdicion+"'";
               System.out.println("Entró");
               System.out.println(sql);
                if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha actualizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LEmpleadosView.fxml"));
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
               String sql= "INSERT INTO satin.empleados (clave,nombre,apaterno,amaterno,rfc,curp,nss,fecha_ingreso,email,activo,pass,repss,jornada,anio,ultimo_sueldo,"+
               "ingreso_acumulable,ingreso_no_acumulable,turno,cedula,tipo_cedula,entidad_cedula,especialidad) VALUES('"+clave+"','"+nombre+"','"+apellidoP+"','"+apellidoM+"','"+
               RFC+"','"+CURP+"','"+NSS+"','"+fecha+"','"+email+"','"+activo+"','"+pass+"','"+repss+"','"+jornada+"','"+anio+"','"+ultimoS+"','"+ingresoA+"','"+ingresoN+"','"+turno+"','"+cedula+"','"+
               tipoC+"','"+entidadC+"','"+especialidad+"')";
            if(mysql.stmt(sql)){
                JOptionPane.showMessageDialog(null, "El registro se ha realizado exitosamente.", "Aviso", JOptionPane.PLAIN_MESSAGE);
                root = FXMLLoader.load(getClass().getResource("/view/LEmpleadosView.fxml"));
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
