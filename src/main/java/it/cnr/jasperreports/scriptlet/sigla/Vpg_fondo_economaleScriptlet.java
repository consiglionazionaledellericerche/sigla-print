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


public class Vpg_fondo_economaleScriptlet extends JRDefaultScriptlet {

    /**
     * Creates a new instance of JRIreportDefaultScriptlet
     */
    public Vpg_fondo_economaleScriptlet() {
    }

    public void beforeReportInit() throws JRScriptletException {
        java.sql.Connection conn = (java.sql.Connection) getParameterValue(JRParameter.REPORT_CONNECTION);
        java.sql.CallableStatement cs = null;
        try {
            cs = conn.prepareCall("{call SPG_FONDO_ECONOMALE(?,?,?,?,?,?,?)}");
            cs.setObject(1, getParameterValue("CDS"));
            cs.setObject(2, getParameterValue("UO"));
            cs.setObject(3, getParameterValue("Esercizio"));
            cs.setObject(4, getParameterValue("Cd_codice_fondo"));
            cs.setObject(5, getParameterValue("DaData"));
            cs.setObject(6, getParameterValue("AData"));
            cs.setObject(7, getParameterValue("Utcr"));
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