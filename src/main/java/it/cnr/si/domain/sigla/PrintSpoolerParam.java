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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PRINT_SPOOLER_PARAM")
public class PrintSpoolerParam {

    // Chiave Primaria composita
    @EmbeddedId
    private PrintSpoolerParamKey key;

    // VALORE_PARAM VARCHAR(300)
    @Column(name = "VALORE_PARAM")
    private java.lang.String valoreParam;

    // PARAM_TYPE VARCHAR(100)
    @Column(name = "PARAM_TYPE")
    private java.lang.String paramType;

    public PrintSpoolerParam() {
        super();
    }

    public PrintSpoolerParam(PrintSpoolerParamKey key) {
        super();
        this.key = key;
    }

    public PrintSpoolerParam(PrintSpoolerParamKey key, String valoreParam,
                             String paramType) {
        super();
        this.key = key;
        this.valoreParam = valoreParam;
        this.paramType = paramType;
    }

    public PrintSpoolerParamKey getKey() {
        return key;
    }

    public void setKey(PrintSpoolerParamKey key) {
        this.key = key;
    }

    public java.lang.String getValoreParam() {
        return valoreParam;
    }

    public void setValoreParam(java.lang.String valoreParam) {
        this.valoreParam = valoreParam;
    }

    public java.lang.String getParamType() {
        return paramType;
    }

    public void setParamType(java.lang.String paramType) {
        this.paramType = paramType;
    }

    @Override
    public String toString() {
        return "PrintSpoolerParam{" +
                "key=" + key +
                ", valoreParam='" + valoreParam + '\'' +
                ", paramType='" + paramType + '\'' +
                '}';
    }
}
