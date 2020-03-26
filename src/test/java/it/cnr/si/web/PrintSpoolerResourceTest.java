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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by francesco on 28/09/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrintSpoolerResourceTest {

	@Autowired
    private PrintSpooleResource printSpooleResource;

	@Test
	public void testCreate() throws Exception {
		PrintSpooler printSpooler = new PrintSpooler();
		printSpooler.setReport("/doccont/doccont/vpg_man_rev_ass.jasper");
		printSpooler.setPriorita(1);
		printSpooler.setPrioritaServer(1);
		printSpooler.setTiVisibilita(PrintVisibility.P);
		PrintThreadLocal.set("test");

		printSpooler.setStato(PrintState.P);
		Set<PrintSpoolerParam> params = new HashSet<PrintSpoolerParam>();

		PrintSpoolerParam param = new PrintSpoolerParam();
		PrintSpoolerParamKey key = new PrintSpoolerParamKey();
		key.setPrintSpooler(printSpooler);
		key.setNomeParam("ciao");
		param.setKey( key);
		param.setValoreParam("ciaoValue");
		params.add(param);

		param = new PrintSpoolerParam();
		key = new PrintSpoolerParamKey();
		key.setPrintSpooler(printSpooler);
		key.setNomeParam("ciao2");
		param.setKey( key);
		param.setValoreParam("ciao2Value");
		params.add(param);

		printSpooler.setParams(params);
		ResponseEntity<Long> responseEntity=printSpooleResource.createPrintSpooler(printSpooler,"tes");
		assertTrue(responseEntity.getBody().compareTo(Long.decode("0"))>0);
	}
}