package it.cnr.jasperreports.scriptlet.sigla;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;


public class Preventivo_finanziario_decisionale_Parte_SpeseScriptlet extends JRDefaultScriptlet {
    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public Preventivo_finanziario_decisionale_Parte_SpeseScriptlet() {
	}

	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue("REPORT_CONNECTION");
		java.sql.CallableStatement cs = null; 
		try{	        
			cs = conn.prepareCall("{call PRT_PREV_FIN_ENT_SPE_DEC(?,?)}");
			cs.setObject(1,(java.lang.Integer)getParameterValue("esercizio"));
			cs.setObject(2,"S");
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