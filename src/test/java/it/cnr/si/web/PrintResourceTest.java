package it.cnr.si.web;

import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.domain.sigla.PrintSpoolerParam;
import it.cnr.si.domain.sigla.PrintSpoolerParamKey;
import it.cnr.si.dto.Commit;
import it.cnr.si.dto.HookRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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