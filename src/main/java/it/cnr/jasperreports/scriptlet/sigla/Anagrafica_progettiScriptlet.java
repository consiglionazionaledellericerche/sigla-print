package it.cnr.jasperreports.scriptlet.sigla;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

public class Anagrafica_progettiScriptlet extends JRDefaultScriptlet{
    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public Anagrafica_progettiScriptlet() {	
	}
	
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue("REPORT_CONNECTION");
		java.sql.CallableStatement cs = null; 
		try{
			cs = conn.prepareCall("{call S_ANA_PROGETTI(?,?,?,?,?,?)}");
			cs.setObject(1,(java.lang.String)getParameterValue("uo"));
			cs.setObject(2,(java.lang.String)getParameterValue("progetto"));
			cs.setObject(3,(java.lang.Integer)getParameterValue("livello"));
			cs.setObject(4,(java.lang.String)getParameterValue("stato"));
			cs.setObject(5,(java.lang.Integer)getParameterValue("esercizio"));
			cs.setObject(6,(java.lang.String)getParameterValue("fase"));
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