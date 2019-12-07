package org.eevolution.form;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.adempiere.webui.panel.ADForm;
import org.zkoss.zul.Iframe;
import org.compiere.model.MSysConfig;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class WMailClient extends ADForm{

	private static final long serialVersionUID = 9155665454645864153L;

	public WMailClient() {
		//super();	
	}

	@Override
	protected void initForm() {
		String userID = Env.getCtx().getProperty("#AD_User_ID");
		String ip = MSysConfig.getValue("QWE_URL_MAILCLIENT");
		Connection conn;
		Statement stmt;
		
		try{
			conn = DB.getConnectionRW();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			/*String urlAD = "jdbc:postgresql://localhost:5432/adempiere";
            Connection connAD = DriverManager.getConnection(urlAD,"adempiere","adempiere");
            Statement stmtRC = connAD.createStatement();*/
            String query = "SELECT emailuser, emailexchange, emailuserpw from ad_user where ad_user_id = '" + userID + "'";
            ResultSet rs = stmt.executeQuery(query);
            String user = "";
            String pw = "";
            while(rs.next()){
            	//FS 20161110 exchange utilizza il formato dominio/nomeutente quindi controllo se devo usare quel campo o la mail normale
            	if(rs.getString("emailexchange") != null) user = rs.getString("emailexchange");
            	else user = rs.getString("emailuser");
                pw = rs.getString("emailuserpw");
            }
            
            String url = "http://"+ ip + "/roundcubemail-1.2.1/index.php?_user=" + user + "&&_pass=" + pw + "&&_task=login&&_action=login";
    		Iframe mail = new Iframe(url);
    		mail.setWidth("100%");
    		mail.setHeight("100%");
    		this.appendChild(mail);	
    		
    		conn.close();
			stmt.close();
		}catch (Exception e){
			logger.log(Level.SEVERE, "Errore recupero usermail e pw", e);
		}
	}

}
