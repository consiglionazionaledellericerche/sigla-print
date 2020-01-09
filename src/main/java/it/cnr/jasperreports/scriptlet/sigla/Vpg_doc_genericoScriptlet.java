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
import net.sf.jasperreports.engine.JRScriptletException;


public class Vpg_doc_genericoScriptlet extends JRDefaultScriptlet {

    /**
     * Creates a new instance of JRIreportDefaultScriptlet
     */
    public Vpg_doc_genericoScriptlet() {
    }

    public void beforeReportInit() throws JRScriptletException {
        java.sql.Connection conn = (java.sql.Connection) getParameterValue("REPORT_CONNECTION");
        java.sql.CallableStatement cs = null;
        try {
            cs = conn.prepareCall("{call SPG_DOC_GENERICO(?,?,?,?,?,?,?,?,?,?)}");
            cs.setObject(1, getParameterValue("aCd_cds"));
            cs.setObject(2, getParameterValue("aCd_uo"));
            cs.setObject(3, getParameterValue("aEs"));
            cs.setObject(4, getParameterValue("aCd_tipo_doc_amm"));
            cs.setObject(5, getParameterValue("aPg_da"));
            cs.setObject(6, getParameterValue("aPg_a"));
            cs.setObject(7, (new java.text.SimpleDateFormat("yyyy/MM/dd")).format((java.util.Date) getParameterValue("aDt_da")));
            cs.setObject(8, (new java.text.SimpleDateFormat("yyyy/MM/dd")).format((java.util.Date) getParameterValue("aDt_a")));
            cs.setObject(9, getParameterValue("aCd_terzo"));
            cs.setObject(10, getParameterValue("acd_tdg"));
            cs.executeQuery();
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