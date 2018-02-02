package it.cnr.si.repository;

import it.cnr.si.domain.sigla.PrintSpooler;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by francesco on 12/09/16.
 */
@Repository
public interface PrintRepository extends CrudRepository<PrintSpooler, Long> {
	@Query("SELECT MIN(pgStampa) FROM PrintSpooler WHERE prioritaServer = :serverPriority AND "
			+ "((stato = 'C' and dtProssimaEsecuzione is null) or (stato IN ('C', 'S') and dtProssimaEsecuzione < CURRENT_TIMESTAMP ))")
	public Long findReportToExecute(@Param("serverPriority")Integer serverPriority);

    @Query("select p.pgStampa from PrintSpooler p " +
            "where p.stato = 'S' " +
            "and p.dtProssimaEsecuzione is null AND p.duva < :dateFrom")
	Iterable<Long> findReportsToDelete(@Param("dateFrom")Date dateFrom);
}