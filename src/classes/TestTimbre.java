/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import com.solucionfactible.cfdi.ws.timbrado.xsd.CFDICertificacion;
import com.solucionfactible.cfdi.ws.timbrado.xsd.CFDIResultadoCertificacion;
import com.solucionfactible.ws.timbrado.client.Timbrado;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.ssl.PKCS8Key;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Jorge
 */
public class TestTimbre {
    public static void main(String args[]) throws ServiceException, IOException, TransformerConfigurationException, TransformerException, NoSuchAlgorithmException, GeneralSecurityException, ParserConfigurationException, SAXException{
        //Empieza cadena original
                // cargar el archivo XSLT
        File xslt = new File("src/resources/cadenaoriginal_3_3.xslt");
        StreamSource sourceXSL = new StreamSource(xslt);
        String rutaXml="C:\\ruta\\PE212043F6.xml";
        // cargar el CFDI
        File cfdi = new File(rutaXml);
        StreamSource sourceXML = new StreamSource(cfdi);
 
        // crear el procesador XSLT que nos ayudará a generar la cadena original
        // con base en las reglas del archivo XSLT
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(sourceXSL);
 
        // aplicar las reglas del XSLT con los datos del CFDI y escribir el resultado en output
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        transformer.transform(sourceXML, new StreamResult(bos));

        byte []cadenaOriginal=bos.toByteArray();

        File keyFile = new File("src/resources/firma/Claveprivada_FIEL_IXS7607092R5_20190614_164756.key");
        InputStream keyFileInput = new FileInputStream(keyFile);
        String pass="12345678a";
        PKCS8Key pkcs8 = new PKCS8Key(keyFileInput, pass.toCharArray());
        KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8Encoded = new PKCS8EncodedKeySpec(pkcs8.getDecryptedBytes());
        PrivateKey privateKey = privateKeyFactory.generatePrivate(pkcs8Encoded);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(cadenaOriginal);
        //System.out.println(DatatypeConverter.printBase64Binary(cadenaOriginal));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try (InputStream is = new FileInputStream(rutaXml)) {

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(is);
        Node comprobante = doc.getElementsByTagName("cfdi:Comprobante").item(0);
        NamedNodeMap attribute = comprobante.getAttributes();
        Node nodeAttr = attribute.getNamedItem("Sello");
        nodeAttr.setTextContent("");
        System.out.println(nodeAttr.toString());
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
 
            Transformer transformer2 = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
 
            StreamResult streamResult = new StreamResult(new File(rutaXml));
            transformer2.transform(domSource, streamResult);
        
        
        }

        //Termina cadena original




        
        
        
        
        
        /*
        String user = "testing@solucionfactible.com";
    String pass = "timbrado.SF.16672";
    String rutaXML = "C:\\ruta\\PE212043F6.xml";
    boolean produccion = false;
    Timbrado timbrado = new Timbrado();
    CFDICertificacion cert = timbrado.timbrar(user, pass, rutaXML, produccion);

    System.out.println("WS Producción: " + (produccion ? "Si" : "No"));
    System.out.println("Status:  " + cert.getStatus());
    System.out.println("Mensaje: " + cert.getMensaje());
    CFDIResultadoCertificacion[] resultados = cert.getResultados();
    if (resultados != null) {
        for (CFDIResultadoCertificacion r : resultados) {
            if (r != null) {
                int rStatus = r.getStatus();
                System.out.println(String.format("[%d] %s", rStatus, r.getMensaje()));
                //Operación creada exitosamente o previamente ya creado
                if (rStatus == 200) { 
                    System.out.println(String.format("CFDI timbrado con folio: %s", r.getUuid()));
                    System.out.println(String.format("Certificado SAT: %s", r.getSelloSAT()));
                    System.out.println("Cadena original del Timbre Fiscal digital: " + r.getCadenaOriginal());
                    System.out.println("XML de CFDI con Timbre Fiscal Digital:");
                    //El CFDI con el TimbreFiscalDigital ya incluido
                    System.out.println(new String(r.getCfdiTimbrado()));
                    //Se usa la libreria de apache commons codec.
                    String qrCodeB64 = new String(org.apache.commons.codec.binary.Base64.encodeBase64(r.getQrCode()));
                    System.out.println("QRCode en base64: " + qrCodeB64);
                }
                System.out.println();
            }
        }
    }*/
    }
    
}
