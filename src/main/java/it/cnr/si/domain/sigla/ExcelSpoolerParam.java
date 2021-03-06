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
import java.util.List;


/**
 * The persistent class for the EXCEL_SPOOLER_PARAM database table.
 */
@Entity
@Table(name = "EXCEL_SPOOLER_PARAM")
@NamedQuery(name = "ExcelSpoolerParam.findAll", query = "SELECT e FROM ExcelSpoolerParam e")
public class ExcelSpoolerParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ExcelSpoolerParamPK id;

    @Column(name = "COLUMN_LABEL")
    private String columnLabel;

    @Column(name = "COLUMN_NAME")
    private String columnName;

    @Column(name = "COLUMN_TYPE")
    private String columnType;

    @Temporal(TemporalType.DATE)
    private Date dacr;

    @Temporal(TemporalType.DATE)
    private Date duva;

    @Column(name = "HEADER_LABEL")
    private String headerLabel;

    @Column(name = "PG_VER_REC")
    private BigDecimal pgVerRec;

    private String utcr;

    private String utuv;

    //bi-directional many-to-one association to ExcelSpooler
    @ManyToOne
    @JoinColumn(name = "PG_ESTRAZIONE", insertable = false, updatable = false)
    private ExcelSpooler excelSpooler;

    //bi-directional many-to-one association to ExcelSpoolerParamColumn
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "PG_ESTRAZIONE", updatable = false),
            @JoinColumn(name = "PG_COLUMN", updatable = false)
    })
    private List<ExcelSpoolerParamColumn> excelSpoolerParamColumns;

    public ExcelSpoolerParam() {
    }

    public ExcelSpoolerParamPK getId() {
        return this.id;
    }

    public void setId(ExcelSpoolerParamPK id) {
        this.id = id;
    }

    public String getColumnLabel() {
        return this.columnLabel;
    }

    public void setColumnLabel(String columnLabel) {
        this.columnLabel = columnLabel;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return this.columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
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

    public String getHeaderLabel() {
        return this.headerLabel;
    }

    public void setHeaderLabel(String headerLabel) {
        this.headerLabel = headerLabel;
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

    public ExcelSpooler getExcelSpooler() {
        return this.excelSpooler;
    }

    public void setExcelSpooler(ExcelSpooler excelSpooler) {
        this.excelSpooler = excelSpooler;
    }

    public List<ExcelSpoolerParamColumn> getExcelSpoolerParamColumns() {
        return this.excelSpoolerParamColumns;
    }

    public void setExcelSpoolerParamColumns(List<ExcelSpoolerParamColumn> excelSpoolerParamColumns) {
        this.excelSpoolerParamColumns = excelSpoolerParamColumns;
    }

    public ExcelSpoolerParamColumn addExcelSpoolerParamColumn(ExcelSpoolerParamColumn excelSpoolerParamColumn) {
        getExcelSpoolerParamColumns().add(excelSpoolerParamColumn);
        excelSpoolerParamColumn.setExcelSpoolerParam(this);

        return excelSpoolerParamColumn;
    }

    public ExcelSpoolerParamColumn removeExcelSpoolerParamColumn(ExcelSpoolerParamColumn excelSpoolerParamColumn) {
        getExcelSpoolerParamColumns().remove(excelSpoolerParamColumn);
        excelSpoolerParamColumn.setExcelSpoolerParam(null);
        return excelSpoolerParamColumn;
    }

    public String getParamColumns(String key) {
        for (ExcelSpoolerParamColumn excelSpoolerParamColumn : excelSpoolerParamColumns) {
            if (excelSpoolerParamColumn.getId().getIdKey().equalsIgnoreCase(key))
                return excelSpoolerParamColumn.getValue();
        }
        return null;
    }
}