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
import classes.Adicional;
import classes.Conceptos;
import classes.DetalleConceptos;
import classes.DetalleNomina;
import classes.Empleados;
import classes.Horas;
import com.linuxense.javadbf.DBFReader;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

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
    List<Empleados> empleados=new ArrayList<Empleados>();
    List<DetalleNomina> detallesNomina=new ArrayList<DetalleNomina>();
    List<Adicional> adicionales=new ArrayList<Adicional>();
    List<Conceptos> conceptos=new ArrayList<Conceptos>();
    List<Horas> horas=new ArrayList<Horas>();
    Object[] rowObjects;





    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTipo.getItems().add((Object)"txt");
        cmbTipo.getItems().add((Object)"dbf");
        cmbTipo.getItems().add((Object)"dat");

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
        FileChooser fc = new FileChooser();
        String tipo=(String) cmbTipo.getValue();
        FileChooser.ExtensionFilter extFilter;
        switch(tipo){
            
            case "txt":
            extFilter = 
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
                }

            }break;
            
            case "dbf":
            extFilter = 
            new FileChooser.ExtensionFilter("Archivos dbf (*.dbf)", "*.dbf");
            fc.getExtensionFilters().add(extFilter);
            JOptionPane.showMessageDialog(null, "Seleccione el archivo de nómina a cargar");
            selectedFile = fc.showOpenDialog(null);
            if(selectedFile!=null){
                JOptionPane.showMessageDialog(null, "Seleccione el archivo de empleados a cargar");
                empleadoFile=fc.showOpenDialog(null);
                lblFile.setText(selectedFile.getAbsolutePath());
                lblFile.setWrapText(true);
            }break;
        

        }
    }
    public void cargarTXT(){
        String tipo=(String) cmbTipo.getValue();
        switch(tipo){
            case "txt":
            extraerEmpleadoTXT();
            extraerProductoTXT();
            insertsTXT(); break;    
            
            case "dbf":
            extraerEmpleadoDBF();
            break;
        }

    }
    public void extraerEmpleadoTXT(){
        try {
            sc = new Scanner(empleadoFile);
            while(sc.hasNextLine()){
            lineScanner = new Scanner(sc.nextLine());
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
            empleados.add(new Empleados(eClave,eAPaterno,eAMaterno,eNombres,eRFC,eCURP,eNSS,eFecha));
            }

            /*sqlEmpleado="INSERT INTO satin.empleados (clave,nombre,apaterno,amaterno,rfc,curp,nss,fecha_ingreso)"+
               "VALUES('"+eClave+"','"+eNombres+"','"+eAPaterno+"','"+eAMaterno+"','"+
               eRFC+"','"+eCURP+"','"+eNSS+"','"+eFecha+"')";*/
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void extraerProductoTXT(){
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
            String tmpAdicionales=lineScanner.next();
            dClue=lineScanner.next(); //Recortar el espacio en blanco
            dClue=dClue.replaceAll("\\s", "");
            dSindicalizado=lineScanner.next();
            dConceptos=lineScanner.next();
            dID=dRFC+dClave+dMovimiento;
            extraerAdicionalesTXT(tmpAdicionales);
            extraerConceptosTXT(Integer.valueOf(dConceptos));
            /*String dClave, dNumEmp,dRFC,dBanco,dCuentaBancaria,dClaveP2,dCentroTrabajo,dCodigoPuesto,dClavePago,dContrato,dClaveContrato
            ,dDescripcion,dFechaInicial,dFechaFinal,dMovimiento,dPercepciones,dDeducciones,dFaltas,dDiaIncapacidad,dTipoIncapacidad,dImporteIncapacidad
            ,dDiasHorasDobles,dHorasDobles,dImporteHorasDobles,dDiasHorasTriples,dHorasTriples,dImporteHorasTriples,dClue,dSindicalizado;*/
            detallesNomina.add(new DetalleNomina( dClave,  dNumEmp,  dRFC,  dBanco,  dCuentaBancaria,  dClaveP2,  dCentroTrabajo,  dCodigoPuesto,  dClavePago,  dContrato,  dClaveContrato, dDescripcion,  dFechaInicial,  dFechaFinal,  dMovimiento,  dPercepciones,  dDeducciones,  dFaltas,  dDiaIncapacidad,  dTipoIncapacidad,  dImporteIncapacidad,  dDiasHorasDobles,  dHorasDobles,  dImporteHorasDobles,  dDiasHorasTriples,  dHorasTriples,  dImporteHorasTriples,  dClue,  dSindicalizado,  dConceptos,  dID));

            /*sqlDetalle=sqlDetalle+"insert into satin.detalle_nomina (id,producto,clave,centro_trabajo,puesto,clavepago,contrato,"
            +"fechai,fechaf,movimiento,total1,total2,conceptos,sindicato,rfc,cuenta_bancaria,banco,clue,instrumento_pago)"+
            "VALUES('"+dID+"','"+dClave+"','"+dNumEmp+"','"+dCentroTrabajo+"','"+dCodigoPuesto+"','"+dClavePago+"','"+dClaveContrato+"','"+
            dFechaInicial+"','"+dFechaFinal+"','"+dMovimiento+"','"+dPercepciones+"','"+dDeducciones+"','"+dConceptos+"','"+dSindicalizado+"','"+dRFC+"','"+dCuentaBancaria+"','"+dBanco+"','"+dClue+"','"+dBanco+"');"+"\n";
            */
            extraerHorasTXT();
            extraerFaltasTXT();
            
            
            
        }
    }
    public void extraerAdicionalesTXT(String a){
        Scanner aScanner = new Scanner(a);
        aScanner.useDelimiter(";");
        while(aScanner.hasNext()){
            adicional=aScanner.next();
            adicionales.add(new Adicional(dID,adicional));
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
            conceptos.add(new Conceptos(dID,cConcepto,cGrabado,cNoGrabado));

        }
    }
    
    private void extraerHorasTXT() {
        horas.add(new Horas(dID,dClave,dDiasHorasDobles,dHorasDobles,dImporteHorasDobles,dDiasHorasTriples,dHorasTriples,dImporteHorasTriples));      
    }

    private void extraerFaltasTXT() {
        sqlHoras=sqlHoras+"insert into incapacidades (id_detalle_nomina,producto,faltas,dia,tipo) values "+
        "('"+dID+"','"+dClave+"','"+dFaltas+"','"+dHorasDobles+"','"+dImporteHorasDobles+"','"+dDiasHorasTriples+"','"+dDiasHorasTriples+"','"+dImporteHorasTriples+"');"+"\n";    
    }

    private void insertsTXT() {
            classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            
            //Inserción empleados
            String insertEmpleado="INSERT INTO satin.empleados (clave,nombre,apaterno,amaterno,rfc,curp,nss,fecha_ingreso) "+
            "VALUES(?,?,?,?,?,?,?,?)";
            try{
                PreparedStatement pstmtEmpleado = mysql.conn.prepareStatement(insertEmpleado);
                for (Empleados empleado : empleados) {
                    pstmtEmpleado.setString(1, empleado.getClave()); 
                    pstmtEmpleado.setString(2, empleado.getNombres());
                    pstmtEmpleado.setString(3, empleado.getaPaterno());
                    pstmtEmpleado.setString(4, empleado.getaMaterno());
                    pstmtEmpleado.setString(5, empleado.getRFC());
                    pstmtEmpleado.setString(6, empleado.getCURP());
                    pstmtEmpleado.setString(7, empleado.getNSS());
                    pstmtEmpleado.setString(8, empleado.getFecha());


                    pstmtEmpleado.addBatch();
            }
            pstmtEmpleado.executeBatch();
            empleados=new ArrayList<Empleados>();
                System.out.println("Empleados insertados");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
                        
                        
            //Insercion Producto
            mysql.stmt(sqlProducto);
            
            //Insercion detalle_nomina
            String insertDetalleNomina="insert into satin.detalle_nomina (id,producto,clave,centro_trabajo,puesto,clavepago,contrato,"
            +"fechai,fechaf,movimiento,total1,total2,conceptos,sindicato,rfc,cuenta_bancaria,banco,clue,instrumento_pago)"+
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try{
                PreparedStatement pstmtDetalle = mysql.conn.prepareStatement(insertDetalleNomina);
                for (DetalleNomina detalleNomina : detallesNomina) {
                    pstmtDetalle.setString(1, detalleNomina.getdID()); 
                    pstmtDetalle.setString(2, detalleNomina.getdClave());
                    pstmtDetalle.setString(3, detalleNomina.getdNumEmp());
                    pstmtDetalle.setString(4, detalleNomina.getdCentroTrabajo());
                    pstmtDetalle.setString(5, detalleNomina.getdCodigoPuesto());
                    pstmtDetalle.setString(6, detalleNomina.getdClavePago());
                    pstmtDetalle.setString(7, detalleNomina.getdClaveContrato());
                    pstmtDetalle.setString(8, detalleNomina.getdFechaInicial());
                    pstmtDetalle.setString(9, detalleNomina.getdFechaFinal());
                    pstmtDetalle.setString(10, detalleNomina.getdMovimiento());
                    pstmtDetalle.setString(11, detalleNomina.getdPercepciones());
                    pstmtDetalle.setString(12, detalleNomina.getdDeducciones());
                    pstmtDetalle.setString(13, detalleNomina.getdConceptos());
                    pstmtDetalle.setString(14, detalleNomina.getdSindicalizado());
                    pstmtDetalle.setString(15, detalleNomina.getdRFC());
                    pstmtDetalle.setString(16, detalleNomina.getdCuentaBancaria());
                    pstmtDetalle.setString(17, detalleNomina.getdBanco());
                    pstmtDetalle.setString(18, detalleNomina.getdClue());
                    pstmtDetalle.setString(19, detalleNomina.getdBanco());
                    pstmtDetalle.addBatch();
            }
            pstmtDetalle.executeBatch();
            detallesNomina=new ArrayList<DetalleNomina>();
            System.out.println("Detalles insertados");

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            //Inserción de adicionales
            String insertAdicionales="insert into satin.adicional (id_detalle_nomina,texto) VALUES (?,?)";
            try{
                PreparedStatement pstmtAdicionales = mysql.conn.prepareStatement(insertAdicionales);
                for (Adicional adicional : adicionales) {
                    pstmtAdicionales.setString(1, adicional.getdID()); 
                    pstmtAdicionales.setString(2, adicional.getAdicional());
                    pstmtAdicionales.addBatch();
                    System.out.println(adicional.getdID());
            }
            //pstmtAdicionales.executeBatch();
            adicionales=new ArrayList<Adicional>();
                System.out.println("Adicionales insertados");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            //Inserción de conceptos
            String insertConceptos="insert into satin.detalle_conceptos (id_detalle_nomina,id_concepto,importe,importe_ng) VALUES(?,?,?,?)";
            try{
                PreparedStatement pstmtConceptos = mysql.conn.prepareStatement(insertConceptos);
                for (Conceptos concepto : conceptos) {
                    pstmtConceptos.setString(1, concepto.getdID()); 
                    pstmtConceptos.setString(2, concepto.getcConcepto());
                    pstmtConceptos.setString(3, concepto.getcGrabado());
                    pstmtConceptos.setString(4, concepto.getcNoGrabado());
                    pstmtConceptos.addBatch();
            }
            pstmtConceptos.executeBatch();
            conceptos=new ArrayList<Conceptos>();
                System.out.println("Conceptos insertados");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            
            
            
            //Inserción de horas
            String insertHoras="insert into horas_extras (id_detalle_nomina,producto,diad,horasd,imported,diat,horast,importet) values(?,?,?,?,?,?,?,?) ";
            try{
                PreparedStatement pstmtHoras = mysql.conn.prepareStatement(insertHoras);
                for (Horas hora : horas) {
                    pstmtHoras.setString(1, hora.getdID()); 
                    pstmtHoras.setString(2, hora.getdClave());
                    pstmtHoras.setString(3, hora.getdDiasHorasDobles());
                    pstmtHoras.setString(4, hora.getdHorasDobles());
                    pstmtHoras.setString(5, hora.getdImporteHorasDobles());
                    pstmtHoras.setString(6, hora.getdDiasHorasTriples());
                    pstmtHoras.setString(7, hora.getdHorasTriples());
                    pstmtHoras.setString(8, hora.getdImporteHorasTriples());
                    pstmtHoras.addBatch();
            }
            pstmtHoras.executeBatch();
            horas=new ArrayList<Horas>();
                System.out.println("Horas insertadas");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            

                    
    }

    private void extraerEmpleadoDBF() {
        try {
            DBFReader reader = new DBFReader(new FileInputStream(empleadoFile));
            int numberOfFields = reader.getFieldCount();

			while ((rowObjects = reader.nextRecord()) != null) {
                            eRFC=rowObjects[0].toString();
                            System.out.println(eRFC);
			}
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


}
