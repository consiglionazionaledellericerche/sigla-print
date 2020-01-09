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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class PrintSpoolerParamKey implements Serializable {
    private static final long serialVersionUID = 1L;

    // NOME_PARAM VARCHAR(100) NOT NULL (PK)
    @Column(name = "NOME_PARAM")
    private java.lang.String nomeParam;

    @ManyToOne
    @JoinColumn(name = "PG_STAMPA")
    private PrintSpooler printSpooler;

    public PrintSpoolerParamKey() {
        super();
    }

    public PrintSpoolerParamKey(String nomeParam, PrintSpooler printSpooler) {
        super();
        this.nomeParam = nomeParam;
        this.printSpooler = printSpooler;
    }

    public java.lang.String getNomeParam() {
        return nomeParam;
    }

    public void setNomeParam(java.lang.String nomeParam) {
        this.nomeParam = nomeParam;
    }

    public java.lang.Long getPgStampa() {
        if (getPrintSpooler() == null)
            return null;
        return getPrintSpooler().getPgStampa();
    }

    public void setPgStampa(java.lang.Long pgStampa) {
        getPrintSpooler().setPgStampa(pgStampa);
    }

    public PrintSpooler getPrintSpooler() {
        return printSpooler;
    }

    public void setPrintSpooler(PrintSpooler printSpooler) {
        this.printSpooler = printSpooler;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "PrintSpoolerParamKey{" +
                "nomeParam='" + nomeParam + '\'' +
                '}';
    }
}
