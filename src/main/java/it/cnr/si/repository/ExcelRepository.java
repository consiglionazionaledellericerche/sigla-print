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

import it.cnr.si.domain.sigla.ExcelSpooler;
import it.cnr.si.domain.sigla.ExcelSpoolerParam;
import it.cnr.si.domain.sigla.ExcelSpoolerParamColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by francesco on 12/09/16.
 */
@Repository
public interface ExcelRepository extends CrudRepository<ExcelSpooler, Long> {
    @Query("SELECT MIN(pgEstrazione) FROM ExcelSpooler WHERE "
            + "((stato = 'C' and dtProssimaEsecuzione is null) or (stato IN ('C', 'S') and dtProssimaEsecuzione < CURRENT_TIMESTAMP))")
    Long findExcelToExecute();

    @Query("select p.pgEstrazione from ExcelSpooler p " +
            "where p.stato = 'S' " +
            "and p.dtProssimaEsecuzione is null AND p.duva < :dateFrom")
    Iterable<Long> findXlsToDelete(@Param("dateFrom") Date dateFrom);

    @Query("select p from ExcelSpoolerParamColumn p " +
            "where p.excelSpoolerParam = :excelSpoolerParam ")
    List<ExcelSpoolerParamColumn> findExcelSpoolerParamColumns(@Param("excelSpoolerParam")ExcelSpoolerParam excelSpoolerParam);
}