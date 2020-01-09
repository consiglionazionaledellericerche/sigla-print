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

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the EXCEL_SPOOLER_PARAM_COLUMN database table.
 */
@Entity
@Table(name = "EXCEL_SPOOLER_PARAM_COLUMN")
@NamedQuery(name = "ExcelSpoolerParamColumn.findAll", query = "SELECT e FROM ExcelSpoolerParamColumn e")
public class ExcelSpoolerParamColumn implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ExcelSpoolerParamColumnPK id;

    @Temporal(TemporalType.DATE)
    private Date dacr;

    @Temporal(TemporalType.DATE)
    private Date duva;

    @Column(name = "PG_VER_REC")
    private BigDecimal pgVerRec;

    private String utcr;

    private String utuv;

    private String value;

    //bi-directional many-to-one association to ExcelSpoolerParam
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PG_COLUMN", referencedColumnName = "PG_COLUMN", insertable = false, updatable = false),
            @JoinColumn(name = "PG_ESTRAZIONE", referencedColumnName = "PG_ESTRAZIONE", insertable = false, updatable = false)
    })
    private ExcelSpoolerParam excelSpoolerParam;

    public ExcelSpoolerParamColumn() {
    }

    public ExcelSpoolerParamColumnPK getId() {
        return this.id;
    }

    public void setId(ExcelSpoolerParamColumnPK id) {
        this.id = id;
    }

    public Date getDacr() {
        return this.dacr;
    }

    public void setDacr(Date dacr) {
        this.dacr = dacr;
    }

    public Date getDuva() {
        return this.duva;
    }

    public void setDuva(Date duva) {
        this.duva = duva;
    }

    public BigDecimal getPgVerRec() {
        return this.pgVerRec;
    }

    public void setPgVerRec(BigDecimal pgVerRec) {
        this.pgVerRec = pgVerRec;
    }

    public String getUtcr() {
        return this.utcr;
    }

    public void setUtcr(String utcr) {
        this.utcr = utcr;
    }

    public String getUtuv() {
        return this.utuv;
    }

    public void setUtuv(String utuv) {
        this.utuv = utuv;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ExcelSpoolerParam getExcelSpoolerParam() {
        return this.excelSpoolerParam;
    }

    public void setExcelSpoolerParam(ExcelSpoolerParam excelSpoolerParam) {
        this.excelSpoolerParam = excelSpoolerParam;
    }

}