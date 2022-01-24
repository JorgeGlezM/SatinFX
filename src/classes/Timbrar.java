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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.ssl.Base64;
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
public class Timbrar {
    static String uuid,fecha,sellosat,sellocfdi, qrCodeB64,xml="";
    MySQL mysql=new MySQL();

static PreparedStatement pstmtTimbrados,pstmtXml;
    public Timbrar() {
        mysql.conectar();
    }
    public String timbrar(CalcularXML c) throws ServiceException, IOException, TransformerConfigurationException, TransformerException, NoSuchAlgorithmException, GeneralSecurityException, ParserConfigurationException, SAXException{
        String error="";
        //Empieza cadena original
                // cargar el archivo XSLT
        Timbrar t=new Timbrar();
        String idNomina=c.getId();
        String password=DatosTimbrado.getPassword_key();
        String rutaXml=c.getRutaXML();
        String rutaKey=DatosTimbrado.getArchivo_key();//Cambiar por datos timbrado
        File xslt = new File("src/resources/cadenaoriginal_3_3.xslt");
        StreamSource sourceXSL = new StreamSource(xslt);
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
        //Termina cadena original

        
        File keyFile = new File(rutaKey);
        InputStream keyFileInput = new FileInputStream(keyFile);
        PKCS8Key pkcs8 = new PKCS8Key(keyFileInput, password.toCharArray());
        KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8Encoded = new PKCS8EncodedKeySpec(pkcs8.getDecryptedBytes());
        PrivateKey privateKey = privateKeyFactory.generatePrivate(pkcs8Encoded);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(cadenaOriginal);
        sellocfdi = new String(Base64.encodeBase64(signature.sign()), "UTF-8");
        System.out.println("------------------ Cadena Original: " + bos.toString());
        System.out.println("---------Firma: " + sellocfdi);
 

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //Agregamos sello al comprobante
        try (InputStream is = new FileInputStream(rutaXml)) {

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(is);
        Node comprobante = doc.getElementsByTagName("cfdi:Comprobante").item(0);
        NamedNodeMap attribute = comprobante.getAttributes();
        Node nodeAttr = attribute.getNamedItem("Sello");
        nodeAttr.setTextContent(sellocfdi);
        System.out.println(nodeAttr.toString());
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
 
            Transformer transformer2 = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
 
            StreamResult streamResult = new StreamResult(new File(rutaXml));
            transformer2.transform(domSource, streamResult);

        }
// TIMBRADO
    String user = "testing-cfdi33@bpm920113pb3.sf";
    String pass = "4qWNLLtZTZ8ZzEDdG3YYyjls";
    boolean produccion = false;
    Timbrado timbrado = new Timbrado();
    CFDICertificacion cert = timbrado.timbrar(user, pass, rutaXml, produccion);

    System.out.println("WS Producción: " + (produccion ? "Si" : "No"));
    System.out.println("Status:  " + cert.getStatus());
    System.out.println("Mensaje: " + cert.getMensaje());
    CFDIResultadoCertificacion[] resultados = cert.getResultados();
    String msg;
    if (resultados != null) {
        for (CFDIResultadoCertificacion r : resultados) {
            if (r != null) {
                msg=String.format("[%d] %s", r.getStatus(), r.getMensaje());
                int rStatus = r.getStatus();
                System.out.println(String.format("[%d] %s", rStatus, r.getMensaje()));
                //Operación creada exitosamente o previamente ya creado
                if (rStatus == 200) { 
                    System.out.println(String.format("CFDI timbrado con folio: %s", r.getUuid()));
                    //El CFDI con el TimbreFiscalDigital ya incluido
                    xml=new String(r.getCfdiTimbrado());
                    //Se usa la libreria de apache commons codec.
                    qrCodeB64 = new String(org.apache.commons.codec.binary.Base64.encodeBase64(r.getQrCode()));
                    System.out.println("QR:");
                    System.out.println(qrCodeB64);
                    uuid=r.getUuid();
                    Calendar cal=r.getFechaTimbrado();
                    Date date = cal.getTime();             
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    fecha = format1.format(date);   
                    sellosat=r.getSelloSAT();
                        try {
        String update="UPDATE `satin`.`detalle_nomina` SET `uuid` = ?, `fechatimbrado` = ?, `sellosat` = ?, `sellocfdi` = ? WHERE (`id` = ?);";
        pstmtTimbrados = mysql.conn.prepareStatement(update);
        pstmtTimbrados.setString(1, uuid);
         
        System.out.println(fecha);

        pstmtTimbrados.setString(2, fecha);
        pstmtTimbrados.setString(3, sellosat);
        pstmtTimbrados.setString(4, sellocfdi);
        pstmtTimbrados.setString(5, idNomina);
        pstmtTimbrados.addBatch();
        pstmtTimbrados.executeBatch();
        

                //TERMINA TIMBRADO
    } catch (SQLException ex) {
        System.out.println(ex);
        Logger.getLogger(Timbrar.class.getName()).log(Level.SEVERE, null, ex);
    }
                }else{
                    System.out.println("no timbrable");
                    error="El detalle con folio: "+idNomina+" no pudo timbrarse. "+msg;
                    System.out.println(error);
                }
            }
        }
    }

    java.io.FileWriter fw = new java.io.FileWriter(rutaXml);
    if(!xml.equals("")||!xml.equals(null)){
            fw.write(xml);
    }
    fw.close();
    String insert="INSERT INTO `satin`.`xml`  (id_detalle,xml) "+
            "VALUES(?,?) on duplicate key UPDATE xml=?";
    try {
        pstmtXml = mysql.conn.prepareStatement(insert);
        pstmtXml.setString(1, c.id);
        File file= new File(rutaXml);
        FileInputStream inputStream= new FileInputStream(file);
        pstmtXml.setBlob(2, inputStream);
        pstmtXml.setBlob(3, inputStream);
        pstmtXml.addBatch();
        pstmtXml.executeBatch();
    } catch (SQLException ex) {
        Logger.getLogger(Timbrar.class.getName()).log(Level.SEVERE, null, ex);
    }

    return error;
    }
    public static void cancelar(String[] uuid) {
        String user = "testing-cfdi33@bpm920113pb3.sf";
        String pass = "4qWNLLtZTZ8ZzEDdG3YYyjls";
        String cer=DatosTimbrado.getArchivo_cert();
        String key=DatosTimbrado.archivo_key;
        String passwordCer=DatosTimbrado.getPassword_key();
        Timbrado timbrado = new Timbrado();
        try{
            timbrado.cancelar(user, passwordCer, uuid, cer, key, passwordCer, false);
            System.out.println("si se canceló"); 
        }catch(Exception e){
            System.out.println("no se canceló"); 

        }

    }
}
