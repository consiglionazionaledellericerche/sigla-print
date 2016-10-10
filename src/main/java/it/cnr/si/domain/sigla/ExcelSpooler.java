package it.cnr.si.domain.sigla;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the EXCEL_SPOOLER database table.
 * 
 */
@Entity
@Table(name="EXCEL_SPOOLER")
@NamedQuery(name="ExcelSpooler.findAll", query="SELECT e FROM ExcelSpooler e")
public class ExcelSpooler implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final java.text.DateFormat FILE_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd");

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PG_ESTRAZIONE")
	private long pgEstrazione;

	@Temporal(TemporalType.DATE)
	private Date dacr;

	@Column(name="DS_ESTRAZIONE")
	private String dsEstrazione;

	@Temporal(TemporalType.DATE)
	@Column(name="DT_PARTENZA")
	private Date dtPartenza;

	@Temporal(TemporalType.DATE)
	@Column(name="DT_PROSSIMA_ESECUZIONE")
	private Date dtProssimaEsecuzione;

	@Temporal(TemporalType.DATE)
	private Date duva;

	@Column(name="EMAIL_A")
	private String emailA;

	@Column(name="EMAIL_BODY")
	private String emailBody;

	@Column(name="EMAIL_CC")
	private String emailCc;

	@Column(name="EMAIL_CCN")
	private String emailCcn;

	@Column(name="EMAIL_SUBJECT")
	private String emailSubject;

	private String errore;

	@Column(name="FL_EMAIL")
	private Boolean flEmail;

	private Integer intervallo;

	@Column(name="NOME_FILE")
	private String nomeFile;

	@Column(name="PG_VER_REC")
	private BigDecimal pgVerRec;

	@Lob
	private String query;

	private String server;

	@Column(name="SHEET_NAME")
	private String sheetName;

	private String stato;

	@Column(name="TI_INTERVALLO")
	private String tiIntervallo;

	private String utcr;

	private String utuv;

	//bi-directional many-to-one association to ExcelSpoolerParam
	@OrderBy("PG_COLUMN")
	@OneToMany(mappedBy="excelSpooler", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	private List<ExcelSpoolerParam> excelSpoolerParams;

	public ExcelSpooler() {
	}

	public long getPgEstrazione() {
		return this.pgEstrazione;
	}

	public void setPgEstrazione(long pgEstrazione) {
		this.pgEstrazione = pgEstrazione;
	}

	public Date getDacr() {
		return this.dacr;
	}

	public void setDacr(Date dacr) {
		this.dacr = dacr;
	}

	public String getDsEstrazione() {
		return this.dsEstrazione;
	}

	public void setDsEstrazione(String dsEstrazione) {
		this.dsEstrazione = dsEstrazione;
	}

	public Date getDtPartenza() {
		return this.dtPartenza;
	}

	public void setDtPartenza(Date dtPartenza) {
		this.dtPartenza = dtPartenza;
	}

	public Date getDtProssimaEsecuzione() {
		return this.dtProssimaEsecuzione;
	}

	public void setDtProssimaEsecuzione(Date dtProssimaEsecuzione) {
		this.dtProssimaEsecuzione = dtProssimaEsecuzione;
	}

	public Date getDuva() {
		return this.duva;
	}

	public void setDuva(Date duva) {
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

	public String getStato() {
		return this.stato;
	}

	public void setStato(String stato) {
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
        if(fileName.startsWith("_"))
            fileName = fileName.substring(1);
        fileName = fileName + ".xls";
        fileName = FILE_FORMAT.format(new Date()) + '_' + pgEstrazione + '_' + fileName;
        return fileName;
	}

}