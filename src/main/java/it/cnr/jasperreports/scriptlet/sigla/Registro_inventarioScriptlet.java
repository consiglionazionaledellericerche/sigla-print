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


public class Registro_inventarioScriptlet extends JRDefaultScriptlet {

    /**
     * Creates a new instance of JRIreportDefaultScriptlet
     */
    public Registro_inventarioScriptlet() {
    }

    public void beforeReportInit() throws JRScriptletException {
        java.sql.Connection conn = (java.sql.Connection) getParameterValue(JRParameter.REPORT_CONNECTION);
        java.sql.CallableStatement cs = null;
        try {
            cs = conn.prepareCall("{call INS_TMP_REGISTRO_INV_QUOTE(?,?,?,?,?,?,?,?,?,?)}");
            cs.setObject(1, getParameterValue("uo"));
            cs.setObject(2, getParameterValue("cds"));
            cs.setObject(3, new java.text.SimpleDateFormat("dd/MM/yyyy").format((java.util.Date) getParameterValue("data_da")));
            cs.setObject(4, new java.text.SimpleDateFormat("dd/MM/yyyy").format((java.util.Date) getParameterValue("data_a")));
            cs.setObject(5, getParameterValue("categoria"));
            cs.setObject(6, getParameterValue("gruppo"));
            cs.setObject(7, getParameterValue("da_codice_bene"));
            cs.setObject(8, getParameterValue("a_codice_bene"));
            cs.setObject(9, getParameterValue("ds_tipo_carico_scarico"));
            cs.setObject(10, getParameterValue("tipo"));
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