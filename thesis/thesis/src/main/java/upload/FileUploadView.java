/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upload;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
 
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
/**
 *
 * @author vima
 */

 
@ManagedBean
public class FileUploadView {
    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CSV = "application/vnd.ms-excel";
    
    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        try {
            Parser p = new Parser(file.getInputstream());
            if(file.getContentType().contains(XLSX)){
                p.parseXSLX();
            }
            else if(file.getContentType().contains(CSV)){
                p.parseCSV();
            }
        } catch (IOException ex) {
            Logger.getLogger(FileUploadView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
