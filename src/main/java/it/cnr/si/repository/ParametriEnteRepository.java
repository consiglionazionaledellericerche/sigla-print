package it.cnr.si.repository;

import it.cnr.si.domain.sigla.ExcelSpooler;
import it.cnr.si.domain.sigla.ParametriEnte;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Created by francesco on 12/09/16.
 */
@Repository
public interface ParametriEnteRepository extends CrudRepository<ParametriEnte, Long> {
	@Query("select e.cancellaStampe from ParametriEnte e " +
			"where e.attivo = 'Y' ")
	public BigDecimal findCancellaStampe();
}