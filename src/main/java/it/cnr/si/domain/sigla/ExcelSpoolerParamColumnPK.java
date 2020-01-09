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

package it.cnr.si.domain.sigla;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The primary key class for the EXCEL_SPOOLER_PARAM_COLUMN database table.
 */
@Embeddable
public class ExcelSpoolerParamColumnPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "PG_ESTRAZIONE", insertable = false, updatable = false)
    private long pgEstrazione;

    @Column(name = "PG_COLUMN", insertable = false, updatable = false)
    private long pgColumn;

    @Column(name = "ID_KEY")
    private String idKey;

    public ExcelSpoolerParamColumnPK() {
    }

    public long getPgEstrazione() {
        return this.pgEstrazione;
    }

    public void setPgEstrazione(long pgEstrazione) {
        this.pgEstrazione = pgEstrazione;
    }

    public long getPgColumn() {
        return this.pgColumn;
    }

    public void setPgColumn(long pgColumn) {
        this.pgColumn = pgColumn;
    }

    public String getIdKey() {
        return this.idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExcelSpoolerParamColumnPK)) {
            return false;
        }
        ExcelSpoolerParamColumnPK castOther = (ExcelSpoolerParamColumnPK) other;
        return
                (this.pgEstrazione == castOther.pgEstrazione)
                        && (this.pgColumn == castOther.pgColumn)
                        && this.idKey.equals(castOther.idKey);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + ((int) (this.pgEstrazione ^ (this.pgEstrazione >>> 32)));
        hash = hash * prime + ((int) (this.pgColumn ^ (this.pgColumn >>> 32)));
        hash = hash * prime + this.idKey.hashCode();

        return hash;
    }
}