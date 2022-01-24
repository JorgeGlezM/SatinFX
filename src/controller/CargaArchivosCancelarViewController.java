package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import classes.Adicional;
import classes.CentrosTrabajo;
import classes.Clues;
import classes.Conceptos;
import classes.DetalleConceptos;
import classes.DetalleNomina;
import classes.DetalleTimbrable;
import classes.Empleados;
import classes.Faltas;
import classes.Horas;
import classes.Productos;
import classes.Timbrar;
import com.linuxense.javadbf.DBFReader;
import static controller.DetallesCanceladoViewController.idProducto;
import static controller.DetallesCanceladoViewController.totalR;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * FXML Controller class
 *
 * @author Jorge
 */
public class CargaArchivosCancelarViewController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    File selectedFile;
    Scanner sc;
    Scanner lineScanner;
    @FXML ChoiceBox cmbTipo;
    @FXML Label lblFile;
    List<String> list = new ArrayList<String>();
    String[] uuid ;

    
   




    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTipo.getItems().add((Object)"Detallado");
        cmbTipo.getItems().add((Object)"UUID");
        cmbTipo.setValue("Detallado");

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
    @FXML private void btnCargar (ActionEvent event) throws IOException{

    }
    @FXML private void btnSeleccionar (ActionEvent event) throws IOException{
        FileChooser fc = new FileChooser();
        String tipo=(String) cmbTipo.getValue();
        FileChooser.ExtensionFilter extFilter;
            
            extFilter = 
            new FileChooser.ExtensionFilter("Archivos de texto (*.TXT)", "*.TXT");
            fc.getExtensionFilters().add(extFilter);
            selectedFile = fc.showOpenDialog(null);
            if(selectedFile!=null){
                lblFile.setText(selectedFile.getAbsolutePath());
                lblFile.setWrapText(true);
                String rt=lblFile.getText();

                }
    }
    
    

    public void extraerDetalle(){
        try {
            sc = new Scanner(selectedFile);
            while(sc.hasNextLine()){
            lineScanner = new Scanner(sc.nextLine());
            lineScanner.useDelimiter("\\|");
            //    String eClave,eAPaterno,eAMaterno, eNombres,eRFC,eCURP,eNSS,eFecha;
            lineScanner.next();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaArchivosCancelarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML private void btnCancelar(ActionEvent event) throws IOException, Exception{

        Timbrar t=new Timbrar();
        String tipo=(String) cmbTipo.getValue();
        switch(tipo){
            case "UUID":
                try {
                    sc = new Scanner(selectedFile);
                    while(sc.hasNextLine()){
                        list.add(sc.next());
                    }
                    uuid = list.toArray(new String[0]);


                    } catch (FileNotFoundException ex) {
                    Logger.getLogger(CargaArchivosCancelarViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                String motivo=JOptionPane.showInputDialog("Ingrese el motivo de la cancelación:");

                try{
                    t.cancelar(uuid);
                        classes.MySQL mysql=new classes.MySQL();
                mysql.conectar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                String fechaC=sdf.format(ts);
                String updateC="UPDATE `satin`.`detalle_nomina` SET `fechacancelacion` = ?, `motivo_can` = ? WHERE (`uuid` = ?);";
                String insertH="INSERT INTO `satin`.`detalle_cancelados` (`id_detalle_nomina`, `fechacancelacion`, `uuid`, `xml`,`motivo`) SELECT dn.id,?,?, x.xml,? from xml x,detalle_nomina dn where x.id_detalle=dn.id and dn.uuid=?";
                PreparedStatement pstmtCancelar=mysql.conn.prepareStatement(updateC);
                PreparedStatement pstmtHistorico=mysql.conn.prepareStatement(insertH);
                System.out.println("uuid: "+uuid[0]);
                for(String dt : uuid) {
                    pstmtCancelar.setString(1, fechaC);
                    pstmtCancelar.setString(2, motivo);
                    pstmtCancelar.setString(3, dt);
                    pstmtCancelar.addBatch();
                    pstmtHistorico.setString(1, fechaC);
                    pstmtHistorico.setString(2, dt);
                    pstmtHistorico.setString(3, motivo);
                    pstmtHistorico.setString(4, dt);
                    pstmtHistorico.addBatch();
                    System.out.println(pstmtHistorico.toString());
                }
                pstmtCancelar.executeBatch();
                pstmtHistorico.executeBatch();


                JOptionPane.showMessageDialog(null,"Los detalles de nómina se ha cancelado exitosamente.");
                }catch(Exception e){
                JOptionPane.showMessageDialog(null,"Los detales de nómina no han podido ser cancelados. Revise su conexión a internet e intentelo de nuevo.");

                }

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
        break;
        case "Detallado":
            classes.MySQL mysql=new classes.MySQL();
            mysql.conectar();
            String updateC="UPDATE `satin`.`detalle_nomina` SET `fechacancelacion` = ?, `motivo_can` = ? WHERE (`uuid` = ?);";
                String insertH="INSERT INTO `satin`.`detalle_cancelados` (`id_detalle_nomina`, `fechacancelacion`, `uuid`, `xml`,`motivo`,`remesa`) SELECT dn.id,?,?, x.xml,?,? from xml x,detalle_nomina dn where x.id_detalle=dn.id and dn.uuid=?";
            PreparedStatement pstmtCancelar=mysql.conn.prepareStatement(updateC);
            PreparedStatement pstmtHistorico=mysql.conn.prepareStatement(insertH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String fechaC=sdf.format(ts);
            try {
                sc = new Scanner(selectedFile);
                while(sc.hasNextLine()){
                    lineScanner = new Scanner(sc.nextLine());
                    lineScanner.useDelimiter("\\,");
                    String uuidtemp=lineScanner.next();
                    motivo=lineScanner.next();
                    String remesa=lineScanner.next();
                    list.add(uuidtemp);
                    pstmtCancelar.setString(1, fechaC);
                    pstmtCancelar.setString(2, motivo);
                    pstmtCancelar.setString(3, uuidtemp);
                    pstmtCancelar.addBatch();
                    pstmtHistorico.setString(1, fechaC);
                    pstmtHistorico.setString(2, uuidtemp);
                    pstmtHistorico.setString(3, motivo);
                    pstmtHistorico.setString(4, remesa);
                    pstmtHistorico.setString(5, uuidtemp);
                    pstmtHistorico.addBatch();
                    System.out.println(pstmtHistorico.toString());
                    }
                uuid = list.toArray(new String[0]);


            /*sqlEmpleado="INSERT INTO satin.empleados (clave,nombre,apaterno,amaterno,rfc,curp,nss,fecha_ingreso)"+
               "VALUES('"+eClave+"','"+eNombres+"','"+eAPaterno+"','"+eAMaterno+"','"+
               eRFC+"','"+eCURP+"','"+eNSS+"','"+eFecha+"')";*/
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
            try{
                t.cancelar(uuid);
                pstmtCancelar.executeBatch();
                pstmtHistorico.executeBatch();


                JOptionPane.showMessageDialog(null,"Los detalles de nómina se ha cancelado exitosamente.");
            }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Los detales de nómina no han podido ser cancelados. Revise su conexión a internet e intentelo de nuevo.");

            }
        break;

        }
        

    }

    


}
