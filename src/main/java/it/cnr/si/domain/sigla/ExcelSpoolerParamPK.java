package it.cnr.si.domain.sigla;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the EXCEL_SPOOLER_PARAM database table.
 * 
 */
@Embeddable
public class ExcelSpoolerParamPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="PG_ESTRAZIONE", insertable=false, updatable=false)
	private long pgEstrazione;

	@Column(name="PG_COLUMN")
	private long pgColumn;

	public ExcelSpoolerParamPK() {
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ExcelSpoolerParamPK)) {
			return false;
		}
		ExcelSpoolerParamPK castOther = (ExcelSpoolerParamPK)other;
		return 
			(this.pgEstrazione == castOther.pgEstrazione)
			&& (this.pgColumn == castOther.pgColumn);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.pgEstrazione ^ (this.pgEstrazione >>> 32)));
		hash = hash * prime + ((int) (this.pgColumn ^ (this.pgColumn >>> 32)));
		
		return hash;
	}
}