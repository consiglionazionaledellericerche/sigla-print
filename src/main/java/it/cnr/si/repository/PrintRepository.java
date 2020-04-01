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
    Long findReportToExecute(@Param("serverPriority") Integer serverPriority);

    @Query("select p.pgStampa from PrintSpooler p " +
            "where p.stato = 'S' " +
            "and p.dtProssimaEsecuzione is null AND p.duva < :dateFrom")
    Iterable<Long> findReportsToDelete(@Param("dateFrom") Date dateFrom);

    @Query("select max(p.pgStampa) + 1 from PrintSpooler p ")
    Long findMaxPgStampa();

}