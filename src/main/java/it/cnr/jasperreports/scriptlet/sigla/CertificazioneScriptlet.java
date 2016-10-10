package it.cnr.jasperreports.scriptlet.sigla;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;


public class CertificazioneScriptlet extends JRDefaultScriptlet {
    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public CertificazioneScriptlet() {
	}
	
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue("REPORT_CONNECTION");
		java.sql.CallableStatement cs = null; 
		try{
			cs = conn.prepareCall("{call SPG_CERTIFICAZIONE(?,?,?,?)}");
			cs.setObject(1,(java.lang.Integer)getParameterValue("esercizio"));
			cs.setObject(2,(java.lang.String)getParameterValue("ti_cert"));
			cs.setObject(3,(java.lang.String)getParameterValue("cd_anag"));
			cs.setObject(4,(java.lang.String)getParameterValue("nota"));
			cs.executeQuery();
		}catch (Throwable e) {
			throw new JRScriptletException(e.getMessage());
		} finally {
			if (cs != null) 
				try {
					cs.close();
				} catch (java.sql.SQLException e1) {
					throw new JRScriptletException(e1.getMessage());
				}
		}	
	}
}