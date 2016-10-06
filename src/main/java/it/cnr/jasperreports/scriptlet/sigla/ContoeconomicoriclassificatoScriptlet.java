package it.cnr.jasperreports.scriptlet.sigla;
import it.cnr.jasperreports.scriptlet.SIGLAScriptlet;
import net.sf.jasperreports.engine.*;

public class ContoeconomicoriclassificatoScriptlet extends SIGLAScriptlet {
    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public ContoeconomicoriclassificatoScriptlet() {
	}
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue(JRParameter.REPORT_CONNECTION);
		java.sql.CallableStatement cs = null; 
		try{
	        
			cs = conn.prepareCall("{call PRT_S_CE_RICLASSIFICATO_J(?,?,?,?,?)}");
			cs.setObject(1, (java.lang.String)getParameterValue("IST_COMM"));
			cs.setObject(2,(java.lang.Integer)getParameterValue("inEs"));
			cs.setObject(3,(java.lang.String)getParameterValue("CDS"));
			cs.setObject(4,(java.lang.String)getParameterValue("uo"));
			cs.setObject(5,(java.lang.String)getParameterValue("dettaglioConti"));
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