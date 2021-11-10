/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import controller.LoginViewController;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Jorge
 */
public class GenerarXML {
    public static void main(String args[]){
        try {
            MySQL mysql=new MySQL();
            mysql.conectar();
            ResultSet rs= mysql.select("SELECT dn.total1,dn.total2,e.rfc,CONCAT(e.apaterno,\" \",e.amaterno,\" \",e.nombre) as nombre, dn.fechai,dn.fechaf,pn.fechapago,dn.sindicato,dn.puesto,e.fecha_ingreso,e.nss,e.clave,e.curp,e.jornada,dn.contrato,dn.producto,dn.id,dn.movimiento,p.descripcion,dn.unidad,dn.clavep,dn.banco,dn.cuenta_bancaria,dn.actividad,dn.proyecto,dn.partida,dn.clavepago,dn.clue from detalle_nomina dn, empleados e, puestos p,producto_nomina pn where dn.clave=e.clave and dn.puesto=p.id_puestos AND pn.clave=dn.producto;");
            rs.next();
            Timbrado t=new Timbrado(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19),rs.getString(20),rs.getString(21),rs.getString(22),rs.getString(23),rs.getString(24),rs.getString(25),rs.getString(26),rs.getString(27),rs.getString(28));
            loadDatos();
            xml(t);
        } catch (SQLException ex) {
            Logger.getLogger(GenerarXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void xml(Timbrado t){
                  try {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    //Obtenemos la fecha y le damos formato
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    Date date = new Date();
    String timestamp=sdf.format(ts);


      //Generamos el nodo raíz
    Document doc = docBuilder.newDocument();
    Element rootElement = doc.createElement("cfdi:Comprobante");
    
    rootElement.setAttribute("Sello", "despues");//No se genera
    rootElement.setAttribute("Certificado", DatosTimbrado.getNum_cert());//No se genera
    rootElement.setAttribute("LugarExpedicion", "de una tabla");//Codigo especial
    rootElement.setAttribute("Moneda", "MXN");
    double total=Double.parseDouble(t.getPercepciones())-Double.parseDouble(t.getDeducciones());
    rootElement.setAttribute("Total", String.valueOf(total));//percepciones- deducciones?
    rootElement.setAttribute("SubTotal", t.getPercepciones());//percepciones?
    rootElement.setAttribute("NoCertificado", DatosTimbrado.getNum_cert());//??
    rootElement.setAttribute("TipoCambio", "1");//Fijo?
    rootElement.setAttribute("MetodoPago", "PUE");//Fijo?
    rootElement.setAttribute("FormaPago", "99");//Fijo?
    rootElement.setAttribute("Fecha", timestamp);
    rootElement.setAttribute("Folio", t.getMovimiento());//serie de detalle nomina
    rootElement.setAttribute("Serie", DatosPatronales.getSerie());
    rootElement.setAttribute("Version", DatosTimbrado.getVersion_cfdi());
    rootElement.setAttribute("TipoDeComprobante", "N");
    rootElement.setAttribute("xsi:schemaLocation", "http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd http://www.sat.gob.mx/nomina12 http://www.sat.gob.mx/sitio_internet/cfd/nomina/nomina12.xsd");
    rootElement.setAttribute("xmlns:nomina12", "http://www.sat.gob.mx/nomina12");
    rootElement.setAttribute("xmlns:xsi", DatosTimbrado.getXml_xsi());//En BD crear el registro
    rootElement.setAttribute("xmlns:cfdi", DatosTimbrado.getXml_cfdi()); //En BD crear el registro
    
    Element emisorElement = doc.createElement("cfdi:Emisor");
    emisorElement.setAttribute("RegimenFiscal", DatosPatronales.getRegimen()); 
    emisorElement.setAttribute("Rfc", DatosPatronales.getRfc()); 
    emisorElement.setAttribute("Nombre", DatosPatronales.getNombre()); 
    rootElement.appendChild(emisorElement);
    
    Element receptorElement = doc.createElement("cfdi:Receptor");
    receptorElement.setAttribute("Rfc", t.getRfc()); 
    receptorElement.setAttribute("Nombre", t.getNombre());
    receptorElement.setAttribute("UsoCFDI", "P01"); //Fijo
    rootElement.appendChild(receptorElement);
    
    Element conceptosElement = doc.createElement("cfdi:Conceptos");
    rootElement.appendChild(conceptosElement);
    int i=0;//Cambiar por un while rs.next();
    Element conceptoElement=doc.createElement("cfdi:Concepto");
    conceptoElement.setAttribute("Descuento", t.getDeducciones()); //se calcoula en subsidios
    conceptoElement.setAttribute("Importe", t.getPercepciones()); //se calcula en subsidios
    conceptoElement.setAttribute("ValorUnitario", t.getPercepciones());//se calcula en subsidios
    conceptoElement.setAttribute("ClaveUnidad", "ACT"); //Fijo
    conceptoElement.setAttribute("Descripcion", "Pago de nómina");//Fijo
    conceptoElement.setAttribute("Cantidad", "1");//Fijo
    conceptoElement.setAttribute("ClaveProdServ", "cc");//Fijo
    conceptosElement.appendChild(conceptoElement);
    i++;
    Element complementoElement=doc.createElement("cfdi:Complemento");
    rootElement.appendChild(complementoElement);
    Element nominaElement=doc.createElement("nomina12:Nomina");
    nominaElement.setAttribute("Version", DatosTimbrado.getVersion_nomina());
    nominaElement.setAttribute("TotalOtrosPagos", "cc");//Suma
    nominaElement.setAttribute("TotalDeducciones", t.getDeducciones());//Suma
    nominaElement.setAttribute("TotalPercepciones", "cc");//Suma
    nominaElement.setAttribute("NumDiasPagados", "cc");//Resta de fecha inicial y final
    nominaElement.setAttribute("FechaFinalPago", t.getFechaf());//Tabla detalle
    nominaElement.setAttribute("FechaInicialPago", t.getFechai());//tabla detalle
    nominaElement.setAttribute("FechaPago", t.getFechapago());
    nominaElement.setAttribute("TipoNomina", "O");//Fijo?
    complementoElement.appendChild(nominaElement);
    Element nominaEElement=doc.createElement("nomina12:Emisor");
    nominaEElement.setAttribute("RegistroPatronal", DatosPatronales.getRegistro_patronal());//??
    nominaElement.appendChild(nominaEElement);
    Element EntidadSNCFElement=doc.createElement("nomina12:EntidadSNCF");
    EntidadSNCFElement.setAttribute("OrigenRecurso", DatosPatronales.getEntidad_sncf());//entidad sncf?
    nominaEElement.appendChild(EntidadSNCFElement);
    Element nominaRElement=doc.createElement("nomina12:Receptor");
    nominaRElement.setAttribute("SalarioDiarioIntegrado", "cc");//Calculo Pendiente
    nominaRElement.setAttribute("SalarioBaseCotApor", "cc");//Calculo Pendiente
    nominaRElement.setAttribute("Antigüedad", "cc");//?
    nominaRElement.setAttribute("Sindicalizado", t.getSindicato());//Tabla empleados
    nominaRElement.setAttribute("Puesto", t.getPuesto());//Empleados -> puestos
    nominaRElement.setAttribute("ClaveEntFed", "entidad");//Datos patronales?
    nominaRElement.setAttribute("RiesgoPuesto", DatosPatronales.getRiesgo_puesto());//Pendiente? O dejar fijo? Está en datos patronales
    nominaRElement.setAttribute("FechaInicioRelLaboral", t.getFecha_ingreso());//Tabla empleados
    nominaRElement.setAttribute("NumSeguridadSocial", t.getNss());//Tabla empleados
    nominaRElement.setAttribute("PeriodicidadPago", "");//??
    nominaRElement.setAttribute("NumEmpleado", t.getClave());//Tabla empleados
    nominaRElement.setAttribute("Curp", t.getCurp());//Tabla empleados
    nominaRElement.setAttribute("TipoRegimen", "02");//Fijo?
    nominaRElement.setAttribute("TipoJornada", t.getJornada());//Tabla empleados
    nominaRElement.setAttribute("TipoContrato", t.getContrato());//Campo contrato en detalle_nomina?
    nominaElement.appendChild(nominaRElement);
    Element percepcionesElement=doc.createElement("nomina12:Percepciones");
    percepcionesElement.setAttribute("TotalSueldos", "cc");//Percepciones?
    percepcionesElement.setAttribute("TotalExento", "cc");//Algunos casos hay parte excenta?
    percepcionesElement.setAttribute("TotalGravado", "cc");//Percepciones?
    nominaElement.appendChild(percepcionesElement);
    i=0;//Cambiar por while rs.next()
    while(i<5){
        Element percepcionElement=doc.createElement("nomina12:Percepcion");
        percepcionElement.setAttribute("ImporteExento", "cc");//No gravado
        percepcionElement.setAttribute("ImporteGravado", "cc");//Gravado
        percepcionElement.setAttribute("Concepto", "cc");//Descripcion tabla conceptos?
        percepcionElement.setAttribute("Clave", "cc");//id_concepto?
        percepcionElement.setAttribute("TipoPercepcion", "cc");//Tipo de tabla conceptos? Como se obtenía antes de ese campo?
        percepcionesElement.appendChild(percepcionElement);
        i++;
    }
    if(true){//Cambiar por condición para ver si hay deducciones o no
        Element deduccionesElement=doc.createElement("nomina12:Deducciones");
        deduccionesElement.setAttribute("TotalOtrasDeducciones", "cc");//Suma algunos conceptos
        deduccionesElement.setAttribute("TotalImpuestosRetenidos", "cc");//Suma otros conceptos
        nominaElement.appendChild(deduccionesElement);
        i=0;
        while(i<5){
            Element deduccionElement=doc.createElement("nomina12:Deduccion");
            deduccionElement.setAttribute("Importe", "cc");//Cantidad
            deduccionElement.setAttribute("Concepto", "cc");//Descripcion de tabla concepto?
            deduccionElement.setAttribute("Clave", "cc");//id concepto?
            deduccionElement.setAttribute("TipoDeduccion", "cc");//tipo de tabla conceptos? como se obtenía antes?
            deduccionesElement.appendChild(deduccionElement);
            i++;
        }
    }
    Element otrosPagosElement=doc.createElement("nomina12:OtrosPagos");
    nominaElement.appendChild(otrosPagosElement);
    i=0;
    while(i<1){//Cambiar por while rs.next()
        Element otroPagoElement=doc.createElement("nomina12:OtroPago");
        otroPagoElement.setAttribute("Importe", "cc");
        otroPagoElement.setAttribute("Concepto", "cc");
        otroPagoElement.setAttribute("Clave", "cc");
        otroPagoElement.setAttribute("TipoOtroPago", "cc");
        otrosPagosElement.appendChild(otroPagoElement);
        //Case aquí para determinar que nodo de "otros pagos" se agrega
        Element subsidioElement=doc.createElement("nomina12:SubsidioAlEmpleo");
        subsidioElement.setAttribute("SubsidioCausado", "cc");
        otroPagoElement.appendChild(subsidioElement);
    
        i++;
    }

    





    
      doc.appendChild(rootElement);

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File("C:/ruta/prueba.xml"));
      transformer.transform(source, result);
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (TransformerException tfe) {
      tfe.printStackTrace();
    }
    }
    //BORRAR. ESTA EN LOGIN.
        public static void loadDatos(){
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

        }
    }

}
