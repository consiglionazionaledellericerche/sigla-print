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
import it.cnr.si.dto.Commit;
import it.cnr.si.dto.HookRequest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.jasperreports.engine.JRParameter;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by francesco on 28/09/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrintResourceTest {

    @Autowired
    private PrintResource printResource;



    //@Test
    public void testPrint() throws Exception {
    	PrintSpooler printSpooler = new PrintSpooler((long)5760923);
    	printSpooler.setReport("/doccont/doccont/vpg_man_rev_ass.jasper");
    	Set<PrintSpoolerParam> params = new HashSet<PrintSpoolerParam>();
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aCd_cds", printSpooler), "075", String.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aCd_terzo", printSpooler), "%", String.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aDt_a", printSpooler), "2016/02/05", Date.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aDt_da", printSpooler), "2016/02/05", Date.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aEs", printSpooler), "2016", Integer.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aPg_a", printSpooler), "6246", Long.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aPg_da", printSpooler), "6246", Long.class.getCanonicalName()));

    	printSpooler.setParams(params);
        printResource.print(printSpooler);

    }
	@Autowired
	private PrintSpoolerResource printSpooleResource;



    @Test
	public void testPrintJson() throws Exception {
		PrintSpooler printSpooler = new PrintSpooler();
		printSpooler.setReport("/ordmag/iss/TestJsonDs.jasper");
		printSpooler.setStato(PrintState.P);
		printSpooler.setPriorita(1);
		printSpooler.setPrioritaServer(1);
		printSpooler.setTiVisibilita(PrintVisibility.P);
		PrintThreadLocal.set("testJson");
		ResponseEntity<Long> response = printSpooleResource.createPrintSpooler(printSpooler, "testJson");

		 printSpooler = new PrintSpooler(response.getBody().longValue());

		Set<PrintSpoolerParam> params = new HashSet<PrintSpoolerParam>();
		params.add(new PrintSpoolerParam(new PrintSpoolerParamKey(
				JRParameter.REPORT_DATA_SOURCE, printSpooler),
				IOUtils.toString(this.getClass().getResourceAsStream("/testJson/JsonTestJasper.json"), StandardCharsets.UTF_8.name()),
				String.class.getCanonicalName()));
		printSpooler.setParams(params);

		ResponseEntity r= printResource.printDsOnBody(printSpooler, "testJson");

	}

    @Test
    public void testCache() throws Exception {
    	HookRequest hookRequest = new HookRequest();
    	hookRequest.setUser_name("mario.rossi");
    	Commit commit = new Commit();
    	commit.setAdded(Collections.emptyList());
    	commit.setRemoved(Collections.emptyList());    	
    	commit.setModified(Arrays.asList("docamm/docamm/vpg_missione_subreport0.jrxml", "doccont/doccont/vpg_man_rev_ass.jrxml"));
    	hookRequest.setCommits(Collections.singletonList(commit));
		printResource.hook(hookRequest);
    }    
}