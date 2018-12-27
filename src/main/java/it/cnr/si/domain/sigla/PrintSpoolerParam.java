package it.cnr.si.domain.sigla;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="PRINT_SPOOLER_PARAM")
public class PrintSpoolerParam {

	// Chiave Primaria composita
	@EmbeddedId
	private PrintSpoolerParamKey key;

	// VALORE_PARAM VARCHAR(300)
	@Column(name="VALORE_PARAM")
	private java.lang.String valoreParam;

	// PARAM_TYPE VARCHAR(100)
	@Column(name="PARAM_TYPE")
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
