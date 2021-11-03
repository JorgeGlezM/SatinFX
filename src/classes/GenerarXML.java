/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.File;
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
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      //Generamos el nodo raíz
      Document doc = docBuilder.newDocument();
      Element rootElement = doc.createElement("cfdi:Comprobante");

    rootElement.setAttribute("Sello", "test");
    rootElement.setAttribute("Certificado", "cc");
    rootElement.setAttribute("LugarExpedicion", "cc");
    rootElement.setAttribute("Moneda", "cc");
    rootElement.setAttribute("Descuento", "cc");
    rootElement.setAttribute("Total", "cc");
    rootElement.setAttribute("SubTotal", "cc");
    rootElement.setAttribute("NoCertificado", "cc");
    rootElement.setAttribute("TipoCambio", "cc");
    rootElement.setAttribute("MetodoPago", "cc");
    rootElement.setAttribute("FormaPago", "cc");
    rootElement.setAttribute("Fecha", "cc");
    rootElement.setAttribute("Folio", "cc");
    rootElement.setAttribute("Serie", "cc");
    rootElement.setAttribute("Version", "cc");
    rootElement.setAttribute("TipoDeComprobante", "cc");
    rootElement.setAttribute("xsi:schemaLocation", "cc");
    rootElement.setAttribute("xmlns:nomina12", "cc");
    rootElement.setAttribute("xmlns:xsi", "cc");
    rootElement.setAttribute("xmlns:cfdi", "cc");
    
    Element emisorElement = doc.createElement("cfdi:Emisor");
    emisorElement.setAttribute("RegimenFiscal", "cc");
    emisorElement.setAttribute("Rfc", "cc");
    emisorElement.setAttribute("Nombre", "cc");
    rootElement.appendChild(emisorElement);
    
    Element receptorElement = doc.createElement("cfdi:Receptor");
    receptorElement.setAttribute("Rfc", "cc");
    receptorElement.setAttribute("Nombre", "cc");
    receptorElement.setAttribute("UsoCFDI", "cc");
    rootElement.appendChild(receptorElement);
    
    Element conceptosElement = doc.createElement("cfdi:Conceptos");
    rootElement.appendChild(conceptosElement);
    int i=0;//Cambiar por un while rs.next();
    while(i<2){
        Element conceptoElement=doc.createElement("cfdi:Concepto");
        conceptoElement.setAttribute("Descuento", "cc");
        conceptoElement.setAttribute("Importe", "cc");
        conceptoElement.setAttribute("ValorUnitario", "cc");
        conceptoElement.setAttribute("ClaveUnidad", "cc");
        conceptoElement.setAttribute("Descripcion", "cc");
        conceptoElement.setAttribute("Cantidad", "cc");
        conceptoElement.setAttribute("ClaveProdServ", "cc");
        conceptosElement.appendChild(conceptoElement);
        i++;
    }
    Element complementoElement=doc.createElement("cfdi:Complemento");
    rootElement.appendChild(complementoElement);
    Element nominaElement=doc.createElement("nomina12:Nomina");
    nominaElement.setAttribute("Version", "cc");
    nominaElement.setAttribute("TotalOtrosPagos", "cc");
    nominaElement.setAttribute("TotalDeducciones", "cc");
    nominaElement.setAttribute("TotalPercepciones", "cc");
    nominaElement.setAttribute("NumDiasPagados", "cc");
    nominaElement.setAttribute("FechaFinalPago", "cc");
    nominaElement.setAttribute("FechaInicialPago", "cc");
    nominaElement.setAttribute("FechaPago", "cc");
    nominaElement.setAttribute("TipoNomina", "cc");
    complementoElement.appendChild(nominaElement);
    Element nominaEElement=doc.createElement("nomina12:Emisor");
    nominaEElement.setAttribute("RegistroPatronal", "cc");
    nominaElement.appendChild(nominaEElement);
    Element EntidadSNCFElement=doc.createElement("nomina12:EntidadSNCF");
    EntidadSNCFElement.setAttribute("OrigenRecurso", "cc");
    nominaEElement.appendChild(EntidadSNCFElement);
    Element nominaRElement=doc.createElement("nomina12:Receptor");
    nominaRElement.setAttribute("SalarioDiarioIntegrado", "cc");
    nominaRElement.setAttribute("SalarioBaseCotApor", "cc");
    nominaRElement.setAttribute("Antigüedad", "cc");
    nominaRElement.setAttribute("Sindicalizado", "cc");
    nominaRElement.setAttribute("Puesto", "cc");
    nominaRElement.setAttribute("ClaveEntFed", "cc");
    nominaRElement.setAttribute("RiesgoPuesto", "cc");
    nominaRElement.setAttribute("FechaInicioRelLaboral", "cc");
    nominaRElement.setAttribute("NumSeguridadSocial", "cc");
    nominaRElement.setAttribute("PeriodicidadPago", "cc");
    nominaRElement.setAttribute("NumEmpleado", "cc");
    nominaRElement.setAttribute("Curp", "cc");
    nominaRElement.setAttribute("TipoRegimen", "cc");
    nominaRElement.setAttribute("TipoJornada", "cc");
    nominaRElement.setAttribute("TipoContrato", "cc");
    nominaElement.appendChild(nominaRElement);
    Element percepcionesElement=doc.createElement("nomina12:Percepciones");
    percepcionesElement.setAttribute("TotalSueldos", "cc");
    percepcionesElement.setAttribute("TotalExento", "cc");
    percepcionesElement.setAttribute("TotalGravado", "cc");
    nominaElement.appendChild(percepcionesElement);
    i=0;//Cambiar por while rs.next()
    while(i<5){
        Element percepcionElement=doc.createElement("nomina12:Percepcion");
        percepcionElement.setAttribute("ImporteExento", "cc");
        percepcionElement.setAttribute("ImporteGravado", "cc");
        percepcionElement.setAttribute("Concepto", "cc");
        percepcionElement.setAttribute("Clave", "cc");
        percepcionElement.setAttribute("TipoPercepcion", "cc");
        percepcionesElement.appendChild(percepcionElement);
        i++;
    }
    if(true){//Cambiar por condición para ver si hay deducciones o no
        Element deduccionesElement=doc.createElement("nomina12:Deducciones");
        deduccionesElement.setAttribute("TotalOtrasDeducciones", "cc");
        deduccionesElement.setAttribute("TotalImpuestosRetenidos", "cc");
        nominaElement.appendChild(deduccionesElement);
        i=0;
        while(i<5){
            Element deduccionElement=doc.createElement("nomina12:Deduccion");
            deduccionElement.setAttribute("Importe", "cc");
            deduccionElement.setAttribute("Concepto", "cc");
            deduccionElement.setAttribute("Clave", "cc");
            deduccionElement.setAttribute("TipoDeduccion", "cc");
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
}
