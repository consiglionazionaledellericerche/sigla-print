package it.cnr.jasperreports.scriptlet.sigla;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRScriptletException;


public class Stampa_pdg_rendicontoScriptlet extends JRDefaultScriptlet {
    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public Stampa_pdg_rendicontoScriptlet() {
	}
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue(JRParameter.REPORT_CONNECTION);
		java.sql.CallableStatement cs = null; 
		try{	        
			cs = conn.prepareCall("{call PRC_LOAD_TABLE_STAMPA_RENDIC(?,?,?,?,?,?)}");
			cs.setObject(1,(java.lang.Integer)getParameterValue("P_ESERCIZIO") );
			cs.setObject(2,(java.lang.String)getParameterValue("P_TIPO"));
			cs.setObject(3,(java.lang.Integer)getParameterValue("P_NUM_LIV"));
			cs.setObject(4,(java.lang.String)getParameterValue("P_TIPO_AGGREGAZIONE"));
			cs.setObject(5,(java.lang.String)getParameterValue("P_TIPO_RENDICONTO"));
			cs.setObject(6,(java.lang.String)getParameterValue("P_ORIGINE"));
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