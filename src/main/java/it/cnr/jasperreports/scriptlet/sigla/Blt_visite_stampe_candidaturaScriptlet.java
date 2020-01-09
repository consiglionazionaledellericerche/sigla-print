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

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Blt_visite_stampe_candidaturaScriptlet extends JRDefaultScriptlet {
    static final long ONE_HOUR = 60 * 60 * 1000L;

    /**
     * Creates a new instance of JRIreportDefaultScriptlet
     */
    public Blt_visite_stampe_candidaturaScriptlet() {
    }

    public static long getGiorniVisita(Date date1, Date date2) {
        return daysBetweenDates(date1, date2) + 1;
    }

    public static long daysBetweenDates(Date date1, Date date2) {
        Calendar earlierDate = new GregorianCalendar();
        Calendar laterDate = new GregorianCalendar();

        GregorianCalendar data_da = (GregorianCalendar) GregorianCalendar.getInstance();
        data_da.setTime(date1);
        GregorianCalendar data_a = (GregorianCalendar) GregorianCalendar.getInstance();
        data_a.setTime(date2);

        int day1 = data_da.get(java.util.GregorianCalendar.DAY_OF_MONTH);
        int month1 = data_da.get(java.util.GregorianCalendar.MONTH);
        int year1 = data_da.get(java.util.GregorianCalendar.YEAR);

        int day2 = data_a.get(java.util.GregorianCalendar.DAY_OF_MONTH);
        int month2 = data_a.get(java.util.GregorianCalendar.MONTH);
        int year2 = data_a.get(java.util.GregorianCalendar.YEAR);

        earlierDate.set(year1, month1, day1, 0, 0, 0);
        laterDate.set(year2, month2, day2, 0, 0, 0);

        long duration = laterDate.getTime().getTime() - earlierDate.getTime().getTime();

        long nDays = (duration + ONE_HOUR) / (24 * ONE_HOUR);// System.out.println("difference in days: " + nDays);
        return nDays;
    }

    public void afterReportInit() throws JRScriptletException {
        String flagToUpdate = (String) getParameterValue("flag_to_update");

        if (flagToUpdate != null && !flagToUpdate.equals("")) {
            java.sql.Connection conn = (java.sql.Connection) getParameterValue("REPORT_CONNECTION");
            java.sql.Statement cs = null, stmt = null;

            String selBltVisite = "SELECT " + flagToUpdate + ", PG_VER_REC FROM BLT_VISITE " +
                    "WHERE CD_ACCORDO = '" + getParameterValue("cd_accordo") + "'" +
                    " AND  CD_PROGETTO = '" + getParameterValue("cd_progetto") + "'" +
                    " AND  CD_TERZO = " + getParameterValue("cd_terzo") +
                    " AND  PG_AUTORIZZAZIONE = " + getParameterValue("pg_autorizzazione") +
                    " AND  PG_VISITA = " + getParameterValue("pg_visita");

            String updBltVisite = "UPDATE BLT_VISITE " +
                    "SET " + flagToUpdate + "='Y', " +
                    "PG_VER_REC = PG_VER_REC + 1 " +
                    "WHERE CD_ACCORDO = '" + getParameterValue("cd_accordo") + "'" +
                    " AND  CD_PROGETTO = '" + getParameterValue("cd_progetto") + "'" +
                    " AND  CD_TERZO = " + getParameterValue("cd_terzo") +
                    " AND  PG_AUTORIZZAZIONE = " + getParameterValue("pg_autorizzazione") +
                    " AND  PG_VISITA = " + getParameterValue("pg_visita") +
                    " AND  PG_VER_REC = " + getParameterValue("pg_ver_rec");

            try {
                stmt = conn.createStatement();
                String flag = null;
                Long pgVerRec = null;
                int contaRec = 0;

                ResultSet rs = stmt.executeQuery(selBltVisite);
                while (rs.next()) {
                    contaRec++;
                    flag = rs.getString(flagToUpdate);
                    pgVerRec = rs.getLong("PG_VER_REC");
                }

                if (contaRec == 0)
                    throw new JRScriptletException("1 - Stampa non eseguita. I dati della visita risultano essere stati modificati prima della esecuzione della stampa!!" + updBltVisite);
                if (contaRec > 1)
                    throw new JRScriptletException("2 - Stampa non eseguita. Errore di too_many_rows nella ricerca dei dati!!");
                if (contaRec == 1 && flag.equals("N")) {
                    if (!pgVerRec.equals(getParameterValue("pg_ver_rec")))
                        throw new JRScriptletException("3 - Stampa non eseguita. I dati della visita risultano essere stati modificati prima della esecuzione della stampa!!" + updBltVisite);

                    cs = conn.createStatement();
                    int numUpdate = cs.executeUpdate(updBltVisite);
                    if (numUpdate == 0)
                        throw new JRScriptletException("4 - Stampa non eseguita. I dati della visita risultano essere stati modificati prima della esecuzione della stampa!!" + updBltVisite);
                }
            } catch (Throwable e) {
                throw new JRScriptletException(e.getMessage());
            } finally {
                if (cs != null)
                    try {
                        cs.close();
                    } catch (java.sql.SQLException e1) {
                        throw new JRScriptletException(e1.getMessage());
                    }
                if (stmt != null)
                    try {
                        stmt.close();
                    } catch (java.sql.SQLException e1) {
                        throw new JRScriptletException(e1.getMessage());
                    }
            }
        }
    }
}