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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Jorge
 */
public class GenerarXML {
    public static String ruta="C:/Facturacion";
    static NumberFormat fmt = NumberFormat.getInstance(Locale.US);
    static int i=0;

    /* Clase main se utilizó para testing, no es necesaria pero se deja por si se requieren hacer más pruebas
    public static void main(String args[]){
        try {
            MySQL mysql=new MySQL();
            mysql.conectar();
            ResultSet rs= mysql.select("SELECT dn.total1,dn.total2,e.rfc,CONCAT(e.apaterno,\" \",e.amaterno,\" \",e.nombre) as nombre, dn.fechai,dn.fechaf,pn.fechapago,dn.sindicato,dn.puesto,e.fecha_ingreso,e.nss,e.clave,e.curp,e.jornada,dn.contrato,dn.producto,dn.id,dn.movimiento,p.descripcion,dn.unidad,dn.clavep,dn.banco,dn.cuenta_bancaria,dn.actividad,dn.proyecto,dn.partida,dn.clavepago,dn.clue,e.ingreso_acumulable,e.ingreso_no_acumulable, e.ultimo_sueldo from detalle_nomina dn, empleados e, puestos p,producto_nomina pn where dn.clave=e.clave and dn.rfc=e.rfc and dn.puesto=p.id_puestos AND pn.clave=dn.producto;");
            loadDatos();
            while(rs.next()){
               CalcularXML t=new CalcularXML(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19),rs.getString(20),rs.getString(21),rs.getString(22),rs.getString(23),rs.getString(24),rs.getString(25),rs.getString(26),rs.getString(27),rs.getString(28),rs.getString(29),rs.getString(30),rs.getString(31));
                xml(t); 
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(GenerarXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    public static void xml(CalcularXML t){
    fmt.setMaximumFractionDigits(2);
    fmt.setMinimumFractionDigits(2);
    fmt.setGroupingUsed(false);

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
    
    rootElement.setAttribute("Sello", "");//No se genera
    rootElement.setAttribute("Certificado", "");//No se genera
    rootElement.setAttribute("LugarExpedicion", DatosPatronales.getCodigo_esp());//Codigo especial
    rootElement.setAttribute("Moneda", "MXN");
    String total=fmt.format(t.getSubtotal()-t.getDescuentoFactura());
    rootElement.setAttribute("Total", total);//percepciones- deducciones?
    String subtotal=fmt.format(t.getSubtotal());
    rootElement.setAttribute("SubTotal", subtotal);//percepciones?
    rootElement.setAttribute("NoCertificado", "");//DatosTimbrado.getNum_cert());//??
    if(!t.isPoneHonorarios()){
        rootElement.setAttribute("TipoCambio", "1");//Fijo
    }
    rootElement.setAttribute("MetodoPago", "PUE");//Fijo
    rootElement.setAttribute("FormaPago", "99");//Fijo
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
    emisorElement.setAttribute("RegimenFiscal", "603");//fijo 
    
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
    Element conceptoElement=doc.createElement("cfdi:Concepto");
    conceptoElement.setAttribute("Descuento", fmt.format(t.getDescuentoFactura())); //se calcoula en subsidios
    if(t.getTotalImpuestos2()!=99999){
        conceptoElement.setAttribute("Importe", fmt.format(t.getSubtotalFactura()+t.getTotalOtros()-t.getSubsidio())); //se calcula en subsidios
        conceptoElement.setAttribute("ValorUnitario", fmt.format(t.getSubtotalFactura()+t.getTotalOtros()-t.getSubsidio()));
    }else{
        conceptoElement.setAttribute("Importe", fmt.format(t.getSubtotalFactura()+t.getSubsidio2())); //se calcula en subsidios
        conceptoElement.setAttribute("ValorUnitario", fmt.format(t.getSubtotalFactura()+t.getSubsidio2()));
    }
    conceptoElement.setAttribute("ClaveUnidad", "ACT"); //Fijo
    conceptoElement.setAttribute("Descripcion", "Pago de nómina");//Fijo
    conceptoElement.setAttribute("Cantidad", "1");//Fijo
    conceptoElement.setAttribute("ClaveProdServ", "84111505");//Fijo
    conceptosElement.appendChild(conceptoElement);
    
    
    Element complementoElement=doc.createElement("cfdi:Complemento");
    rootElement.appendChild(complementoElement);
    Element nominaElement=doc.createElement("nomina12:Nomina");
    nominaElement.setAttribute("Version", DatosTimbrado.getVersion_nomina());//Estaba fijo, ingresar correctamente a BD
    if((t.getTotalOtros()+t.getTotalOtro1())>0){
        nominaElement.setAttribute("TotalOtrosPagos",fmt.format(t.getTotalOtros()+t.getTotalOtro1()));
    }else if((t.getTotalOtros()+t.getTotalOtro1())<0){
        nominaElement.setAttribute("TotalOtrosPagos",fmt.format((t.getTotalOtros()+t.getTotalOtro1())*(-1)));
    }
    nominaElement.setAttribute("TotalPercepciones", fmt.format(t.getTotalGravado()+t.getTotalExento()));//Suma
    nominaElement.setAttribute("NumDiasPagados", String.valueOf(t.getDiasPagados()));//Resta de fecha inicial y final
    nominaElement.setAttribute("FechaFinalPago", t.getFechaf());//Tabla detalle
    nominaElement.setAttribute("FechaInicialPago", t.getFechai());//tabla detalle
    nominaElement.setAttribute("FechaPago", t.getFechapago());
    nominaElement.setAttribute("TipoNomina", t.getTipoNomina());//Pendiente determinar si es ordinaria o extraoridnaria
    complementoElement.appendChild(nominaElement);
    Element nominaEElement=doc.createElement("nomina12:Emisor");
    if(t.isPonerRegistroPatronal()){
            nominaEElement.setAttribute("RegistroPatronal", DatosPatronales.getRegistro_patronal());//??
    }
    nominaElement.appendChild(nominaEElement);
    if(t.isPonerEntidadSNCF()){
        Element EntidadSNCFElement=doc.createElement("nomina12:EntidadSNCF");
        EntidadSNCFElement.setAttribute("OrigenRecurso", t.getCFDOrigen());
        if(t.getCFDOrigen().equals("IM")){
            EntidadSNCFElement.setAttribute("MontoRecursoPropio", fmt.format(t.getImportePropio()));
        }
        nominaEElement.appendChild(EntidadSNCFElement);
    }

    Element nominaRElement=doc.createElement("nomina12:Receptor");
    nominaRElement.setAttribute("Sindicalizado", t.getSindicato());//Tabla empleados
    nominaRElement.setAttribute("ClaveEntFed", DatosPatronales.getEstado());//Datos patronales?
    if(!t.isPoneHonorarios()){
        nominaRElement.setAttribute("RiesgoPuesto", t.getTipoRiesgo());//También está en datos patronales
        nominaRElement.setAttribute("FechaInicioRelLaboral", t.getFecha_ingreso());//Tabla empleados
        String nss=t.getNss();
        if(nss.equals(null)||nss.equals("")){
            nss="000000000";
        }
        nominaRElement.setAttribute("NumSeguridadSocial", nss);//Tabla empleados
        nominaRElement.setAttribute("Antigüedad", t.getAntiguedad());// Pendiente. Se calcula por un método
        nominaRElement.setAttribute("SalarioDiarioIntegrado", fmt.format(t.getSalarioDiario()));
        nominaRElement.setAttribute("SalarioBaseCotApor", fmt.format(t.getSalarioDiario()));
    }
    if(!t.getDescripcionPuesto().equals("0")&&!t.getDescripcionPuesto().equals("")){
        nominaRElement.setAttribute("Puesto", t.getDescripcionPuesto());//Empleados -> puestos
    }
    if(t.isPoneHonorarios()){
        nominaRElement.setAttribute("Banco", StringUtils.leftPad(t.getBanco(), 3, '0'));//Empleados -> puestos
        if(t.getCuentaBancaria().equals("")||t.getCuentaBancaria().equals("0")){
            t.setCuentaBancaria("0000000000");
        }
        nominaRElement.setAttribute("CuentaBancaria", t.getCuentaBancaria().replaceAll("'", ""));//Empleados -> puestos
        nominaRElement.setAttribute("Departamento", t.getClue());//Empleados -> puestos
    }
    nominaRElement.setAttribute("PeriodicidadPago", t.getPeriodoPago());
    nominaRElement.setAttribute("NumEmpleado", t.getClave());
    nominaRElement.setAttribute("Curp", t.getCurp());
    nominaRElement.setAttribute("TipoRegimen", StringUtils.leftPad(t.getTipoRegimen(), 2, '0'));
    nominaRElement.setAttribute("TipoJornada", StringUtils.leftPad(t.getJornada(), 2, '0'));//03 mixta, 01 diurno
    nominaRElement.setAttribute("TipoContrato", t.getTipoContrato());//Verificada
    nominaElement.appendChild(nominaRElement);
    Element percepcionesElement=doc.createElement("nomina12:Percepciones");
    percepcionesElement.setAttribute("TotalSueldos", fmt.format(t.getTotalSueldos()));//Percepciones?
    percepcionesElement.setAttribute("TotalExento", fmt.format(t.getTotalExento()));//Algunos casos hay parte excenta?
    percepcionesElement.setAttribute("TotalGravado", fmt.format(t.getTotalGravado()));//Percepciones?
    if(t.getTotalJubilacionPensionRetiro()>0){
        percepcionesElement.setAttribute("TotalJubilacionPensionRetiro",fmt.format(t.getTotalJubilacionPensionRetiro()));
    }
    if(t.getTotalSeparacion()>0){
        percepcionesElement.setAttribute("TotalSeparacionIndemnizacion",fmt.format(t.getTotalSeparacion()));
    }
    nominaElement.appendChild(percepcionesElement);
    for(Percepciones p : t.percepcionesList){
        Element percepcionElement=doc.createElement("nomina12:Percepcion");
        percepcionElement.setAttribute("ImporteExento", p.getImp_exe());//No gravado
        percepcionElement.setAttribute("ImporteGravado", p.getImp_gra());//Gravado
        percepcionElement.setAttribute("Concepto", p.getConcepto());//Descripcion tabla conceptos?
        percepcionElement.setAttribute("Clave", p.getClave());//id_concepto?
        percepcionElement.setAttribute("TipoPercepcion", p.getTipo_per());//Tipo de tabla conceptos? Como se obtenía antes de ese campo?
        percepcionesElement.appendChild(percepcionElement);
        
    }
    for(Jubilaciones j : t.jubilacionesList){
        Element jubilacioncionElement=doc.createElement("nomina12:JubilacionPensionRetiro");
        jubilacioncionElement.setAttribute("IngresoAcumulable", j.ingresoAcumulable);//No gravado
        jubilacioncionElement.setAttribute("IngresoNoAcumulable", j.ingresoNoAcumulable);//Gravado
        jubilacioncionElement.setAttribute("TotalUnaExhibicion", j.totalJubilacionPensionRetiro);//Descripcion tabla conceptos?
        percepcionesElement.appendChild(jubilacioncionElement);
        
    }
    for(Indemnizaciones in : t.indemnizacionesList){
        Element separacionElement=doc.createElement("nomina12:SeparacionIndemnizacion");
        separacionElement.setAttribute("TotalPagado", in.totalPagado);
        separacionElement.setAttribute("NumAñosServicio", in.aniosServicio);
        separacionElement.setAttribute("UltimoSueldoMensOrd", in.ultimoSueldoMensOrd);
        separacionElement.setAttribute("IngresoAcumulable", in.ingresoAcumulable);
        separacionElement.setAttribute("IngresoNoAcumulable", in.ingresoNoAcumulable);
        percepcionesElement.appendChild(separacionElement);
    }
    for(Horas h : t.horasList){
        Element horasElement=doc.createElement("nomina12:HorasExtra");
        horasElement.setAttribute("Dias", h.getDias());
        horasElement.setAttribute("TipoHoras", h.getTipoHoras());
        horasElement.setAttribute("HorasExtra", h.getHorasExtra());
        horasElement.setAttribute("ImportePagado", h.getImportePagado());
        percepcionesElement.appendChild(horasElement);
    }
    if(t.descuentoFactura>0){
    }
    if(t.descuentoFactura!=0){
        nominaElement.setAttribute("TotalDeducciones", fmt.format(t.getDescuentoFactura()));
        Element deduccionesElement=doc.createElement("nomina12:Deducciones");
        if(t.getTotalImpuestos()>0){
            deduccionesElement.setAttribute("TotalImpuestosRetenidos", fmt.format(t.getTotalImpuestos()));//Suma otros conceptos
        }
        deduccionesElement.setAttribute("TotalOtrasDeducciones", fmt.format(t.getTotalDeducciones()));//Suma algunos conceptos
        nominaElement.appendChild(deduccionesElement);
        for(Deducciones d : t.deduccionesList){//Cambiar por ciclo de objetos listados
            Element deduccionElement=doc.createElement("nomina12:Deduccion");
            deduccionElement.setAttribute("Importe", d.getImporte());//Cantidad
            deduccionElement.setAttribute("Concepto", d.getConcepto());//Descripcion de tabla concepto?
            deduccionElement.setAttribute("Clave", d.getClave());//id concepto?
            deduccionElement.setAttribute("TipoDeduccion", d.getTipoDeduccion());//tipo de tabla conceptos? como se obtenía antes?
            deduccionesElement.appendChild(deduccionElement);
        }
    }
    
        if(t.otrosPagosList.size()>0&&t.getTotalOtro1NO()==0){
        if((t.getTotalOtros()+t.getTotalOtro1())>0){
            percepcionesElement.setAttribute("TotalOtrosPagos",fmt.format(t.getTotalOtros()+t.getTotalOtro1()));
        }else{
            percepcionesElement.setAttribute("TotalOtrosPagos",fmt.format((t.getTotalOtros()+t.getTotalOtro1())*(-1)));
        }
            Element otrosPagosElement=doc.createElement("nomina12:OtrosPagos");
            nominaElement.appendChild(otrosPagosElement);
            if(t.getTipoRegimen().equals("02")){
                Element otroPagoSubsidioElement=doc.createElement("nomina12:OtroPago");
                otroPagoSubsidioElement.setAttribute("TipoOtroPago", "002");
                otroPagoSubsidioElement.setAttribute("Clave", "1SE00");
                otroPagoSubsidioElement.setAttribute("Conceptop", "SUBSIDIO PARA EL EMPLEO (EFECTIVAMENTE ENTREGADO AL TRABAJADOR)");
                otroPagoSubsidioElement.setAttribute("Importe", fmt.format(t.getTotalOtros()));
                otrosPagosElement.appendChild(otroPagoSubsidioElement);
                Element subsidioAlEmpleo=doc.createElement("nomina12:SubsidioAlEmpleo");
                subsidioAlEmpleo.setAttribute("SubsidioCausado", fmt.format(t.getSubsidio()));
                otroPagoSubsidioElement.appendChild(subsidioAlEmpleo);
            }
            for(OtrosPagos op:t.otrosPagosList){
                Element otroPagoElement=doc.createElement("nomina12:OtroPago");
                if(op.getTipo().equals("005")){
                    otroPagoElement.setAttribute("Importe", fmt.format(t.getImpuestosSF()*(-1)));
                }else if(op.getTipo().equals("001")){
                    otroPagoElement.setAttribute("Importe", fmt.format(t.getImpuestosSF()));
                }else{
                    otroPagoElement.setAttribute("Importe", op.getImporte());

                }
                otroPagoElement.setAttribute("Concepto", op.getConcepto());
                otroPagoElement.setAttribute("Clave", op.getClave());
                otroPagoElement.setAttribute("TipoOtroPago", op.getTipo());
                otrosPagosElement.appendChild(otroPagoElement);

            }
        } 

        if(t.incapacidadesList.size()>0){
            Element incapacidadesElement=doc.createElement("nomina12:Incapacidades");
            nominaElement.appendChild(incapacidadesElement);
            for(Incapacidades in:t.incapacidadesList){
                Element incapacidadElement=doc.createElement("nomina12:Incapacidad");
                incapacidadElement.setAttribute("DiasIncapacidad", in.getDias());
                incapacidadElement.setAttribute("TipoIncapacidad", in.getTipo());
                incapacidadElement.setAttribute("ImporteMonetario", in.getImporte());
                incapacidadesElement.appendChild(incapacidadElement);
            }

        }



    





    
      doc.appendChild(rootElement);
    i++;
    System.out.println("Factura no: "+i);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      String rutaFinal=ruta+"/"+t.getProducto()+"/";
      System.out.println(rutaFinal);
    File directory = new File(rutaFinal);
        if (! directory.exists()){
        directory.mkdirs();
        // If you require it to make the entire directory path including parents,
        // use directory.mkdirs(); here instead.
    }
      
      
      StreamResult result = new StreamResult(new File(rutaFinal+t.id+".xml"));
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
