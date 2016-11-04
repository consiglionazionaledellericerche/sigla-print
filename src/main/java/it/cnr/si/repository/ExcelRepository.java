package it.cnr.si.repository;

import it.cnr.si.domain.sigla.ExcelSpooler;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by francesco on 12/09/16.
 */
@Repository
public interface ExcelRepository extends CrudRepository<ExcelSpooler, Long> {
	@Query("SELECT MIN(pgEstrazione) FROM ExcelSpooler WHERE "
			+ "((stato = 'C' and dtProssimaEsecuzione is null) or (stato IN ('C', 'S') and dtProssimaEsecuzione < SYSDATE))")	
	public Long findExcelToExecute();
	
    @Query("select p.pgEstrazione from ExcelSpooler p, ParametriEnte e  where e.attivo = 'Y' and p.stato = 'S' and p.dtProssimaEsecuzione is null AND TRUNC(SYSDATE - p.duva) > Nvl(e.cancellaStampe,30)")
	Iterable<Long> findXlsToDelete();	
}