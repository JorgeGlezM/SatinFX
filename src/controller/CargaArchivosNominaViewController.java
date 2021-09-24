/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 * FXML Controller class
 *
 * @author Jorge
 */
public class CargaArchivosNominaViewController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    File selectedFile;
    File empleadoFile;
    String productoActual="";
    //Strings para tabla empleado
    String eClave,eAPaterno,eAMaterno, eNombres,eRFC,eCURP,eNSS,eFecha;
    //Strings para tabla producto
    String pClave,pAnio,pMes,pFechaPago,pRenglones,pTotal;
    //Strings para tabla detalle
    String dClave, dNumEmp,dRFC,dBanco,dCuentaBancaria,dClaveP2,dCentroTrabajo,dCodigoPuesto,dClavePago,dContrato,dClaveContrato
            ,dDescripcion,dFechaInicial,dFechaFinal,dMovimiento,dPercepciones,dDeducciones,dFaltas,dDiaIncapacidad,dTipoIncapacidad,dImporteIncapacidad
            ,dDiasHorasDobles,dHorasDobles,dImporteHorasDobles,dDiasHorasTriples,dHorasTriples,dImporteHorasTriples,dClue,dSindicalizado,dConceptos, dID;
    String adicional;
    //Strings para tabla conceptos
    String cConcepto,cGrabado,cNoGrabado;
    //Strings para inserciones SQL
    String sqlEmpleado="", sqlProducto="",sqlDetalle="",sqlAdicional="",sqlConceptos="",sqlHoras="",sqlFaltas="";
    Scanner sc;
    Scanner lineScanner;
    @FXML ChoiceBox cmbTipo;
    @FXML Label lblFile;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTipo.getItems().add((Object)"txt");
        cmbTipo.getItems().add((Object)"dbf");
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

        cargarTXT();
    }
    @FXML private void btnSeleccionar (ActionEvent event) throws IOException{

        //Cambiamos la escena
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = 
        new FileChooser.ExtensionFilter("Archivos de texto (*.TXT)", "*.TXT");
        fc.getExtensionFilters().add(extFilter);
        selectedFile = fc.showOpenDialog(null);
        if(selectedFile!=null){
            lblFile.setText(selectedFile.getAbsolutePath());
            lblFile.setWrapText(true);
            String rutaEMP=lblFile.getText().substring(0,lblFile.getText().lastIndexOf("TXT"))+"EMP";
            empleadoFile=new File(rutaEMP);
            if(!empleadoFile.exists()){
                selectedFile=null;
                JOptionPane.showMessageDialog(null, "El archivo EMP ligado al archivo seleccionado no existe. Verifique su existencia e intentelo denuevo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                lblFile.setText("Archivo incorrecto");
            }else{
            }
        }
    }
    public void cargarTXT(){
        extraerEmpleadoTXT();
 	extraerProducto();
        insertsTXT();
    }
    public void extraerEmpleadoTXT(){
        try {
            lineScanner = new Scanner(empleadoFile);
            lineScanner.useDelimiter("\\|");
            //    String eClave,eAPaterno,eAMaterno, eNombres,eRFC,eCURP,eNSS,eFecha;
            eClave=lineScanner.next();
            eAPaterno=lineScanner.next();
            eAMaterno=lineScanner.next();
            eNombres=lineScanner.next();
            eRFC=lineScanner.next();
            eCURP=lineScanner.next();
            eNSS=lineScanner.next();
            eFecha=lineScanner.next();
            sqlEmpleado="INSERT INTO satin.empleados (clave,nombre,apaterno,amaterno,rfc,curp,nss,fecha_ingreso)"+
               "VALUES('"+eClave+"','"+eNombres+"','"+eAPaterno+"','"+eAMaterno+"','"+
               eRFC+"','"+eCURP+"','"+eNSS+"','"+eFecha+"')";
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void extraerProducto(){
                    try {
      sc = new Scanner(selectedFile);

        String str = sc.nextLine();
        //Scanner con delimitador, producto_nomina
        lineScanner = new Scanner(str);
        //Tiene que incluir los slash para que reconozca el separador (caractér especial)
        lineScanner.useDelimiter("\\|");
        //pClave,pAnio,pMes,pFechaPago,pRenglones,pTotal
        pClave=lineScanner.next();
        pAnio=lineScanner.next();
        pMes=lineScanner.next();
        pFechaPago=lineScanner.next();
        pRenglones=lineScanner.next();
        pTotal=lineScanner.next();
        extraerDetalleTXT(Integer.valueOf(pRenglones));
        sqlProducto="INSERT INTO satin.producto_nomina (clave,anio,mes,fechapago,total)"+
               "VALUES('"+pClave+"','"+pAnio+"','"+pMes+"','"+pFechaPago+"','"+pTotal+"')";
      
    } catch (IOException  exp) {
      // TODO Auto-generated catch block
      exp.printStackTrace();
    }finally{
      if(sc != null)
        sc.close();
    }	 
    }
    public void extraerDetalleTXT(int max){
        int i=0;
        while(i<max){
            i++;
            String str=sc.nextLine();
            lineScanner = new Scanner(str);
            lineScanner.useDelimiter("\\|");
            /*String dClave, dNumEmp,dRFC,dBanco,dCuentaBancaria,dClaveP2,dCentroTrabajo,dCodigoPuesto,dClavePago,dContrato,dClaveContrato
            ,dDescripcion,dFechaInicial,dFechaFinal,dMovimiento,dPercepciones,dDeducciones,dFaltas,dDiaIncapacidad,dTipoIncapacidad,dImporteIncapacidad
            ,dDiasHorasDobles,dHorasDobles,dImporteHorasDobles,dDiasHorasTriples,dHorasTriples,dImporteHorasTriples,dClue,dSindicalizado;*/
            dClave=lineScanner.next();
            dNumEmp=lineScanner.next();
            dRFC=lineScanner.next();
            lineScanner.next();
            dBanco=lineScanner.next();
            dCuentaBancaria=lineScanner.next();
            dClaveP2=lineScanner.next();
            dCentroTrabajo=lineScanner.next();
            dCodigoPuesto=lineScanner.next();
            dClavePago=lineScanner.next();
            dContrato=lineScanner.next();
            dClaveContrato=lineScanner.next();
            dDescripcion=lineScanner.next();
            dFechaInicial=lineScanner.next();
            dFechaFinal=lineScanner.next();
            dMovimiento=lineScanner.next();
            dPercepciones=lineScanner.next();
            dDeducciones=lineScanner.next();
            dFaltas=lineScanner.next();
            dDiaIncapacidad=lineScanner.next();
            dTipoIncapacidad=lineScanner.next();
            dImporteIncapacidad=lineScanner.next();
            dDiasHorasDobles=lineScanner.next();
            dHorasDobles=lineScanner.next();
            dImporteHorasDobles=lineScanner.next();
            dDiasHorasTriples=lineScanner.next();
            dHorasTriples=lineScanner.next();
            dImporteHorasTriples=lineScanner.next();
            extraerAdicionalesTXT(lineScanner.next());
            dClue=lineScanner.next(); //Recortar el espacio en blanco
            dSindicalizado=lineScanner.next();
            dConceptos=lineScanner.next();
            dID=dRFC+dClave+dMovimiento;
            extraerConceptosTXT(Integer.valueOf(dConceptos));
            /*String dClave, dNumEmp,dRFC,dBanco,dCuentaBancaria,dClaveP2,dCentroTrabajo,dCodigoPuesto,dClavePago,dContrato,dClaveContrato
            ,dDescripcion,dFechaInicial,dFechaFinal,dMovimiento,dPercepciones,dDeducciones,dFaltas,dDiaIncapacidad,dTipoIncapacidad,dImporteIncapacidad
            ,dDiasHorasDobles,dHorasDobles,dImporteHorasDobles,dDiasHorasTriples,dHorasTriples,dImporteHorasTriples,dClue,dSindicalizado;*/
            
            //Falta checar instrumento pago
            sqlDetalle=sqlDetalle+"insert into satin.detalle_nomina (id,producto,clave,centro_trabajo,puesto,clavepago,contrato,"
            +"fechai,fechaf,movimiento,total1,total2,conceptos,sindicato,rfc,cuenta_bancaria,banco,clue,instrumento_pago)"+
            "VALUES('"+dID+"','"+dClave+"','"+dNumEmp+"','"+dCentroTrabajo+"','"+dCodigoPuesto+"','"+dClavePago+"','"+dClaveContrato+"','"+
            dFechaInicial+"','"+dFechaFinal+"','"+dMovimiento+"','"+dPercepciones+"','"+dDeducciones+"','"+dConceptos+"','"+dSindicalizado+"','"+dRFC+"','"+dCuentaBancaria+"','"+dBanco+"','"+dClue+"','"+dBanco+"');"+"\n";
            extraerHorasTXT();
            extraerFaltasTXT();
            
            
            
        }
    }
    public void extraerAdicionalesTXT(String a){
        Scanner aScanner = new Scanner(a);
        aScanner.useDelimiter(";");
        while(aScanner.hasNext()){
            adicional=aScanner.next();
            sqlAdicional=sqlAdicional+"insert into satin.adicional (id_detalle_nomina,texto)"+
            "VALUES('"+dID+"','"+adicional+"');"+"\n";
            //No funciona actualmente. Al hacer inserts masivos no puedo obtener el valor id_detalle_nomina al momento. Checar opciones.

        }
        
    }

    public void extraerConceptosTXT(int max){
        int i=0;
        //String cConcepto,cGrabado,cNoGrabado;
        while(i<max){
            Scanner aScanner = new Scanner(lineScanner.next());
            aScanner.useDelimiter(";");
            cConcepto=aScanner.next();
            cGrabado=aScanner.next();
            cNoGrabado=aScanner.next();
            i++;
            sqlConceptos=sqlConceptos+"insert into satin.detalle_conceptos (id_detalle_nomina,id_concepto,importe,importe_ng)"+
            "VALUES('"+dID+"','"+cConcepto+"','"+cGrabado+"','"+cNoGrabado+"');"+"\n";

        }
    }

    private void insertsTXT() {
            classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            try{
                //String sqlEmpleado="", sqlProducto="",sqlDetalle="",sqlAdicional="",sqlConceptos="",sqlHoras="",sqlFaltas="";
                //mysql.stmt(sqlEmpleado);
                mysql.stmt(sqlDetalle);
                JOptionPane.showMessageDialog(null, "Los registros de los archivos se han insertado correctamente");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
    }
/*
    private void extraerClueTXT() {
        //No existe la tabla ya
        sqlClue=sqlClue+"insert into clues (id_detalle_nomina,texto,producto) values"+
        "VALUES('"+dID+"','"+dClue+"','"+dClave+"');"+"\n";
    }*/

    private void extraerHorasTXT() {
        sqlHoras=sqlHoras+"insert into horas_extras (id_detalle_nomina,producto,diad,horasd,imported,diat,horast,importet) values "+
        "('"+dID+"','"+dClave+"','"+dDiasHorasDobles+"','"+dHorasDobles+"','"+dImporteHorasDobles+"','"+dDiasHorasTriples+"','"+dDiasHorasTriples+"','"+dImporteHorasTriples+"');"+"\n";    
    }

    private void extraerFaltasTXT() {
        sqlHoras=sqlHoras+"insert into incapacidades (id_detalle_nomina,producto,faltas,dia,tipo) values "+
        "('"+dID+"','"+dClave+"','"+dFaltas+"','"+dHorasDobles+"','"+dImporteHorasDobles+"','"+dDiasHorasTriples+"','"+dDiasHorasTriples+"','"+dImporteHorasTriples+"');"+"\n";    
    }
}
