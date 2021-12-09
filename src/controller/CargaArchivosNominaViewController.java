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
import classes.Empleados;
import classes.Faltas;
import classes.Horas;
import classes.Productos;
import com.linuxense.javadbf.DBFReader;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

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
    File selectedFile2;
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
            dControlSar,dTipoTrabajador,dNivel,dRango,dPorcentaje,dEstado,dMunicipio,dActividad,dProyecto,dPartida,dGfSf,dNombre2,dClaveP,dDescripcionCentro;
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
    List<Clues> clues=new ArrayList<Clues>();
    List<CentrosTrabajo> centros=new ArrayList<CentrosTrabajo>();
    ArrayList<String> rfcs = new ArrayList<String>();
    int countRFC;





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
        empleados=new ArrayList<Empleados>();
        detallesNomina=new ArrayList<DetalleNomina>();
        adicionales=new ArrayList<Adicional>();
        conceptos=new ArrayList<Conceptos>();
        horas=new ArrayList<Horas>();
        productos=new ArrayList<Productos>();
        faltas=new ArrayList<Faltas>();
        clues=new ArrayList<Clues>();
        centros=new ArrayList<CentrosTrabajo>();
        String tipo=(String) cmbTipo.getValue();
        switch(tipo){
            case "txt":extraerEmpleadoTXT();extraerProductoTXT();insertsTXT();break;
            case "dbf":extraerDatosDBF();extraerConceptosDBF();insertsDBF();break;
            case "dat/tra":extraerDatosTRA();extraerDatosDAT();insertsDAT();break;
        }       
        fillClues();
        validaciones();
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
                selectedFile2=new File(rutaEMP);
                if(!selectedFile2.exists()){
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
                selectedFile2=fc.showOpenDialog(null);
                lblFile.setText(selectedFile.getAbsolutePath());
                lblFile.setWrapText(true);
            }
            break;
            
            case "dat/tra":
                
            extFilter = 
            new FileChooser.ExtensionFilter("Archivos DAT (*.dat)", "*.dat");
            fc.getExtensionFilters().add(extFilter);
            JOptionPane.showMessageDialog(null, "Seleccione el archivo .dat a cargar");
            selectedFile = fc.showOpenDialog(null);
            if(selectedFile!=null){
                fc = new FileChooser();
                extFilter = 
                new FileChooser.ExtensionFilter("Archivos TRA (*.tra)", "*.tra");
                fc.getExtensionFilters().add(extFilter);
                JOptionPane.showMessageDialog(null, "Seleccione el archivo .tra a cargar");
                selectedFile2=fc.showOpenDialog(null);
                lblFile.setText(selectedFile.getAbsolutePath());
                lblFile.setWrapText(true);
            }
                
            break;
        

        }
    }
    
    
    public void extraerEmpleadoTXT(){
        try {
            sc = new Scanner(selectedFile2);
            while(sc.hasNextLine()){
            lineScanner = new Scanner(sc.nextLine());
            lineScanner.useDelimiter("\\|");
            //    String eClave,eAPaterno,eAMaterno, eNombres,eRFC,eCURP,eNSS,eFecha;
            eClave=lineScanner.next();
            eAPaterno=lineScanner.next();
            eAMaterno=lineScanner.next();
            eNombres=lineScanner.next();
            System.out.println(eNombres);
            eRFC=lineScanner.next();
            eCURP=lineScanner.next();
            eNSS=lineScanner.next();
            eFecha=lineScanner.next();  
            rfcs.add(eRFC);
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
        countRFC=0;
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

            dClave=lineScanner.next();
            dNumEmp=lineScanner.next();
            //dRFC=lineScanner.next(); Esta es la forma correcta, el listado es temporal en lo que corrigen los archivos
            dRFC=rfcs.get(countRFC);
            countRFC++;
            lineScanner.next();
            dBanco=lineScanner.next();
            String tempCuenta=lineScanner.next();
            tempCuenta=tempCuenta.replaceFirst("^0+(?!$)", "");
            dCuentaBancaria=tempCuenta;
            dClaveP2=lineScanner.next();
            dCentroTrabajo=lineScanner.next();
            dCodigoPuesto=lineScanner.next();
            dCodigoPuesto=dCodigoPuesto.replaceAll("\\s", "");
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
            if(dClue.equals("")){
                dClue="CLUE0000000";
            }
            dSindicalizado=lineScanner.next();
            dConceptos=lineScanner.next();
            dID=dClave+i;
            extraerAdicionalesTXT(tmpAdicionales);
            extraerConceptosTXT(Integer.valueOf(dConceptos));
            /*String dClave, dNumEmp,dRFC,dBanco,dCuentaBancaria,dClaveP2,dCentroTrabajo,dCodigoPuesto,dClavePago,dContrato,dClaveContrato
            ,dDescripcion,dFechaInicial,dFechaFinal,dMovimiento,dPercepciones,dDeducciones,dFaltas,dDiaIncapacidad,dTipoIncapacidad,dImporteIncapacidad
            ,dDiasHorasDobles,dHorasDobles,dImporteHorasDobles,dDiasHorasTriples,dHorasTriples,dImporteHorasTriples,dClue,dSindicalizado;*/
            detallesNomina.add(new DetalleNomina( dClave,  dNumEmp,  dRFC,  dBanco,  dCuentaBancaria,  dClaveP2,  dCentroTrabajo,  dCodigoPuesto,  dClavePago,  dContrato,  dClaveContrato, dDescripcion,  dFechaInicial,  dFechaFinal,  dMovimiento,  dPercepciones,  dDeducciones,  dFaltas,  dDiaIncapacidad,  dTipoIncapacidad,  dImporteIncapacidad,  dDiasHorasDobles,  dHorasDobles,  dImporteHorasDobles,  dDiasHorasTriples,  dHorasTriples,  dImporteHorasTriples,  dClue,  dSindicalizado,  dConceptos,  dID));
            centros.add(new CentrosTrabajo(dCentroTrabajo,""));
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
    if(!dFaltas.equals("0")){
            faltas.add(new Faltas(dID,dClave,dFaltas,dDiaIncapacidad,dTipoIncapacidad));
    }
    }


    private void insertsTXT() {
            classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            
            //Inserción empleados
            String insertEmpleado="INSERT INTO satin.empleados (clave,nombre,apaterno,amaterno,rfc,curp,nss,fecha_ingreso) "+
            "VALUES(?,?,?,?,?,?,?,?) on duplicate key UPDATE nombre=?,apaterno=?,amaterno=?,rfc=?,curp=?,nss=?,fecha_ingreso=?";
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
                    pstmtEmpleado.setString(9, empleado.getNombres());
                    pstmtEmpleado.setString(10, empleado.getaPaterno());
                    pstmtEmpleado.setString(11, empleado.getaMaterno());
                    pstmtEmpleado.setString(12, empleado.getRFC());
                    pstmtEmpleado.setString(13, empleado.getCURP());
                    pstmtEmpleado.setString(14, empleado.getNSS());
                    pstmtEmpleado.setString(15, empleado.getFecha());
                    


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
                    pstmtDetalle.setString(7, detalleNomina.getdContrato());
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
                    if(!falta.getdFaltas().equals("")){
                        pstmtFaltas.addBatch();
                    }
            }
            pstmtFaltas.executeBatch();
            faltas=new ArrayList<Faltas>();
                            System.out.println("Faltas insertadas");

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de Faltas", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            //Inserción de centros inexistentes 
            String insertCentros="INSERT ignore INTO `satin`.`centrotrabajo` (`id_centrotrabajo`) VALUES (?)";
            try{
                PreparedStatement pstmtCentros = mysql.conn.prepareStatement(insertCentros);
                for (CentrosTrabajo centro : centros) {
                    pstmtCentros.setString(1, centro.getId()); 
                    pstmtCentros.addBatch();
            }
            pstmtCentros.executeBatch();
            centros=new ArrayList<CentrosTrabajo>();
                            System.out.println("Centros faltantes insertadas");

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de Centros", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            

            

                    
    }
    
        private void insertsDBF() {
            classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            
            //Inserción empleados
            String insertEmpleado="INSERT INTO satin.empleados (clave,nombre,apaterno,amaterno,rfc,curp,nss,fecha_ingreso) "+
            "VALUES(?,?,?,?,?,?,?,?) on duplicate key UPDATE nombre=?,apaterno=?,amaterno=?,rfc=?,curp=?,nss=?,fecha_ingreso=?";
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
                    pstmtEmpleado.setString(9, empleado.getNombres());
                    pstmtEmpleado.setString(10, empleado.getaPaterno());
                    pstmtEmpleado.setString(11, empleado.getaMaterno());
                    pstmtEmpleado.setString(12, empleado.getRFC());
                    pstmtEmpleado.setString(13, empleado.getCURP());
                    pstmtEmpleado.setString(14, empleado.getNSS());
                    pstmtEmpleado.setString(15, empleado.getFecha());
                    


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
            String insertDetalleNomina="insert into detalle_nomina (id,producto,clave,tipor,clavep,centro_trabajo,puesto,contrato,"+
            " clavepago,no_puesto,indicador_mando,fechai,fechaf,movimiento,total1,total2,conceptos,"+
            " salariodiario,sindicato,rfc,seq,unidad,instrumento_pago,horas,fonac,digito_ver,pagaduria,control_sar,"+
            " tipo_trabajador,nivel,actividad,sellosat,banco,cuenta_bancaria,clue) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
                    pstmtDetalle.setString(9, detalleNomina.getdClavePago());
                    pstmtDetalle.setString(10, detalleNomina.getdNoPuesto());
                    pstmtDetalle.setString(11, detalleNomina.getdIndicadorMando());
                    pstmtDetalle.setString(12, detalleNomina.getdFechaInicial());
                    pstmtDetalle.setString(13, detalleNomina.getdFechaFinal());
                    pstmtDetalle.setString(14, detalleNomina.getdMovimiento());
                    pstmtDetalle.setString(15, detalleNomina.getdPercepciones());
                    pstmtDetalle.setString(16, detalleNomina.getdDeducciones());
                    pstmtDetalle.setString(17, detalleNomina.getdConceptos());
                    pstmtDetalle.setString(18, detalleNomina.getdSalarioDiario());
                    pstmtDetalle.setString(19, detalleNomina.getdSindicalizado());
                    pstmtDetalle.setString(20, detalleNomina.getdRFC());
                    pstmtDetalle.setString(21, detalleNomina.getdSecuencia());
                    pstmtDetalle.setString(22, detalleNomina.getdUnidadResponsable());
                    pstmtDetalle.setString(23, detalleNomina.getdInstrumentoPago());
                    pstmtDetalle.setString(24, detalleNomina.getdHoras());
                    pstmtDetalle.setString(25, detalleNomina.getdFonac());
                    pstmtDetalle.setString(26, detalleNomina.getdDigito());
                    pstmtDetalle.setString(27, detalleNomina.getdPagaduria());
                    pstmtDetalle.setString(28, detalleNomina.getdControlSar());
                    pstmtDetalle.setString(29, detalleNomina.getdTipoTrabajador());
                    pstmtDetalle.setString(30, detalleNomina.getdNivel());
                    pstmtDetalle.setString(31, detalleNomina.getdActividad());
                    pstmtDetalle.setString(32, detalleNomina.getdNombre2());
                    pstmtDetalle.setString(33, detalleNomina.getdBanco());
                    pstmtDetalle.setString(34, detalleNomina.getdCuentaBancaria());
                    pstmtDetalle.setString(35, detalleNomina.getdClue());

                    
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
            
            
                        //Inserción de centros inexistentes 
            String insertCentros="INSERT ignore INTO `satin`.`centrotrabajo` (`id_centrotrabajo`,`nombre`) VALUES (?,?)";
            try{
                PreparedStatement pstmtCentros = mysql.conn.prepareStatement(insertCentros);
                for (CentrosTrabajo centro : centros) {
                    pstmtCentros.setString(1, centro.getId()); 
                    pstmtCentros.setString(2, centro.getDescripcion()); 

                    pstmtCentros.addBatch();
            }
            pstmtCentros.executeBatch();
            centros=new ArrayList<CentrosTrabajo>();
                            System.out.println("Centros faltantes insertadas");

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de Centros", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
            

                    
    }
        
            // - - - - - - -- - - - - - - - - - - - - - - - - - - - - - INSERCION DE DAT Y TRA - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    private void insertsDAT() {
        
        classes.MySQL mysql= new classes.MySQL();
            mysql.conectar();
            
            //Inserción empleados
            String insertEmpleado="INSERT INTO satin.empleados (clave,nombre,apaterno,amaterno,rfc,curp,nss,fecha_ingreso) "+
            "VALUES(?,?,?,?,?,?,?,?) on duplicate key UPDATE nombre=?,apaterno=?,amaterno=?,rfc=?,curp=?,nss=?,fecha_ingreso=?";
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
                    pstmtEmpleado.setString(9, empleado.getNombres());
                    pstmtEmpleado.setString(10, empleado.getaPaterno());
                    pstmtEmpleado.setString(11, empleado.getaMaterno());
                    pstmtEmpleado.setString(12, empleado.getRFC());
                    pstmtEmpleado.setString(13, empleado.getCURP());
                    pstmtEmpleado.setString(14, empleado.getNSS());
                    pstmtEmpleado.setString(15, empleado.getFecha());
                    


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
            String insertDetalleNomina="insert into detalle_nomina (id,producto,clave,tipor,clavep,centro_trabajo,puesto,contrato,"+
            " clavepago,no_puesto,indicador_mando,fechai,fechaf,movimiento,total1,total2,conceptos,"+
            " sindicato,rfc,seq,unidad,instrumento_pago,horas,fonac,digito_ver,pagaduria,control_sar,"+
            " tipo_trabajador,nivel,rango,porcentaje,edo,mun,actividad,proyecto,partida,gf_sf,sellosat,banco,cuenta_bancaria,clue) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
                    pstmtDetalle.setString(9, detalleNomina.getdClavePago());
                    pstmtDetalle.setString(10, detalleNomina.getdNoPuesto());
                    pstmtDetalle.setString(11, detalleNomina.getdIndicadorMando());
                    pstmtDetalle.setString(12, detalleNomina.getdFechaInicial());
                    pstmtDetalle.setString(13, detalleNomina.getdFechaFinal());
                    pstmtDetalle.setString(14, detalleNomina.getdMovimiento());
                    pstmtDetalle.setString(15, detalleNomina.getdPercepciones());
                    pstmtDetalle.setString(16, detalleNomina.getdDeducciones());
                    pstmtDetalle.setString(17, detalleNomina.getdConceptos());
                    pstmtDetalle.setString(18, detalleNomina.getdSindicalizado());
                    pstmtDetalle.setString(19, detalleNomina.getdRFC());
                    pstmtDetalle.setString(20, detalleNomina.getdSecuencia());
                    pstmtDetalle.setString(21, detalleNomina.getdUnidadResponsable());
                    pstmtDetalle.setString(22, detalleNomina.getdInstrumentoPago());
                    pstmtDetalle.setString(23, detalleNomina.getdHoras());
                    pstmtDetalle.setString(24, detalleNomina.getdFonac());
                    pstmtDetalle.setString(25, detalleNomina.getdDigito());
                    pstmtDetalle.setString(26, detalleNomina.getdPagaduria());
                    pstmtDetalle.setString(27, detalleNomina.getdControlSar());
                    pstmtDetalle.setString(28, detalleNomina.getdTipoTrabajador());
                    pstmtDetalle.setString(29, detalleNomina.getdNivel());
                    pstmtDetalle.setString(30, detalleNomina.getdRango());
                    pstmtDetalle.setString(31, detalleNomina.getdPorcentaje());
                    pstmtDetalle.setString(32, detalleNomina.getdEstado());
                    pstmtDetalle.setString(33, detalleNomina.getdMunicipio());
                    pstmtDetalle.setString(34, detalleNomina.getdActividad());
                    pstmtDetalle.setString(35, detalleNomina.getdProyecto());
                    pstmtDetalle.setString(36, detalleNomina.getdPartida());
                    pstmtDetalle.setString(37, detalleNomina.getdGfSf());
                    pstmtDetalle.setString(38, detalleNomina.getdNombre2());
                    pstmtDetalle.setString(39, detalleNomina.getdBanco());
                    pstmtDetalle.setString(40, detalleNomina.getdCuentaBancaria());
                    pstmtDetalle.setString(41, detalleNomina.getdClue());

                    
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
            
                        //Inserción de centros inexistentes 
            String insertCentros="INSERT ignore INTO `satin`.`centrotrabajo` (`id_centrotrabajo`,`nombre`) VALUES (?,?)";
            try{
                PreparedStatement pstmtCentros = mysql.conn.prepareStatement(insertCentros);
                for (CentrosTrabajo centro : centros) {
                    pstmtCentros.setString(1, centro.getId()); 
                    pstmtCentros.setString(2, centro.getDescripcion()); 

                    pstmtCentros.addBatch();
            }
            pstmtCentros.executeBatch();
            centros=new ArrayList<CentrosTrabajo>();
                            System.out.println("Centros faltantes insertadas");

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Hubo un error en la inserción de Centros", "Advertencia", JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            
    }
    

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - -   EXTRAER DATOS DE DBF (TODO MENOS CONCEPTOS) - - - - - - - - - - - - - - - - - - - - - - - - - - 
        
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
                                if(!pFechaPago.equals("")){
                                    int mes=Integer.parseInt(pFechaPago.substring(5,7));
                                    int dia=Integer.parseInt(pFechaPago.substring(8,10));
                                    if(dia<16){
                                        pMes=String.valueOf((mes*2)-1);
                                    }else{
                                        pMes=String.valueOf(mes*2);
                                    }
                                    pAnio=pFechaPago.substring(0,4);
                                }
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
                                pTotal=totales.subtract(deducciones).toString();
                                if(pFechaPago.equals("")){
                                    pFechaPago=rowObjects[45].toString();
                                }
                                productos.add(new Productos(pClave,pAnio,pMes,pFechaPago,pTotal));
                                
                                //Iniciamos el siguiente registro
                                preClave=rowObjects[58].toString();
                                pAnio=rowObjects[49].toString();
                                if(RFCEmisor.equals("SSC961129CH3")||RFCEmisor.equals("SSO960923M2A")||RFCEmisor.equals("SSN970115QI9")){
                                    pClave=preClave+pAnio.substring(2,4);
                                }else{
                                    pClave=preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                                }                              
                                pMes=rowObjects[48].toString();
                                pFechaPago=rowObjects[43].toString();
                                if(!pFechaPago.equals("")){
                                    int mes=Integer.parseInt(pFechaPago.substring(5,7));
                                    int dia=Integer.parseInt(pFechaPago.substring(8,10));
                                    if(dia<16){
                                        pMes=String.valueOf((mes*2)-1);
                                    }else{
                                        pMes=String.valueOf(mes*2);
                                    }
                                    pAnio=pFechaPago.substring(0,4);
                                }
                                totales=new BigDecimal(rowObjects[53].toString());
                                deducciones=new BigDecimal(rowObjects[54].toString());
                            }
                            
                            
                            //Iniciamos extracción de detalle_nomina
                            


                            dClave=pClave;
                            dNumEmp=rowObjects[0].toString();
                            dTipoR="0";
                            dClaveP=rowObjects[7].toString();
                            dCentroTrabajo=rowObjects[26].toString();
                            dCodigoPuesto=rowObjects[22].toString();
                            dCodigoPuesto=dCodigoPuesto.replaceAll("\\s", "");
                            dContrato="0";
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
                            dSalarioDiario=dDeducciones;//Pendiente calcular 
                            dSindicalizado="NO";
                            dRFC=rowObjects[1].toString();
                            dSecuencia=rowObjects[59].toString();
                            dUnidadResponsable=rowObjects[14].toString();
                            dInstrumentoPago=rowObjects[52].toString();
                            dHoras=rowObjects[34].toString();
                            dFonac=rowObjects[66].toString();
                            dClue=rowObjects[68].toString();
                            if(dClue.equals("")){
                            dClue="CLUE0000000";

                            }
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
                            dID=dClave+dSecuencia;
                            dDescripcionCentro=rowObjects[76].toString(); 
                            detallesNomina.add(new DetalleNomina(dClave,dNumEmp,dRFC,dBanco,dCuentaBancaria,dCentroTrabajo,dCodigoPuesto,dClavePago,dContrato,dClaveContrato,dDescripcion,dFechaInicial,dFechaFinal,dMovimiento,dDeducciones,dClue,dSindicalizado,dConceptos,dID,dTipoR,dControlSar,dTipoTrabajador,dNivel,dActividad,dIndicadorMando,dClaveP,dSalarioDiario,dSecuencia,dUnidadResponsable,dInstrumentoPago,dHoras,dFonac,dPagaduria,dDigito,dNoPuesto,dPercepciones,dNombre2));
                            centros.add(new CentrosTrabajo(dCentroTrabajo,dDescripcionCentro));
            }
                        //Agregamos el registro final de producto al salir del diclo ya que nunca entraría a la condición de ser clave distinta porque no hay más registros.

                            pTotal=totales.subtract(deducciones).toString();
                            if(pFechaPago.equals("")){
                                    pFechaPago=dFechaFinal;
                            }
                            productos.add(new Productos(pClave,pAnio,pMes,pFechaPago,pTotal));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

   //- - - - - - - - - - - - - - - - - - - - - - - - - - - - -  EXTRACCIÓN DE CONCEPTOS DBF - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    private void extraerConceptosDBF() {
        try {
            dbfreader = new DBFReader(new FileInputStream(selectedFile2));
			while ((rowObjects = dbfreader.nextRecord()) != null) {
                            dRFC=rowObjects[0].toString();
                            preClave=rowObjects[11].toString();
                            if(RFCEmisor.equals("SSJ970331PM5")){
                                dClave="E"+preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                            }else{
                                dClave=preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                            }
                            dMovimiento=rowObjects[2].toString();
                            dID=dClave+rowObjects[12].toString();
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

			}
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void extraerDatosTRA(){
            try {
            sc = new Scanner(selectedFile2);
            while(sc.hasNextLine()){
            lineScanner = new Scanner(sc.nextLine());
            lineScanner.useDelimiter("\\|");
            //    String eClave,eAPaterno,eAMaterno, eNombres,eRFC,eCURP,eNSS,eFecha;
            dRFC=lineScanner.next();
            lineScanner.next();
            dMovimiento=lineScanner.next();
            String c1=lineScanner.next();
            if(c1.equals("3")){
                c1="2";
            }
            String c2=lineScanner.next();
            String importe=lineScanner.next();
            pAnio=lineScanner.next();
            pMes=lineScanner.next();
            String c3=lineScanner.next();
            cConcepto=c1+c2+c3;
            String a1=lineScanner.next();
            if(a1.length()>0){
                a1=StringUtils.leftPad(a1, 4, '0');
            }
            
            String a2=lineScanner.next();
            if(a2.length()>0){
                a2=StringUtils.leftPad(a2, 4, '0');
            }
            adicional=a1+a2;
            preClave=lineScanner.next();
            //Verifica si es de Coahuila, Oaxaca o Nuevo Leon ya que la clave de producto es distinta
            if(RFCEmisor.equals("SSC961129CH3")||RFCEmisor.equals("SSO960923M2A")||RFCEmisor.equals("SSN970115QI9")){
                pClave=preClave+pAnio.substring(2,4);
            }else{
                pClave=preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
            }
            dSecuencia=lineScanner.next();
            try{
                cGrabado=lineScanner.next();
                cNoGrabado=lineScanner.next();
            }catch(Exception E){
                cGrabado=importe;
                cNoGrabado="0";
            }
            dID=pClave+dSecuencia;
            conceptos.add(new Conceptos(dID,cConcepto,cGrabado,cNoGrabado));
            adicionales.add(new Adicional(dID,adicional));
            }
        } catch (Exception e) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, e);
            System.out.println(e);
        }
    }
    private void extraerDatosDAT(){
                    try {
            sc = new Scanner(selectedFile,"UTF-8");
            int j=0;
            while(sc.hasNextLine()){
            lineScanner = new Scanner(sc.nextLine());
            j++;
            System.out.println(j);
            lineScanner.useDelimiter("\\|");
            dNumEmp=lineScanner.next();
            eClave=dNumEmp;
            eRFC=lineScanner.next();
            dRFC=eRFC;
            eCURP=lineScanner.next();
            String nombre=lineScanner.next();
            String nombre2=nombre;
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
            dControlSar=lineScanner.next();
            dTipoR=lineScanner.next();
            dBanco=dTipoR;
            dClaveP=lineScanner.next();
            dCuentaBancaria=lineScanner.next();
            dCentroTrabajo="1";
            dContrato="0";
            dDescripcion="0";
            eNSS="0";
            int i=0;
            while(i<6){
                i++;
                String t=lineScanner.next();
            }
            dUnidadResponsable=lineScanner.next();
            String grupoFuncion=lineScanner.next();
            String funcion=lineScanner.next();
            String subfuncion=lineScanner.next();
            String programa=lineScanner.next();
            dGfSf=grupoFuncion+funcion+subfuncion;
            dActividad=lineScanner.next();
            dProyecto=lineScanner.next();
            dPartida=lineScanner.next();
            dCodigoPuesto=lineScanner.next();
            dCodigoPuesto=dCodigoPuesto.replaceAll("\\s", "");
            dNoPuesto=lineScanner.next();
            dEstado=lineScanner.next();
            dMunicipio=lineScanner.next();
            if(RFCEmisor.equals("SSO960923M2A")){
                dClavePago=dProyecto+" "+dUnidadResponsable+" "+dPartida+" "+dCodigoPuesto+" "+programa+dActividad+"0"+dNoPuesto;
            }else if(RFCEmisor.equals("OPD970314U91")){
                dClavePago=dProyecto+" "+dUnidadResponsable+" "+dPartida+" "+dCodigoPuesto+" "+dEstado+" "+dActividad+" "+dGfSf+" "+dNoPuesto;
            }else if(RFCEmisor.equals("SSC961129CH3")){
                dClavePago=dProyecto+" "+dUnidadResponsable+" "+dPartida+" "+dCodigoPuesto+" "+programa+" "+dActividad+" "+dNoPuesto+" "+dGfSf;

            }else if(RFCEmisor.equals("SSM9609248P8")){
                dClavePago=dUnidadResponsable+" "+dProyecto+" "+programa+" "+dActividad+" "+dPartida+" "+dCodigoPuesto+" "+dNoPuesto;
            }else{
                dClavePago=dProyecto+" "+dUnidadResponsable+" "+dPartida+" "+dCodigoPuesto+" "+dEstado+" "+dActividad+" "+dNoPuesto;
            }
            dCentroTrabajo=lineScanner.next();
            eNSS=lineScanner.next();
            if(eNSS.length()>11){
                eNSS=eNSS.substring((eNSS.length()-11),eNSS.length());
            }
            dPagaduria=lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            String nivel=lineScanner.next();
            dRango=lineScanner.next();
            dIndicadorMando=lineScanner.next();
            dHoras=lineScanner.next();
            dPorcentaje=lineScanner.next();
            dTipoTrabajador=lineScanner.next();
            dNivel=lineScanner.next();
            lineScanner.next();
            eFecha=lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            //Pendiente separar mes y año y formato fechas
            pFechaPago=lineScanner.next();
            if(!pFechaPago.equals("")){
                int mes=Integer.parseInt(pFechaPago.substring(5,7));
                int dia=Integer.parseInt(pFechaPago.substring(8,10));
                if(dia<16){
                    pMes=String.valueOf((mes*2)-1);
                }else{
                    pMes=String.valueOf(mes*2);
                }
                pAnio=pFechaPago.substring(0,4);
            }
            dFechaInicial=lineScanner.next();
            dFechaFinal=lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            String tipoPago=lineScanner.next();
            lineScanner.next();
            dInstrumentoPago=lineScanner.next();
            //En Oaxaca Inst Pago está en posición 4
            if(RFCEmisor.equals("SSO960923M2A")){
                dInstrumentoPago=dTipoR;
            }
            dPercepciones=lineScanner.next();
            dDeducciones=lineScanner.next();
            lineScanner.next();
            dConceptos=lineScanner.next();
            lineScanner.next();
            String producto=lineScanner.next();

            
            if(preClave.equals("")){
                preClave=producto;
                //Iniciamos el primer producto
                //Verifica si es de Coahuila, Oaxaca o Nuevo Leon ya que la clave de producto es distinta
                if(RFCEmisor.equals("SSC961129CH3")||RFCEmisor.equals("SSO960923M2A")||RFCEmisor.equals("SSN970115QI9")){
                    pClave=preClave+pAnio.substring(2,4);
                    dClave=pClave;
                }else{
                    pClave=preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                    dClave=pClave;
                }
                totales=new BigDecimal(dPercepciones);

                deducciones=new BigDecimal(dDeducciones);

            }else if(producto.equals(preClave)){
                //Sumamos totales al producto
                totales=totales.add(new BigDecimal(dPercepciones));
                deducciones=deducciones.add(new BigDecimal(dDeducciones));
            }else{
                //Concluimos el registro anterior y lo agregamos a la lista
                preClave=producto;
                pTotal=totales.subtract(deducciones).toString();
                if(pFechaPago.equals("")){
                    pFechaPago=dFechaFinal;
                }
                productos.add(new Productos(pClave,pAnio,pMes,pFechaPago,pTotal));

                //Iniciamos el siguiente registro
                preClave=producto;
                if(RFCEmisor.equals("SSC961129CH3")||RFCEmisor.equals("SSO960923M2A")||RFCEmisor.equals("SSN970115QI9")){
                    pClave=preClave+pAnio.substring(2,4);
                    dClave=pClave;
                }else{
                    pClave=preClave.substring(0, 4)+pAnio.substring(2,4)+preClave.substring(4,preClave.length());
                    dClave=pClave;
                }                                
                totales=new BigDecimal(dPercepciones);
                deducciones=new BigDecimal(dDeducciones);
            }
            dSecuencia=lineScanner.next();
            dMovimiento=lineScanner.next();
            dDigito=lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            dFonac=lineScanner.next();
            lineScanner.next();
            dClue=lineScanner.next();
            if(RFCEmisor.equals("SSO960923M2A")){
                dSindicalizado=dClue;
                dClue="";
            }else{
                dSindicalizado="NO";
            }
            if(dClue.equals("")){
                dClue="CLUE0000000";
            }
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            lineScanner.next();
            dDescripcionCentro=lineScanner.next();
            dID=dClave+dSecuencia;
            
             char cuarto=preClave.charAt(3);
                            if(dTipoTrabajador.equals("20")||RFCEmisor.equals("SSO960923M2A")&&cuarto=='P'){
                                dNombre2=nombre2;//Cargamos nombre del pensionado
                            }else{dNombre2="";}             
            centros.add(new CentrosTrabajo(dCentroTrabajo,dDescripcionCentro));
            empleados.add(new Empleados(eClave,eAPaterno,eAMaterno,eNombres,eRFC,eCURP,eNSS,eFecha));
            detallesNomina.add(new DetalleNomina(dClave, dBanco, dCuentaBancaria, dCentroTrabajo, dCodigoPuesto, dClavePago, dContrato, dDescripcion, dFechaInicial, dFechaFinal, dMovimiento, dPercepciones, dDeducciones, dClue, dSindicalizado, dConceptos, dID, dTipoR, dControlSar, dTipoTrabajador, dNivel, dRango, dPorcentaje, dEstado, dMunicipio, dActividad, dProyecto, dPartida, dGfSf, dNoPuesto, dIndicadorMando, dSecuencia, dUnidadResponsable, dInstrumentoPago, dHoras, dFonac, dPagaduria, dDigito,dNumEmp,dRFC));

            }
            //Ultimo registro de nomina al salir del ciclo
                            pTotal=totales.subtract(deducciones).toString();
                            if(pFechaPago.equals("")){
                                    pFechaPago=dFechaFinal;
                            }
                            productos.add(new Productos(pClave,pAnio,pMes,pFechaPago,pTotal));
        } catch (Exception e) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, e);
            System.out.println(e);
        }
    }
    
    public void fillClues(){
        try {
            classes.MySQL mysql= new classes.MySQL();
            ResultSet rs= mysql.select("SELECT clue,centro_trabajo  FROM satin.detalle_nomina where CLUE NOT IN (SELECT clue from satin.clues) AND clue !=\"\" group by clue;");
            while(rs.next()){
                clues.add(new Clues(rs.getString(1),rs.getString(2)));
            }
            PreparedStatement pstmtClues = mysql.conn.prepareStatement("INSERT INTO `satin`.`clues` (`clue`, `id_centrotrabajo`) VALUES (?, ?);");
                for (Clues clue : clues) {
                    pstmtClues.setString(1, clue.getClue());
                    pstmtClues.setString(2, clue.getCentro());
                    pstmtClues.addBatch();
                }
                pstmtClues.executeBatch();
                System.out.println("Clues insertados correctamente");
        } catch (SQLException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void validaciones(){
        boolean pendientes=false;
        String noValidados="Algunos registros no pudieron ser validados. Para timbrarlos, es necesario completar la información en el módulo VALIDACIONES PENDIENTES: \n --------------------------------NO EXISTE EL PUESTO:---------------------------------- \n";
        //Pasamos todos aquellos que no se encuentran en catalogo a estatus pendiente (P)
        classes.MySQL mysql=new classes.MySQL();
        ResultSet rs= mysql.select("SELECT id,rfc,movimiento,fechaf  FROM satin.detalle_nomina where puesto NOT IN (SELECT id_puestos from satin.puestos) AND puesto !=\"\" AND vpuesto=\"N\"");
        try {
            PreparedStatement pstmtPuestosP=mysql.conn.prepareStatement("UPDATE `satin`.`detalle_nomina` SET `vpuesto` = 'P' WHERE (`id` = ?) and (`rfc` = ?)");
            while(rs.next()){
                pendientes=true;
                noValidados=noValidados+"RFC: "+rs.getString(2)+" Movimiento: "+rs.getString(3)+" Fecha Final: "+rs.getString(4)+"\n";
                    pstmtPuestosP.setString(1, rs.getString(1));
                    pstmtPuestosP.setString(2, rs.getString(2));
                    pstmtPuestosP.addBatch();

            }
            pstmtPuestosP.executeBatch();

        } catch (SQLException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Ahora verificamos aquellos que si existen y los cambiamos a estatus "V
        rs= mysql.select("SELECT id,rfc  FROM satin.detalle_nomina where puesto IN (SELECT id_puestos from satin.puestos) AND puesto !=\"\" AND vpuesto=\"N\"");
        try {
            PreparedStatement pstmtPuestosV=mysql.conn.prepareStatement("UPDATE `satin`.`detalle_nomina` SET `vpuesto` = 'V' WHERE (`id` = ?) and (`rfc` = ?)");
            while(rs.next()){
                    pstmtPuestosV.setString(1, rs.getString(1));
                    pstmtPuestosV.setString(2, rs.getString(2));
                    pstmtPuestosV.addBatch();

            }
            pstmtPuestosV.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Pasamos a pendiente los detalle_conceptos que no se encuentren en conceptos
        rs= mysql.select("SELECT id FROM satin.detalle_conceptos where id_concepto not in (SELECT id_concepto FROM satin.conceptos)");
        try {
            PreparedStatement pstmtConceptosP=mysql.conn.prepareStatement("UPDATE `satin`.`detalle_conceptos` SET `validacion` = 'P' WHERE (`id` = ?);");
            while(rs.next()){
                    pendientes=true;
                    pstmtConceptosP.setString(1, rs.getString(1));
                    pstmtConceptosP.addBatch();

            }
            pstmtConceptosP.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Validamos aquellos detalle_conceptos que si se encuentran en conceptos
        rs= mysql.select("SELECT id FROM satin.detalle_conceptos where id_concepto in (SELECT id_concepto FROM satin.conceptos)");
        try {
            PreparedStatement pstmtConceptosV=mysql.conn.prepareStatement("UPDATE `satin`.`detalle_conceptos` SET `validacion` = 'V' WHERE (`id` = ?);");
            while(rs.next()){
                    pstmtConceptosV.setString(1, rs.getString(1));
                    pstmtConceptosV.addBatch();

            }
            pstmtConceptosV.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Finalmente verificamos que el número de conceptos validados sea mayor o igual al número de conceptos en detalle_nomina y pasamos aquellos registros que cumplan la condicion a verificados  y los que no a pendientes
        rs= mysql.select("select dn.id,dn.rfc from satin.detalle_nomina dn left join (SELECT id_detalle_nomina, COUNT(*) as contador FROM satin.detalle_conceptos where validacion=\"V\" group by id_detalle_nomina) c on c.id_detalle_nomina=dn.id where dn.conceptos<=c.contador and dn.vconceptos=\"N\"");
        try {
            PreparedStatement pstmtVConceptos=mysql.conn.prepareStatement("UPDATE `satin`.`detalle_nomina` SET `vconceptos` = 'V' WHERE (`id` = ?) and (`rfc` = ?)");
            while(rs.next()){
                    pstmtVConceptos.setString(1, rs.getString(1));
                    pstmtVConceptos.setString(2, rs.getString(2));
                    pstmtVConceptos.addBatch();
            }
            pstmtVConceptos.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Pasamos a pendientes los que no se validaron
        rs= mysql.select("select id,rfc,movimiento,fechaf from satin.detalle_nomina where vconceptos=\"N\"");
        try {
            noValidados=noValidados+"--------------------------------CONCEPTOS FALTANTES:----------------------------------\n";
            PreparedStatement pstmtPConceptos=mysql.conn.prepareStatement("UPDATE `satin`.`detalle_nomina` SET `vconceptos` = 'P' WHERE (`id` = ?) and (`rfc` = ?)");
            while(rs.next()){
                    noValidados=noValidados+"RFC: "+rs.getString(2)+" Movimiento: "+rs.getString(3)+" Fecha Final: "+rs.getString(4)+"\n";
                    pstmtPConceptos.setString(1, rs.getString(1));
                    pstmtPConceptos.setString(2, rs.getString(2));
                    pstmtPConceptos.addBatch();
            }
            pstmtPConceptos.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
if(pendientes){        
        JOptionPane.showMessageDialog(null, "Algunos registros no fueron validados. Se generará un archivo con los registros pendientes.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivo de texto (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
 
            //Show save file dialog
            File file = fileChooser.showSaveDialog(stage);
            if(file!=null){
            PrintWriter writer;
            try {
                writer = new PrintWriter(file);
                writer.println(noValidados);
                writer.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
            }

            }
                    try {
            Files.write(Paths.get("./DetallesPendientes.txt"), noValidados.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(CargaArchivosNominaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }else{
    JOptionPane.showMessageDialog(null,"El producto se ha insertado correctamente y está listo para su timbrado.");
    }
}


    


}
