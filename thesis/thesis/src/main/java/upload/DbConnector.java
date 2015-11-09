/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upload;

import Data.Betragsdaten;
import Data.Stammdaten;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.model.DualListModel;
import pdf.PdfBuilder;

/**
 *
 * @author vima
 */
@ManagedBean(name = "dbc")
@ApplicationScoped
public class DbConnector {

    private String driver;
    private String adresse;
    private String port;
    private String dbName;
    private String username;
    private String password;

    public DualListModel<String> getColumns() {
        return columns;
    }

    public void setColumns(DualListModel<String> columns) {
        this.columns = columns;
    }
    private Connection conn;
    private DualListModel<String> tables;
    private DualListModel<String> columns;

    private void initConnection() {
        try {
            System.out.println(driver + " " + adresse + " " + port + " " + dbName + " " + username + " " + password);
            String url = "";
            Class.forName(driver);

            // Step 2: Establish the connection to the database
            if (driver.startsWith("com.mysql")) {
                url = "jdbc:mysql://";
            }
            conn = DriverManager.getConnection(url + adresse + ":" + port + "/" + dbName, username, password);
        } catch (SQLException | ClassNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error Occured while connecting to " + dbName));
            Logger.getLogger(DbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void checkCon() {
        if (conn == null) {
            initConnection();
        }
        try {
            if (conn.isValid(5)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Connected", "Succesfully connected to" + dbName));
                PreparedStatement ps = conn.prepareStatement("select * from ba");
                ResultSet executeQuery = ps.executeQuery();
                int columnCount = executeQuery.getMetaData().getColumnCount();
                System.out.println(columnCount);
                for (int i = 1; i < columnCount + 1; i++) {
                    System.out.println(executeQuery.getMetaData().getColumnName(i));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String loadTables() {
        if (conn == null) {
            initConnection();
        }
        String ret = "tables";
        List<String> tablesSource = new ArrayList<>();
        List<String> tablesTarget = new ArrayList<>();

        try {
            ResultSet rs = conn.getMetaData().getTables(null, "%", "%", null);
            while (rs.next()) {
                tablesSource.add(rs.getString("TABLE_NAME"));
            }
            if (tablesSource.isEmpty()) {
                ret = "dbcon";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Datenbank" + dbName + "enthält keine Tabellen"));
            }
            tables = new DualListModel<>(tablesSource, tablesTarget);
        } catch (SQLException ex) {
            Logger.getLogger(DbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Succesfully connected to" + dbName));
        return ret;
    }

    public String loadColumns() {
        String ret = "columns";
        try {
            List<String> columnsSource = new ArrayList<>();
            List<String> columnsTarget = new ArrayList<>();
            if (conn == null) {
                initConnection();
            }
            for (String tableName : tables.getTarget()) {
                String sql = "SELECT * FROM " + tableName;
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i < columnCount + 1; i++) {
                    columnsSource.add(rs.getMetaData().getColumnName(i));
                }
            }
            columns = new DualListModel<>(columnsSource, columnsTarget);
        } catch (SQLException ex) {
            Logger.getLogger(DbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    public String fillStammdaten(){
        String ret = "viewpdf";
        StringBuilder sb = new StringBuilder("SELECT ");
        List<Stammdaten> staList = new ArrayList<>();
        
        for(String columnName : columns.getTarget()){
            sb.append(columnName);
            sb.append(" , ");
        }
        sb.delete((sb.length()-2), sb.length());
        sb.append("FROM bestandsdaten");
        System.out.println(sb.toString());
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sb.toString());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Stammdaten sd = new Stammdaten();
                sd.setBeruf(rs.getString("Beruf"));
                sd.setGeburtsdatum(rs.getString("Geburtstag"));
                sd.setISO(rs.getString("ISO"));
                sd.setKreditnehmer("Kreditnehmer");
                sd.setSitz("Sitz");
                
                staList.add(sd);
            }
           
            PdfBuilder pb = new PdfBuilder();
            for(Stammdaten sta:staList){
                pb.build(sta);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
               
        return ret;
    }
    public void test(){
        System.out.println("test");
        PdfBuilder pdf = new PdfBuilder();
        pdf.build(null);
    }
    public String fillBetragsdaten(){
        String ret = "viewpdf";
        StringBuilder sb = new StringBuilder("SELECT ");
        List<Betragsdaten> bdList = new ArrayList<>();
        
        for(String columnName : columns.getTarget()){
            sb.append(columnName);
            sb.append(" , ");
        }
        sb.delete((sb.length()-2), sb.length());
        sb.append("FROM bestandsdaten");
        System.out.println(sb.toString());
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sb.toString());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Betragsdaten bd = new Betragsdaten();
                bd.setBeruf(rs.getString("Beruf"));
                bd.setGeburtsdatum(rs.getString("Geburtstag"));
                bd.setISO(rs.getString("ISO"));
                bd.setKreditnehmer("Kreditnehmer");
                bd.setSitz("Sitz");
                
                bdList.add(bd);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
               
        return ret;
    }

    public void setTables(DualListModel<String> tables) {
        this.tables = tables;
    }

    public DualListModel<String> getTables() {
        return tables;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
