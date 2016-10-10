package it.cnr.jasperreports.scriptlet.sigla;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRScriptletException;


public class Vpg_partitario_compensiScriptlet extends JRDefaultScriptlet {    
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public Vpg_partitario_compensiScriptlet() {
	}
	
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue(JRParameter.REPORT_CONNECTION);
		java.sql.CallableStatement cs = null; 
		try{	        
			cs = conn.prepareCall("{call SPG_PARTITARIO_COMPENSI(?,?,?,?)}");
			cs.setObject(1,(java.lang.String)getParameterValue("CDS") );
			cs.setObject(2,(java.lang.String)getParameterValue("UO"));
			cs.setObject(3,(java.lang.Integer)getParameterValue("Esercizio"));
			cs.setObject(4,(java.lang.Integer)getParameterValue("Cd_Terzo"));
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