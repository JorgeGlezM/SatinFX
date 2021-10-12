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
import classes.Faltas;
import classes.Horas;
import classes.Productos;
import classes.Validaciones;
import com.linuxense.javadbf.DBFReader;
import java.io.FileInputStream;
import java.math.BigDecimal;
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
    String RFCEmisor="";
    String productoActual="";
    //Strings para tabla empleado
    String eClave,eAPaterno,eAMaterno, eNombres,eRFC,eCURP,eNSS,eFecha;
    //Strings para tabla producto
    String pClave,pAnio,pMes,pFechaPago,pRenglones,pTotal;
    //Strings para tabla detalle
    String dClave, dNumEmp,dRFC,dBanco,dCuentaBancaria,dClaveP2,dCentroTrabajo,dCodigoPuesto,dClavePago,dContrato,dClaveContrato
            ,dDescripcion,dFechaInicial,dFechaFinal,dMovimiento,dPercepciones,dDeducciones,dFaltas,dDiaIncapacidad,dTipoIncapacidad,dImporteIncapacidad
            ,dDiasHorasDobles,dHorasDobles,dImporteHorasDobles,dDiasHorasTriples,dHorasTriples,dImporteHorasTriples,dClue,dSindicalizado,dConceptos, dID,
            dTipoR,dNoPuesto,dIndicadorMando,dSalarioDiario,dSecuencia, dUnidadResponsable,dInstrumentoPago,dHoras,dFonac,dDigito,dPagaduria,
            dControlSar,dTipoTrabajador,dNivel,dRango,dPorcentaje,dEstado,dMunicipio,dActividad,dProyecto,dPartida,dGfSf,dNombre2,dClaveP;
    String adicional;
    //Strings para tabla conceptos
    String cConcepto,cGrabado,cNoGrabado;
    //Strings para inserciones SQL
    String sqlEmpleado="", sqlProducto="",sqlDetalle="",sqlAdicional="",sqlConceptos="",sqlHoras="",sqlFaltas="";
    String preClave="";
    int c=0;
    BigDecimal totales=new BigDecimal(0);
    BigDecimal deducciones=new BigDecimal(0);
    Scanner sc;
    Scanner lineScanner;
    @FXML ChoiceBox cmbTipo;
    @FXML Label lblFile;
    List<Empleados> empleados=new ArrayList<Empleados>();
    List<DetalleNomina> detallesNomina=new ArrayList<DetalleNomina>();
    List<Adicional> adicionales=new ArrayList<Adicional>();
    List<Conceptos> conceptos=new ArrayList<Conceptos>();
    List<Horas> horas=new ArrayList<Horas>();
    List<Productos> productos=new ArrayList<Productos>();
    List<Faltas> faltas=new ArrayList<Faltas>();
    List<Validaciones> validaciones=new ArrayList<Validaciones>();



    Object[] rowObjects;
    DBFReader dbfreader;




    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTipo.getItems().add((Object)"txt");
        cmbTipo.getItems().add((Object)"dbf");
        cmbTipo.getItems().add((Object)"dat/tra");
        cmbTipo.setValue("dat/tra");

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

        String tipo=(String) cmbTipo.getValue();
        switch(tipo){
            case "txt":extraerEmpleadoTXT();extraerProductoTXT();insertsTXT(); break;
            case "dbf":extraerDatosDBF();extraerConceptosDBF();insertsDBF();break;
        }        
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
                JOptionPane.showMessageDialog(null, "Seleccione el archivo de conceptos a cargar");
                empleadoFile=fc.showOpenDialog(null);
                lblFile.setText(selectedFile.getAbsolutePath());
                lblFile.setWrapText(true);
            }
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
            generarValidacionesTXT();


        }
    }
    
    private void extraerHorasTXT() {
        horas.add(new Horas(dID,dClave,dDiasHorasDobles,dHorasDobles,dImporteHorasDobles,dDiasHorasTriples,dHorasTriples,dImporteHorasTriples));      
    }

    private void extraerFaltasTXT() {
    if(!dFaltas.equals("0")){
            faltas.add(new Faltas(dID,dClave,dFaltas,dDiaIncapacidad,dTipoIncapacidad));
    }
    }
    private void generarValidacionesTXT() {
        //dID,dClue,cConcepto,dNoPuesto
        validaciones.add(new Validaciones(dID,dClue,cConcepto,dCodigoPuesto));
        
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
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de Empleados", "Advertencia", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de detalles de nómina", "Advertencia", JOptionPane.WARNING_MESSAGE);
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
            }
            pstmtAdicionales.executeBatch();
            adicionales=new ArrayList<Adicional>();
                System.out.println("Adicionales insertados");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de Adicionales", "Advertencia", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de detalle de conceptos", "Advertencia", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de Horas", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            //Inserción de faltas 
            String insertFaltas="insert into incapacidades (id_detalle_nomina,producto,faltas,dia,tipo) values (?,?,?,?,?)";
            try{
                PreparedStatement pstmtFaltas = mysql.conn.prepareStatement(insertFaltas);
                for (Faltas falta : faltas) {
                    pstmtFaltas.setString(1, falta.getdID()); 
                    pstmtFaltas.setString(2, falta.getdClave());
                    pstmtFaltas.setString(3, falta.getdFaltas());
                    if(falta.getdDiaIncapacidad().equals("")){
                        pstmtFaltas.setString(4, "0");
                    }else{
                        pstmtFaltas.setString(4, falta.getdDiaIncapacidad());

                    }
                    pstmtFaltas.setString(5, falta.getdTipoIncapacidad());
                    pstmtFaltas.addBatch();
            }
            pstmtFaltas.executeBatch();
            faltas=new ArrayList<Faltas>();
                            System.out.println("Faltas insertadas");

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de Faltas", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
                        //Inserción de validaciones 
            String insertValidaciones="insert into tmpvalidardetalle (iddetalle,clue,puesto,concepto) values (?,?,?,?)";
            try{
                PreparedStatement pstmtValidaciones = mysql.conn.prepareStatement(insertValidaciones);
                for (Validaciones validacion : validaciones) {
                    pstmtValidaciones.setString(1, validacion.getdID()); 
                    pstmtValidaciones.setString(2, validacion.getdClue());
                    pstmtValidaciones.setString(3, validacion.getdCodigoPuesto());
                    pstmtValidaciones.setString(4, validacion.getcConcepto());

                    
                    pstmtValidaciones.addBatch();
            }
            pstmtValidaciones.executeBatch();
            faltas=new ArrayList<Faltas>();
                System.out.println("Validaciones insertadas");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de la tabla de validación", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            

                    
    }
    
        private void insertsDBF() {
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
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de Empleados", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
                        
                            
            //Insercion Producto
                            //productos.add(new Productos(pClave,pAnio,pMes,pFechaPago,pTotal));
            String insertProducto="INSERT INTO satin.producto_nomina (clave,anio,mes,fechapago,total)"+
               "VALUES(?,?,?,?,?)";
            try{
                
                PreparedStatement pstmtProductos = mysql.conn.prepareStatement(insertProducto);
                for (Productos producto : productos) {
                    pstmtProductos.setString(1, producto.getpClave()); 
                    pstmtProductos.setString(2, producto.getpAnio());
                    pstmtProductos.setString(3, producto.getpMes());
                    pstmtProductos.setString(4, producto.getpFechaPago());
                    pstmtProductos.setString(5, producto.getpTotal());
                    
                    pstmtProductos.addBatch();
            }
            pstmtProductos.executeBatch();
            productos=new ArrayList<Productos>();
            System.out.println("Productos insertados");

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de productos de nómina", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            //Insercion detalle_nomina
            String insertDetalleNomina="insert into detalle_nomina (id,producto,clave,tipor,clavep,centro_trabajo,puesto,contrato,clav,"+
            " clavepago,no_puesto,indicador_mando,descripcion,fechai,fechaf,movimiento,total1,total2,conceptos,"+
            " salariodiario,sindicato,rfc,seq,unidad,instrumento_pago,horas,fonac,digito_ver,pagaduria,control_sar,"+
            " tipo_trabajador,nivel,actividad,sellosat,banco,cuenta_bancaria,clue) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try{
                
                PreparedStatement pstmtDetalle = mysql.conn.prepareStatement(insertDetalleNomina);
                for (DetalleNomina detalleNomina : detallesNomina) {
                    pstmtDetalle.setString(1, detalleNomina.getdID()); 
                    pstmtDetalle.setString(2, detalleNomina.getdClave());
                    pstmtDetalle.setString(3, detalleNomina.getdNumEmp());
                    pstmtDetalle.setString(4, detalleNomina.getdTipoR());
                    pstmtDetalle.setString(5, detalleNomina.getdClaveP());
                    pstmtDetalle.setString(6, detalleNomina.getdCentroTrabajo());
                    pstmtDetalle.setString(7, detalleNomina.getdCodigoPuesto());
                    pstmtDetalle.setString(8, detalleNomina.getdContrato());
                    pstmtDetalle.setString(9, detalleNomina.getdClaveContrato());
                    pstmtDetalle.setString(10, detalleNomina.getdClavePago());
                    pstmtDetalle.setString(11, detalleNomina.getdNoPuesto());
                    pstmtDetalle.setString(12, detalleNomina.getdIndicadorMando());
                    pstmtDetalle.setString(13, detalleNomina.getdDescripcion());
                    pstmtDetalle.setString(14, detalleNomina.getdFechaInicial());
                    pstmtDetalle.setString(15, detalleNomina.getdFechaFinal());
                    pstmtDetalle.setString(16, detalleNomina.getdMovimiento());
                    pstmtDetalle.setString(17, detalleNomina.getdPercepciones());
                    pstmtDetalle.setString(18, detalleNomina.getdDeducciones());
                    pstmtDetalle.setString(19, detalleNomina.getdConceptos());
                    pstmtDetalle.setString(20, detalleNomina.getdSalarioDiario());
                    pstmtDetalle.setString(21, detalleNomina.getdSindicalizado());
                    pstmtDetalle.setString(22, detalleNomina.getdRFC());
                    pstmtDetalle.setString(23, detalleNomina.getdSecuencia());
                    pstmtDetalle.setString(24, detalleNomina.getdUnidadResponsable());
                    pstmtDetalle.setString(25, detalleNomina.getdInstrumentoPago());
                    pstmtDetalle.setString(26, detalleNomina.getdHoras());
                    pstmtDetalle.setString(27, detalleNomina.getdFonac());
                    pstmtDetalle.setString(28, detalleNomina.getdDigito());
                    pstmtDetalle.setString(29, detalleNomina.getdPagaduria());
                    pstmtDetalle.setString(30, detalleNomina.getdControlSar());
                    pstmtDetalle.setString(31, detalleNomina.getdTipoTrabajador());
                    pstmtDetalle.setString(32, detalleNomina.getdNivel());
                    pstmtDetalle.setString(33, detalleNomina.getdActividad());
                    pstmtDetalle.setString(34, detalleNomina.getdNombre2());
                    pstmtDetalle.setString(35, detalleNomina.getdBanco());
                    pstmtDetalle.setString(36, detalleNomina.getdCuentaBancaria());
                    pstmtDetalle.setString(37, detalleNomina.getdClue());

                    
                    pstmtDetalle.addBatch();
            }
            pstmtDetalle.executeBatch();
            detallesNomina=new ArrayList<DetalleNomina>();
            System.out.println("Detalles insertados");

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de detalles de nómina", "Advertencia", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de detalle de conceptos", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            
                        //Inserción de validaciones 
            String insertValidaciones="insert into tmpvalidardetalle (iddetalle,clue,puesto,concepto) values (?,?,?,?)";
            try{
                PreparedStatement pstmtValidaciones = mysql.conn.prepareStatement(insertValidaciones);
                for (Validaciones validacion : validaciones) {
                    pstmtValidaciones.setString(1, validacion.getdID()); 
                    pstmtValidaciones.setString(2, validacion.getdClue());
                    pstmtValidaciones.setString(3, validacion.getdCodigoPuesto());
                    pstmtValidaciones.setString(4, validacion.getcConcepto());

                    
                    pstmtValidaciones.addBatch();
            }
            pstmtValidaciones.executeBatch();
            faltas=new ArrayList<Faltas>();
                System.out.println("Validaciones insertadas");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de la tabla de validación", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            

                    
    }

    
    private void extraerDatosDBF() {
        try {
            dbfreader = new DBFReader(new FileInputStream(selectedFile));
            
                        //Ciclo de registros dbf
			while ((rowObjects = dbfreader.nextRecord()) != null) {
                            //Inicia extracción empleado
                            eClave=rowObjects[0].toString();
                            //Separamos la cadena del nombre
                            String nombre=rowObjects[3].toString();
                            String temp=nombre.substring(0,nombre.indexOf(" "));
                            nombre=nombre.substring(nombre.indexOf(" ")+1,nombre.length());
                            eAPaterno=temp;
                            if(temp.equalsIgnoreCase("de")||temp.equalsIgnoreCase("del")){
                                temp=nombre.substring(0,nombre.indexOf(" "));
                                eAPaterno=eAPaterno+" "+temp;
                                nombre=nombre.substring(nombre.indexOf(" ")+1,nombre.length());
                                if(temp.equalsIgnoreCase("el")||temp.equalsIgnoreCase("la")||temp.equalsIgnoreCase("las")||temp.equalsIgnoreCase("los")){
                                    temp=nombre.substring(0,nombre.indexOf(" "));
                                    eAPaterno=eAPaterno+" "+temp;
                                    nombre=nombre.substring(nombre.indexOf(" ")+1,nombre.length());
                                }
                            }
                            temp=nombre.substring(0,nombre.indexOf(" "));
                            nombre=nombre.substring(nombre.indexOf(" ")+1,nombre.length());
                            eAMaterno=temp;
                            if(temp.equalsIgnoreCase("de")||temp.equalsIgnoreCase("del")){
                                temp=nombre.substring(0,nombre.indexOf(" "));
                                eAMaterno=eAMaterno+" "+temp;
                                nombre=nombre.substring(nombre.indexOf(" ")+1,nombre.length());
                                if(temp.equalsIgnoreCase("el")||temp.equalsIgnoreCase("la")||temp.equalsIgnoreCase("las")||temp.equalsIgnoreCase("los")){
                                    temp=nombre.substring(0,nombre.indexOf(" "));
                                    eAMaterno=eAMaterno+" "+temp;
                                    nombre=nombre.substring(nombre.indexOf(" ")+1,nombre.length());
                                }
                            }
                            eNombres=nombre;
                            eRFC=rowObjects[1].toString();
                            eCURP=rowObjects[2].toString();
                            eNSS="0";
                            eFecha=rowObjects[39].toString();
                            empleados.add(new Empleados(eClave,eAPaterno,eAMaterno,eNombres,eRFC,eCURP,eNSS,eFecha));
                            //Termina extracción empleado
                            
                            
                            
                            //Inicia extracción producto nomina
                            //String pClave,pAnio,pMes,pFechaPago,pRenglones,pTotal;
                            if(preClave.equals("")){
                                //Iniciamos el primer producto
                                preClave=rowObjects[58].toString();
                                pAnio=rowObjects[49].toString();
                                if(RFCEmisor.equals("SSJ970331PM5")){
                                    pClave="E"+preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                                }else{
                                    pClave=preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                                }
                                pMes=rowObjects[48].toString();
                                pFechaPago=rowObjects[43].toString();
                                totales=new BigDecimal(rowObjects[53].toString());

                                deducciones=new BigDecimal(rowObjects[54].toString());
                                
                            }else if(rowObjects[58].toString().equals(preClave)){
                                //Sumamos totales al producto
                                totales=totales.add(new BigDecimal(rowObjects[53].toString()));
                                deducciones=deducciones.add(new BigDecimal(rowObjects[54].toString()));
                            }else if(!preClave.equals("")&&!rowObjects[58].toString().equals(preClave)){
                                //Concluimos el registro anterior y lo agregamos a la lista
                                //System.out.println("percepcion:" +totales);
                                //System.out.println("Deduccion:" +deducciones);
                                System.out.println("percepcion:" + totales.toString());
                                System.out.println("deducción:" + deducciones.toString());
                                pTotal=totales.subtract(deducciones).toString();
                                System.out.println(pTotal);
                                productos.add(new Productos(pClave,pAnio,pMes,pFechaPago,pTotal));
                                
                                //Iniciamos el siguiente registro
                                preClave=rowObjects[58].toString();
                                pAnio=rowObjects[49].toString();
                                pClave="E"+preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                                System.out.println(pClave);
                                pMes=rowObjects[48].toString();
                                pFechaPago=rowObjects[43].toString();
                                totales=new BigDecimal(rowObjects[53].toString());
                                deducciones=new BigDecimal(rowObjects[54].toString());
                            }
                            
                            
                            //Iniciamos extracción de detalle_nomina
                            
                            /*
                                'Insert DETALLE_NOMINA
    str = "insert into detalle_nomina (producto,clave,tipor,clavep,centro_trabajo,puesto,contrato,clav," & _
    " clavepago,no_puesto,indicador_mando,descripcion,fechai,fechaf,movimiento,total1,total2,conceptos," & _
    " salariodiario,sindicato,rfc,seq,unidad,letra,instrumento_pago,horas,fonac,digito_ver,pagaduria,control_sar," & _
    " tipo_trabajador,nivel,rango,porcentaje,edo,mun,actividad,proyecto,partida,gf_sf,sellosat,banco,cuenta_bancaria) " & _
    " values ('" & Trim(CFDIProducto) & "'," & CFDINumEmp & ",'" & CFDItipor & "','" & CFDIclavep & _
    "','" & CFDIcentro & "','" & Trim(CFDIPuesto) & "','" & CFDIcontrato & "','" & CFDIclavecon & "','" & CFDIotro & "','" & CFDINoPuesto & _
    "','" & CFDITipoMando & "','" & CFDIdescripcion & "','" & CFDIFechaI & "','" & CFDIFechaF & "','" & CFDIMovimiento & _
    "','" & CFDITotal1 & "','" & CFDITotal2 & "','" & CFDIConceptos & "','" & CFDITotal2 & "','" & _
    CFDSindicato & "','" & CFDRecepRFC & "','" & CFDISEQ & "','" & CFDUnidadResponsable & "','" & _
    LETRA & "','" & CFDIInstrumentoPago & "','" & CFDIHoras & "','" & CFDIFonac & "','" & _
    CFDIDigito & "','" & CFDIPagaduria & "','" & CFDIControlSar & "','" & CFDITipoTrabajador & _
    "','" & CFDINivel & "','" & CFDIRango & "','" & CFDIPorcentaje & "','" & CFDIEdo & "','" & CFDIMun & _
    "','" & CFDIActividad & "','" & CFDIProyecto & "','" & CFDIPartida & "','" & CFDIGF_SF & _
    "','" & CFDInombre2 & "','" & CFDIBanco & "','" & CFDICuentaBancaria & "')"
                            
                            */
                            

                            dClave=pClave;
                            dNumEmp=rowObjects[0].toString();
                            dTipoR="0";
                            dClaveP=rowObjects[7].toString();
                            dCentroTrabajo=rowObjects[26].toString();
                            dCodigoPuesto=rowObjects[22].toString();
                            dContrato="0";
                            dClaveContrato="0";
                            dClavePago=rowObjects[14].toString()+rowObjects[18].toString()+rowObjects[19].toString()+rowObjects[22].toString()+rowObjects[23].toString()+rowObjects[16].toString()+rowObjects[17].toString()+rowObjects[18].toString();
                            dNoPuesto=rowObjects[23].toString();
                            dIndicadorMando=rowObjects[33].toString();
                            dDescripcion="0";
                            dFechaInicial=rowObjects[44].toString();
                            dFechaFinal=rowObjects[45].toString();
                            dMovimiento=rowObjects[60].toString();
                            dPercepciones=rowObjects[53].toString();
                            dDeducciones=rowObjects[54].toString();
                            dConceptos=rowObjects[56].toString();
                            dSalarioDiario=dDeducciones;
                            dSindicalizado="NO";
                            dRFC=rowObjects[1].toString();
                            dSecuencia=rowObjects[59].toString();
                            dUnidadResponsable=rowObjects[14].toString();
                            dInstrumentoPago=rowObjects[52].toString();
                            dHoras=rowObjects[34].toString();
                            dFonac=rowObjects[66].toString();
                            dClue=rowObjects[68].toString();
                            dDigito=rowObjects[61].toString();
                            dPagaduria=rowObjects[28].toString();
                            dControlSar=rowObjects[4].toString();
                            dTipoTrabajador=rowObjects[36].toString();
                            dNivel=rowObjects[37].toString();
                            dActividad=rowObjects[19].toString();
                            char cuarto=rowObjects[58].toString().charAt(3);
                            if(dTipoTrabajador.equals("20")||RFCEmisor.equals("SSO960923M2A")&&cuarto=='P'){
                                dNombre2=rowObjects[3].toString();//Cargamos nombre del pensionado
                            }else{dNombre2="";}                            
                            dBanco=rowObjects[4].toString();
                            dCuentaBancaria=rowObjects[7].toString();                 
                            dID=dRFC+dClave+dMovimiento;
                            detallesNomina.add(new DetalleNomina(dClave,dNumEmp,dRFC,dBanco,dCuentaBancaria,dCentroTrabajo,dCodigoPuesto,dClavePago,dContrato,dClaveContrato,dDescripcion,dFechaInicial,dFechaFinal,dMovimiento,dDeducciones,dClue,dSindicalizado,dConceptos,dID,dTipoR,dControlSar,dTipoTrabajador,dNivel,dActividad,dIndicadorMando,dClaveP,dSalarioDiario,dSecuencia,dUnidadResponsable,dInstrumentoPago,dHoras,dFonac,dPagaduria,dDigito));
                            validaciones.add(new Validaciones(dID,dClue,"NO",dCodigoPuesto));

            }
                        //Agregamos el registro final de producto al salir del diclo ya que nunca entraría a la condición de ser clave distinta porque no hay más registros.
                            System.out.println("percepcion:" + totales.toString());
                            System.out.println("deducción:" + deducciones.toString());
                            pTotal=totales.subtract(deducciones).toString();
                            System.out.println(pTotal);
                            productos.add(new Productos(pClave,pAnio,pMes,pFechaPago,pTotal));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void extraerConceptosDBF() {
        try {
            dbfreader = new DBFReader(new FileInputStream(empleadoFile));
			while ((rowObjects = dbfreader.nextRecord()) != null) {
                            dRFC=rowObjects[0].toString();
                            preClave=rowObjects[11].toString();
                            if(RFCEmisor.equals("SSJ970331PM5")){
                                dClave="E"+preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                            }else{
                                dClave=preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                            }
                            dMovimiento=rowObjects[2].toString();
                            dID=dRFC+dClave+dMovimiento;
                            String c1,c2,c3;
                            c1=rowObjects[3].toString();
                            c2=rowObjects[4].toString();
                            c3=rowObjects[8].toString();             
                            //    If c1 = "3" Then c1 = "2"
                            if(c1.equals("1")){
                                c1="2";
                            }
                            cConcepto=c1+c2+c3;
                            //Ya que se agreguen los campos descomentar este codigo y quitar valores asignados
                            cGrabado="12";
                            cNoGrabado="23";
                            //cGrabado=rowObjects[13].toString();
                            //cNoGrabado=rowObjects[14].toString();
                            conceptos.add(new Conceptos(dID,cConcepto,cGrabado,cNoGrabado));
                            validaciones.add(new Validaciones(dID,"NO",cConcepto,"NO"));                            
			}
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    


}
