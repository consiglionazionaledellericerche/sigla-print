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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * The persistent class for the EXCEL_SPOOLER database table.
 */
@Entity
@Table(name = "EXCEL_SPOOLER")
@NamedQuery(name = "ExcelSpooler.findAll", query = "SELECT e FROM ExcelSpooler e")
public class ExcelSpooler implements Serializable {
    public static final java.text.DateFormat FILE_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd");
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PG_ESTRAZIONE")
    private long pgEstrazione;

    private Timestamp dacr;

    @Column(name = "DS_ESTRAZIONE")
    private String dsEstrazione;

    @Column(name = "DT_PARTENZA")
    private Timestamp dtPartenza;

    @Column(name = "DT_PROSSIMA_ESECUZIONE")
    private Timestamp dtProssimaEsecuzione;

    private Timestamp duva;

    @Column(name = "EMAIL_A")
    private String emailA;

    @Column(name = "EMAIL_BODY")
    private String emailBody;

    @Column(name = "EMAIL_CC")
    private String emailCc;

    @Column(name = "EMAIL_CCN")
    private String emailCcn;

    @Column(name = "EMAIL_SUBJECT")
    private String emailSubject;

    private String errore;

    @Column(name = "FL_EMAIL")
    private Boolean flEmail;

    private Integer intervallo;

    @Column(name = "NOME_FILE")
    private String nomeFile;

    @Column(name = "PG_VER_REC")
    private BigDecimal pgVerRec;

    @Lob
    private String query;

    private String server;

    @Column(name = "SHEET_NAME")
    private String sheetName;

    @Enumerated(EnumType.STRING)
    private PrintState stato;

    @Column(name = "TI_INTERVALLO")
    private String tiIntervallo;

    @Column(name = "BEFORE_STATEMENT")
    private String beforeStatement;

    private String utcr;

    private String utuv;

    //bi-directional many-to-one association to ExcelSpoolerParam
    @OrderBy("PG_COLUMN")
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "PG_ESTRAZIONE", updatable = false)
    private List<ExcelSpoolerParam> excelSpoolerParams;

    public ExcelSpooler() {
    }

    public long getPgEstrazione() {
        return this.pgEstrazione;
    }

    public void setPgEstrazione(long pgEstrazione) {
        this.pgEstrazione = pgEstrazione;
    }

    public Timestamp getDacr() {
        return this.dacr;
    }

    public void setDacr(Timestamp dacr) {
        this.dacr = dacr;
    }

    public String getDsEstrazione() {
        return this.dsEstrazione;
    }

    public void setDsEstrazione(String dsEstrazione) {
        this.dsEstrazione = dsEstrazione;
    }

    public Timestamp getDtPartenza() {
        return this.dtPartenza;
    }

    public void setDtPartenza(Timestamp dtPartenza) {
        this.dtPartenza = dtPartenza;
    }

    public Timestamp getDtProssimaEsecuzione() {
        return this.dtProssimaEsecuzione;
    }

    public void setDtProssimaEsecuzione(Timestamp dtProssimaEsecuzione) {
        this.dtProssimaEsecuzione = dtProssimaEsecuzione;
    }

    public Timestamp getDuva() {
        return this.duva;
    }

    public void setDuva(Timestamp duva) {
        this.duva = duva;
    }

    public String getEmailA() {
        return this.emailA;
    }

    public void setEmailA(String emailA) {
        this.emailA = emailA;
    }

    public String getEmailBody() {
        return this.emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getEmailCc() {
        return this.emailCc;
    }

    public void setEmailCc(String emailCc) {
        this.emailCc = emailCc;
    }

    public String getEmailCcn() {
        return this.emailCcn;
    }

    public void setEmailCcn(String emailCcn) {
        this.emailCcn = emailCcn;
    }

    public String getEmailSubject() {
        return this.emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getErrore() {
        return this.errore;
    }

    public void setErrore(String errore) {
        this.errore = errore;
    }

    public Boolean getFlEmail() {
        return this.flEmail;
    }

    public void setFlEmail(Boolean flEmail) {
        this.flEmail = flEmail;
    }

    public Integer getIntervallo() {
        return this.intervallo;
    }

    public void setIntervallo(Integer intervallo) {
        this.intervallo = intervallo;
    }

    public String getNomeFile() {
        return this.nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public BigDecimal getPgVerRec() {
        return this.pgVerRec;
    }

    public void setPgVerRec(BigDecimal pgVerRec) {
        this.pgVerRec = pgVerRec;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSheetName() {
        return this.sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public PrintState getStato() {
        return this.stato;
    }

    public void setStato(PrintState stato) {
        this.stato = stato;
    }

    public String getTiIntervallo() {
        return this.tiIntervallo;
    }

    public void setTiIntervallo(String tiIntervallo) {
        this.tiIntervallo = tiIntervallo;
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

    public List<ExcelSpoolerParam> getExcelSpoolerParams() {
        return this.excelSpoolerParams;
    }

    public void setExcelSpoolerParams(List<ExcelSpoolerParam> excelSpoolerParams) {
        this.excelSpoolerParams = excelSpoolerParams;
    }

    public String getBeforeStatement() {
        return beforeStatement;
    }

    public void setBeforeStatement(String beforeStatement) {
        this.beforeStatement = beforeStatement;
    }

    public ExcelSpoolerParam addExcelSpoolerParam(ExcelSpoolerParam excelSpoolerParam) {
        getExcelSpoolerParams().add(excelSpoolerParam);
        excelSpoolerParam.setExcelSpooler(this);

        return excelSpoolerParam;
    }

    public ExcelSpoolerParam removeExcelSpoolerParam(ExcelSpoolerParam excelSpoolerParam) {
        getExcelSpoolerParams().remove(excelSpoolerParam);
        excelSpoolerParam.setExcelSpooler(null);

        return excelSpoolerParam;
    }

    public String getName() {
        String fileName = getSheetName();
        fileName = fileName.replace('/', '_');
        fileName = fileName.replace('\\', '_');
        fileName = fileName.replace(' ', '_');
        if (fileName.startsWith("_"))
            fileName = fileName.substring(1);
        fileName = fileName + ".xls";
        fileName = FILE_FORMAT.format(new Date()) + '_' + pgEstrazione + '_' + fileName;
        return fileName;
    }

    public boolean canExecute() {
        return Optional.ofNullable(getStato())
                .map(printState ->
                        (printState.equals(PrintState.C) && getDtProssimaEsecuzione() == null) ||
                                ((printState.equals(PrintState.C) || printState.equals(PrintState.S)) &&
                                        Optional.ofNullable(getDtProssimaEsecuzione())
                                                .map(Timestamp::toInstant)
                                                .orElse(Instant.now()).isBefore(Instant.now()))
                ).orElse(false);
    }
}