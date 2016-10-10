package it.cnr.si.domain.sigla;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the PARAMETRI_ENTE database table.
 * 
 */
@Entity
@Table(name="PARAMETRI_ENTE")
@NamedQuery(name="ParametriEnte.findAll", query="SELECT p FROM ParametriEnte p")
public class ParametriEnte implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String attivo;

	@Column(name="BOX_COMUNICAZIONI")
	private String boxComunicazioni;

	@Column(name="BOX_SCADENZE")
	private String boxScadenze;

	@Column(name="CANCELLA_STAMPE")
	private BigDecimal cancellaStampe;

	@Temporal(TemporalType.DATE)
	private Date dacr;

	private String descrizione;

	@Temporal(TemporalType.DATE)
	@Column(name="DT_LDAP_MIGRAZIONE")
	private Date dtLdapMigrazione;

	@Temporal(TemporalType.DATE)
	private Date duva;

	@Column(name="FL_AUTENTICAZIONE_LDAP")
	private String flAutenticazioneLdap;

	@Column(name="LDAP_APP_NAME")
	private String ldapAppName;

	@Column(name="LDAP_BASE_DN")
	private String ldapBaseDn;

	@Column(name="LDAP_LINK_CAMBIO_PASSWORD")
	private String ldapLinkCambioPassword;

	@Column(name="LDAP_PASSWORD")
	private String ldapPassword;

	@Column(name="LDAP_USER")
	private String ldapUser;

	@Column(name="PG_VER_REC")
	private BigDecimal pgVerRec;

	@Column(name="TIPO_DB")
	private String tipoDb;

	private String utcr;

	private String utuv;

	public ParametriEnte() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAttivo() {
		return this.attivo;
	}

	public void setAttivo(String attivo) {
		this.attivo = attivo;
	}

	public String getBoxComunicazioni() {
		return this.boxComunicazioni;
	}

	public void setBoxComunicazioni(String boxComunicazioni) {
		this.boxComunicazioni = boxComunicazioni;
	}

	public String getBoxScadenze() {
		return this.boxScadenze;
	}

	public void setBoxScadenze(String boxScadenze) {
		this.boxScadenze = boxScadenze;
	}

	public BigDecimal getCancellaStampe() {
		return this.cancellaStampe;
	}

	public void setCancellaStampe(BigDecimal cancellaStampe) {
		this.cancellaStampe = cancellaStampe;
	}

	public Date getDacr() {
		return this.dacr;
	}

	public void setDacr(Date dacr) {
		this.dacr = dacr;
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Date getDtLdapMigrazione() {
		return this.dtLdapMigrazione;
	}

	public void setDtLdapMigrazione(Date dtLdapMigrazione) {
		this.dtLdapMigrazione = dtLdapMigrazione;
	}

	public Date getDuva() {
		return this.duva;
	}

	public void setDuva(Date duva) {
		this.duva = duva;
	}

	public String getFlAutenticazioneLdap() {
		return this.flAutenticazioneLdap;
	}

	public void setFlAutenticazioneLdap(String flAutenticazioneLdap) {
		this.flAutenticazioneLdap = flAutenticazioneLdap;
	}

	public String getLdapAppName() {
		return this.ldapAppName;
	}

	public void setLdapAppName(String ldapAppName) {
		this.ldapAppName = ldapAppName;
	}

	public String getLdapBaseDn() {
		return this.ldapBaseDn;
	}

	public void setLdapBaseDn(String ldapBaseDn) {
		this.ldapBaseDn = ldapBaseDn;
	}

	public String getLdapLinkCambioPassword() {
		return this.ldapLinkCambioPassword;
	}

	public void setLdapLinkCambioPassword(String ldapLinkCambioPassword) {
		this.ldapLinkCambioPassword = ldapLinkCambioPassword;
	}

	public String getLdapPassword() {
		return this.ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	public String getLdapUser() {
		return this.ldapUser;
	}

	public void setLdapUser(String ldapUser) {
		this.ldapUser = ldapUser;
	}

	public BigDecimal getPgVerRec() {
		return this.pgVerRec;
	}

	public void setPgVerRec(BigDecimal pgVerRec) {
		this.pgVerRec = pgVerRec;
	}

	public String getTipoDb() {
		return this.tipoDb;
	}

	public void setTipoDb(String tipoDb) {
		this.tipoDb = tipoDb;
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

}