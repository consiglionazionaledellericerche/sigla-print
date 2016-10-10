package it.cnr.jasperreports.scriptlet.sigla;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRScriptletException;


public class ContoeconomicoriclassificatoinvScriptlet extends JRDefaultScriptlet {
	private java.lang.String id_report;
	/** Creates a new instance of JRIreportDefaultScriptlet */
	public ContoeconomicoriclassificatoinvScriptlet() {
	}
	
	public void afterDetailEval() throws JRScriptletException{
		setVariableValue("id_rep",id_report);	
		super.afterDetailEval();
	}
	
	public void beforeReportInit() throws JRScriptletException{
		java.sql.Connection conn = (java.sql.Connection)getParameterValue(JRParameter.REPORT_CONNECTION);
		java.sql.CallableStatement cs = null; 	
		try{
			cs = conn.prepareCall("{ ? = call func_PRT_S_CE_RICLASSIFICATO_J(?,?,?,?,?)}");
			cs.registerOutParameter(1,java.sql.Types.VARCHAR);
			cs.setObject(2, (java.lang.String)getParameterValue("IST_COMM"));
			cs.setObject(3,(java.lang.Integer)getParameterValue("inEs"));
			cs.setObject(4,(java.lang.String)getParameterValue("CDS"));
			cs.setObject(5,(java.lang.String)getParameterValue("uo"));
			cs.setObject(6,(java.lang.String)getParameterValue("dettaglioConti"));
			cs.executeQuery();
			id_report = (java.lang.String)cs.getObject(1);
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