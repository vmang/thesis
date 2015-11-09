/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf;

import Data.Stammdaten;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

/**
 *
 * @author vima
 */
@ManagedBean
public class PdfBuilder {

    private static final String K_EINZELINSTITUT = "Kontrollkästchen1";
    private static final String K_KONSOLIDIERT = "Kontrollkästchen2";
    private static final String K_KWG14 = "Kontrollkästchen3";
    private static final String EINREICHUNG = "a3";
    private static final String MELDETERMIN = "a3a";
    private static final String KREDITGEBER_Ü_NAME1 = "a4";
    private static final String KREDITGEBER_Ü_NAME2 = "a5";
    private static final String KREDITGEBER_Ü_ID = "a6";
    private static final String KREDITGEBER_N_NAME1 = "a7";
    private static final String KREDITGEBER_N_NAME2 = "a8";
    private static final String KREDITGEBER_N_ID = "a9";
    private static final String KREDITNEHMER1 = "a10";
    private static final String KREDITNEHMER2 = "a11";
    private static final String KREDITNEHMER_ID = "a12";
    private static final String PLZ = "a13";
    private static final String SITZ = "a14";
    private static final String STAAT = "a15";
    private static final String ISO = "a16";
    private static final String WIRTSCHAFTSZWEIG = "a17";
    private static final String STEUERNUMMER = "a17a";
    private static final String REGISTEREINTRAGUNG_ART_NUMMER = "a18";
    private static final String REGISTEREINTRAGUNG_ORT = "a19";
    private static final String BUNDESSTAAT = "a20";
    private static final String GEBURTSDATUM = "a21";
    private static final String BERUF = "a22";
    private static final String ISIN = "a23";
    private static final String LEI = "a23a";
    private static final String KREDITNEHMEREINHEIT_NAME1 = "a24";
    private static final String KREDITNEHMEREINHEIT_ID = "a25";
    private static final String KREDITNEHMEREINHEIT_NAME2 = "a26";
    private static final String BEGRÜNDUNG_ZUORDNUNG = "a27";
    private static final String REFERENZSCHULDNER_NAME = "a28";
    private static final String REFERENZSCHULDNER_ID = "a29";
    private static final String KREDITNEHMEREINHEIT_BEGRÜNDUNG1 = "a31";
    private static final String KREDITNEHMEREINHEIT_BEGRÜNDUNG2 = "a32";
    private static final String KREDITNEHMEREINHEIT_BEGRÜNDUNG3 = "a33";
    private static final String KREDITNEHMEREINHEIT_BEGRÜNDUNG4 = "a34";
    private static final String LAUFENDE_NUMMER = "a41";
    private static final String ZUSATZANGABEN1 = "a46";
    private static final String ZUSATZANGABEN2 = "a47";
    private static final String SACHBEARBEITER = "a48";
    private static final String TELEFON = "a49";
    private static final String EMAIL = "a50";

    public void build(Stammdaten sta) {
        try (
                InputStream is = getClass().getResourceAsStream("sta.pdf");
                
                //get pdfobject
                PDDocument pdfTemplate = PDDocument.load(is)) {
                PDDocumentCatalog docCatalog = pdfTemplate.getDocumentCatalog();
                PDAcroForm acroForm = docCatalog.getAcroForm();
                //Set Data
                /*
                acroForm.getField(PLZ).setValue(sta.getPostleitzahl());
                acroForm.getField(SITZ).setValue(sta.getSitz());
                acroForm.getField(STAAT).setValue(sta.getStaat());
                acroForm.getField(ISO).setValue(sta.getISO());
                acroForm.getField(BERUF).setValue(sta.getBeruf());
                acroForm.getField(GEBURTSDATUM).setValue(sta.getGeburtsdatum());
                acroForm.getField(KREDITNEHMER1).setValue(sta.getKreditnehmer());
                        */
                acroForm.getField(STAAT).setValue("test");
                // Generate new File
                // TODO: change uuid to timestamp
                File saveFile = new File("STA" + UUID.randomUUID() + ".pdf");

                if (saveFile.createNewFile()) {
                    System.out.println("File is created!");
                } else {
                    System.out.println("File already exists.");
                }
                // Save edited file
                pdfTemplate.save(saveFile);
                PDStream ps=new PDStream(pdfTemplate);
                InputStream finalPDF=ps.createInputStream();;
        } catch (IOException | COSVisitorException ex) {
            Logger.getLogger(PdfBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}
