/*
 * Copyright (C) 2020  Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.cnr.jasperreports.scriptlet.sigla;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRScriptletException;


public class StatopatrimonialericlassificatoinvScriptlet extends JRDefaultScriptlet {
    private java.lang.String id_report;

    /**
     * Creates a new instance of JRIreportDefaultScriptlet
     */
    public StatopatrimonialericlassificatoinvScriptlet() {
    }

    public void afterDetailEval() throws JRScriptletException {
        setVariableValue("id_rep", id_report);
        super.afterDetailEval();
    }

    public void beforeReportInit() throws JRScriptletException {
        java.sql.Connection conn = (java.sql.Connection) getParameterValue(JRParameter.REPORT_CONNECTION);
        java.sql.CallableStatement cs = null;
        try {
            cs = conn.prepareCall("{ ? = call func_PRT_S_SP_RICLASSIFICATO(?,?,?,?,?)}");
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            cs.setObject(2, getParameterValue("ATTPAS"));
            cs.setObject(3, getParameterValue("IST_COMM"));
            cs.setObject(4, getParameterValue("inEs"));
            cs.setObject(5, getParameterValue("CDS"));
            cs.setObject(6, getParameterValue("uo"));
            cs.executeQuery();
            id_report = (java.lang.String) cs.getObject(1);
        } catch (Throwable e) {
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