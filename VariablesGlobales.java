/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @web http://jc-mouse.blogspot.com/
 * @author Mouse
 */
public class VariablesGlobales {

    public VariablesGlobales() {
    }

    public static void main(String[] args) throws IOException {
       //codigoQR();
       //PDF417();
    }

//=====================================CODIFICAR Y DECODIFICAR ARCHIVOS ENVIADOS, RECIBIDOS A SUNAT=========================================
//https://coderanch.com/t/517954/Web-Services/java/access-byte-array-web-method
    public String encode(String RutaCPE) throws Exception {
        FileInputStream fis = new FileInputStream(RutaCPE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int c = 0;
        while ((c = fis.read()) != -1) {
            baos.write(c);
        }
        fis.close();
        byte[] byteReturn = baos.toByteArray();

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        OutputStream b64os = MimeUtility.encode(baos2, "base64");
        b64os.write(byteReturn);
        b64os.close();
        return baos2.toString();
    }

    public String decode(String Rptarchivo, String nombreArchivo) throws Exception {
        byte[] recibeArchivoBytes = null;
        recibeArchivoBytes = Rptarchivo.getBytes();

        ByteArrayInputStream bais = new ByteArrayInputStream(recibeArchivoBytes);
        InputStream b64is = MimeUtility.decode(bais, "base64");
        byte[] tmp = new byte[recibeArchivoBytes.length];
        int n = b64is.read(tmp);
        byte[] res = new byte[n];
        System.arraycopy(tmp, 0, res, 0, n);

        try {
            OutputStream out = new FileOutputStream(nombreArchivo + ".ZIP");
            out.write(res);
            out.close();
        } catch (Exception ex) {
            //ok = false;
            System.out.println("el  error es " + ex);
        } finally {
        }

        return "ok";
    }

    //POSIBLE SOLUCION
    //http://www.programcreek.com/java-api-examples/index.php?class=javax.xml.soap.SOAPMessage&method=setProperty
    /////////////////////////////////////COMPRIMIR DE DESCOMPRIMIR ZIP///////////////////////////////////
    private static final int BUFFER_SIZE = 1024;

    public void add_unzip(String archivoOriginal, String archivoZip) {
        //https://www.youtube.com/watch?v=niUYQus1c5I
        try {
            ZipFile archivo = new ZipFile(archivoZip);
            ArrayList lista = new ArrayList();
            lista.add(new File(archivoOriginal));
            ZipParameters parametros = new ZipParameters();
            parametros.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parametros.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            archivo.addFiles(lista, parametros);
        } catch (ZipException ex) {
            ex.printStackTrace();
        }
    }

    public void extrac_unzip(String source, String destination) {
        try {
            ZipFile zipFile = new ZipFile(source);
//            if (zipFile.isEncrypted()) {
//                zipFile.setPassword(password);
//            }
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public String valorXML(String rutaArchivo, String Nspace, String TagName) throws SAXException, IOException, ParserConfigurationException {
        String rta = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(rutaArchivo + ".XML"), "ISO-8859-1");//origen
            doc.getDocumentElement().normalize();
            if (Nspace.length() > 0) {
                rta = doc.getDocumentElement().getElementsByTagNameNS("*", TagName).item(0).getTextContent();//cbc:ResponseCode
            } else {
                rta = doc.getDocumentElement().getElementsByTagName(TagName).item(0).getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rta;
    }

    protected static void PDF417() throws FileNotFoundException, IOException {
        try {
            File outputFile = new File("D:\\CPE\\CODIGOBARRA\\10447915125-01-F001-00000001.jpg");

            BitMatrix bitMatrix;
            Writer writer = new QRCodeWriter();
            writer = new PDF417Writer();
            bitMatrix = writer.encode("", BarcodeFormat.PDF_417, 300, 200);
            MatrixToImageWriter.writeToStream(bitMatrix, "jpg", new FileOutputStream(outputFile)); 
        } catch (WriterException ex) {
        }
    }
    
    protected static void codigoQR() throws FileNotFoundException, IOException {
        try {
            File outputFile = new File("D:\\CPE\\CODIGOBARRA\\10447915125-01-F001-00000001.jpg");
            BitMatrix bitMatrix;
            Writer writer = new QRCodeWriter();
            bitMatrix = writer.encode("", BarcodeFormat.QR_CODE, 350, 350);
            MatrixToImageWriter.writeToStream(bitMatrix, "jpg", new FileOutputStream(outputFile));
        } catch (WriterException ex) {
        }
    }
}
