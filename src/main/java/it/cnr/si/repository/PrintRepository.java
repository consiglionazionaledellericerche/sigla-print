package it.cnr.si.repository;

import it.cnr.si.domain.sigla.PrintSpooler;

import java.util.Date;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by francesco on 12/09/16.
 */
@Repository
public interface PrintRepository extends CrudRepository<PrintSpooler, Long> {
	@Query("SELECT MIN(pgStampa) FROM PrintSpooler WHERE prioritaServer = ?1 AND "
			+ "((stato = 'C' and dtProssimaEsecuzione is null) or (stato IN ('C', 'S') and dtProssimaEsecuzione BETWEEN ?2 AND ?3 ))")	
	public Long findReportToExecute(@Param("serverPriority")Integer serverPriority, @Param("dataInizio")Date dataInizio, @Param("dataFine")Date dataFine);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PrintSpooler p where p.pgStampa = :pgStampa")
	PrintSpooler findOneForUpdate(@Param("pgStampa") Long pgStampa);	
}