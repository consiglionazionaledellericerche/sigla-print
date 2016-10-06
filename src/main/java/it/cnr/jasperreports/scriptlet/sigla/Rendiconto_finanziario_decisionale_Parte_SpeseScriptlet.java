package it.cnr.jasperreports.scriptlet.sigla;
import it.cnr.jasperreports.scriptlet.SIGLAScriptlet;
import net.sf.jasperreports.engine.*;


public class Rendiconto_finanziario_decisionale_Parte_SpeseScriptlet extends SIGLAScriptlet {
	    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public Rendiconto_finanziario_decisionale_Parte_SpeseScriptlet() {
	}
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue("REPORT_CONNECTION");
		java.sql.CallableStatement cs = null; 
		try{	        	
			cs = conn.prepareCall("{call PRT_REND_FIN_ENT_SPE_DEC(?,?,?)}");
			cs.setObject(1,(java.lang.Integer)getParameterValue("esercizio"));
			cs.setObject(2,"S");
			if (getParameterValue("ufficiale").equals("Y"))
				cs.setObject(3,java.lang.Boolean.TRUE);
			else
				cs.setObject(3,java.lang.Boolean.FALSE);
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