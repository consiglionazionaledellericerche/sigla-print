package it.cnr.si.domain.sigla;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@Embeddable
public class PrintSpoolerParamKey implements Serializable {
	private static final long serialVersionUID = 1L;

	// NOME_PARAM VARCHAR(100) NOT NULL (PK)
	@Column(name="NOME_PARAM")
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
