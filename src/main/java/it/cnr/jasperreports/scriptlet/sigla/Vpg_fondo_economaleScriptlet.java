package it.cnr.jasperreports.scriptlet.sigla;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRScriptletException;


public class Vpg_fondo_economaleScriptlet extends JRDefaultScriptlet {
	    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public Vpg_fondo_economaleScriptlet() {
	}
	public void beforeReportInit() throws JRScriptletException{
	java.sql.Connection conn = (java.sql.Connection)getParameterValue(JRParameter.REPORT_CONNECTION);
		java.sql.CallableStatement cs = null; 
		try{	        
			cs = conn.prepareCall("{call SPG_FONDO_ECONOMALE(?,?,?,?,?,?,?)}");
			cs.setObject(1,(java.lang.String)getParameterValue("CDS") );
			cs.setObject(2,(java.lang.String)getParameterValue("UO"));
			cs.setObject(3,(java.lang.Integer)getParameterValue("Esercizio"));
			cs.setObject(4,(java.lang.String)getParameterValue("Cd_codice_fondo"));
			cs.setObject(5,(java.lang.String)getParameterValue("DaData"));
			cs.setObject(6,(java.lang.String)getParameterValue("AData"));
			cs.setObject(7,(java.lang.String)getParameterValue("Utcr"));
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