package it.cnr.jasperreports.scriptlet.sigla;
import it.cnr.jasperreports.scriptlet.SIGLAScriptlet;
import net.sf.jasperreports.engine.*;


public class Vpg_missioneScriptlet extends SIGLAScriptlet {
	    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public Vpg_missioneScriptlet() {
	}
	
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue("REPORT_CONNECTION");
		java.sql.CallableStatement cs = null; 
		try{	        	
			cs = conn.prepareCall("{call SPG_MISSIONE(?,?,?,?,?,?)}");
			cs.setObject(1,(java.lang.String)getParameterValue("aCd_cds"));
			cs.setObject(2,(java.lang.String)getParameterValue("aCd_uo"));
			cs.setObject(3,(java.lang.Integer)getParameterValue("aEs"));
			cs.setObject(4,(java.lang.Long)getParameterValue("aPg_da"));
			cs.setObject(5,(java.lang.Long)getParameterValue("aPg_a"));
			cs.setObject(6,(java.lang.String)getParameterValue("aCd_terzo"));
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