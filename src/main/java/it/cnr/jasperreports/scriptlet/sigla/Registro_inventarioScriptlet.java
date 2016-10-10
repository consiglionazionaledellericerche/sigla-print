package it.cnr.jasperreports.scriptlet.sigla;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRScriptletException;


public class Registro_inventarioScriptlet extends JRDefaultScriptlet {
    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public Registro_inventarioScriptlet() {
	}
	
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue(JRParameter.REPORT_CONNECTION);
		java.sql.CallableStatement cs = null; 
		try{	        
			cs = conn.prepareCall("{call INS_TMP_REGISTRO_INV_QUOTE(?,?,?,?,?,?,?,?,?,?)}");
			cs.setObject(1, (java.lang.String)getParameterValue("uo"));
			cs.setObject(2,(java.lang.String)getParameterValue("cds"));
			cs.setObject(3,(java.lang.String)new java.text.SimpleDateFormat("dd/MM/yyyy").format((java.util.Date)getParameterValue("data_da")));
			cs.setObject(4,(java.lang.String)new java.text.SimpleDateFormat("dd/MM/yyyy").format((java.util.Date)getParameterValue("data_a")));
			cs.setObject(5,(java.lang.String)getParameterValue("categoria"));
			cs.setObject(6,(java.lang.String)getParameterValue("gruppo"));
			cs.setObject(7,(java.lang.Long)getParameterValue("da_codice_bene"));
			cs.setObject(8,(java.lang.Long)getParameterValue("a_codice_bene"));
			cs.setObject(9,(java.lang.String)getParameterValue("ds_tipo_carico_scarico"));
			cs.setObject(10,(java.lang.String)getParameterValue("tipo"));
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