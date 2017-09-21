package it.cnr.jasperreports.scriptlet.sigla;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRScriptletException;


public class Stampa_situazione_progettoScriptlet extends JRDefaultScriptlet {
    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public Stampa_situazione_progettoScriptlet() {
	}
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue(JRParameter.REPORT_CONNECTION);
		java.sql.CallableStatement cs = null; 
		try{
			conn.setAutoCommit(false);
			cs = conn.prepareCall("{call PRC_LOAD_SITUAZIONE_PROGETTI(?,?,?,?,?,?,?,?,?,?)}");
			cs.setObject(1,(java.lang.Integer)getParameterValue("P_ESERCIZIO") );
			cs.setObject(2,(java.lang.Integer)getParameterValue("P_PROGETTO"));
			cs.setObject(3,(java.lang.String)getParameterValue("P_UO"));
			cs.setObject(4,(java.lang.String)getParameterValue("P_GAE"));
			cs.setObject(5,(java.lang.String)(((java.lang.Boolean)getParameterValue("P_PRINT_ANNO")).equals(Boolean.TRUE)?"S":"N"));
			cs.setObject(6,(java.lang.String)(((java.lang.Boolean)getParameterValue("P_PRINT_GAE")).equals(Boolean.TRUE)?"S":"N"));
			cs.setObject(7,(java.lang.String)(((java.lang.Boolean)getParameterValue("P_PRINT_VOCE")).equals(Boolean.TRUE)?"S":"N"));
			cs.setObject(8,(java.lang.String)(((java.lang.Boolean)getParameterValue("P_PRINT_PIANO_ECO")).equals(Boolean.TRUE)?"S":"N"));
			cs.setObject(9,(java.lang.String)(((java.lang.Boolean)getParameterValue("P_PRINT_SOLO_GAE_ATTIVE")).equals(Boolean.TRUE)?"S":"N"));
			cs.setObject(10,(java.lang.String)(((java.lang.Boolean)getParameterValue("P_PRINT_MOVIMENTAZIONE")).equals(Boolean.TRUE)?"S":"N"));
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