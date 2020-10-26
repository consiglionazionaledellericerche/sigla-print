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

package it.cnr.si.web;

import it.cnr.si.domain.sigla.*;
import net.sf.jasperreports.engine.JRParameter;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by francesco on 28/09/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrintSpoolerResourceTest {

    @Autowired
    private PrintSpoolerResource printSpooleResource;

    @Test
    public void testCreate() throws Exception {
        PrintSpooler printSpooler = new PrintSpooler();
        printSpooler.setPgStampa(BigDecimal.ONE.longValue());
        printSpooler.setReport("/ordmag/iss/TestJsonDs.jasper");
        printSpooler.setPriorita(1);
        printSpooler.setPrioritaServer(1);
        printSpooler.setTiVisibilita(PrintVisibility.P);
        PrintThreadLocal.set("testJson");

        printSpooler.setStato(PrintState.P);
        Set<PrintSpoolerParam> params = new HashSet<PrintSpoolerParam>();

        printSpooler.setParams(params);

        PrintSpoolerParam param = new PrintSpoolerParam();
        PrintSpoolerParamKey key = new PrintSpoolerParamKey();
        key.setPrintSpooler(printSpooler);
        key.setNomeParam("ciao");
        param.setKey(key);
        param.setValoreParam("ciaoValue");
        param.setParamType(String.class.getCanonicalName());
        params.add(param);

        param = new PrintSpoolerParam();
        key = new PrintSpoolerParamKey();
        key.setPrintSpooler(printSpooler);
        key.setNomeParam("ciao2");
        param.setKey(key);
        param.setValoreParam("ciao2Value");
        param.setParamType(String.class.getCanonicalName());
        params.add(param);

        params.add(new PrintSpoolerParam(new PrintSpoolerParamKey(
                JRParameter.REPORT_DATA_SOURCE, printSpooler),
                "",
                String.class.getCanonicalName()));

        ResponseEntity<Long> responseEntity = printSpooleResource.createPrintSpooler(printSpooler, "tes");
        assertTrue(responseEntity.getBody().compareTo(Long.decode("0")) > 0);
    }

    @After
    public void shutdown() {
        printSpooleResource.deletePrintSpooler(BigDecimal.ONE.longValue(), "tes");
    }
}