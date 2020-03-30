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

import com.google.gson.Gson;
import it.cnr.si.domain.sigla.*;
import it.cnr.si.dto.EventPrint;
import net.sf.jasperreports.engine.JRParameter;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

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
		printSpooler.setReport("/doccont/doccont/vpg_man_rev_ass.jasper");
		printSpooler.setPriorita(1);
		printSpooler.setPrioritaServer(1);
		printSpooler.setTiVisibilita(PrintVisibility.P);
		PrintThreadLocal.set("testJson");

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

		params.add(new PrintSpoolerParam(new PrintSpoolerParamKey(
				JRParameter.REPORT_DATA_SOURCE, printSpooler),
				"",
				String.class.getCanonicalName()));

		ResponseEntity<Long> responseEntity=printSpooleResource.createPrintSpooler(printSpooler,"tes");
		assertTrue(responseEntity.getBody().compareTo(Long.decode("0"))>0);
	}

	@Test
	public void testCreateHttpClient() throws Exception {

		PrintSpooler printSpooler = new PrintSpooler();
		printSpooler.setReport("/doccont/doccont/vpg_man_rev_ass.jasper");
		printSpooler.setPriorita(1);
		printSpooler.setPrioritaServer(1);
		printSpooler.setTiVisibilita(PrintVisibility.P);
		PrintThreadLocal.set("testJson");

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

		params.add(new PrintSpoolerParam(new PrintSpoolerParamKey(
				JRParameter.REPORT_DATA_SOURCE, printSpooler),
				"",
				String.class.getCanonicalName()));


			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpPost method = null;
			try {
				method = new HttpPost( "http://localhost:8082/api/v1/get/print/");
				method.addHeader("Accept-Language", Locale.getDefault().toString());
				method.setHeader("Content-Type", "application/json;charset=UTF-8");
				method.setHeader("ds-utente", "test");
				HttpEntity requestEntity = new ByteArrayEntity(new Gson().toJson(printSpooler).getBytes());
				method.setEntity(requestEntity);
				HttpResponse httpResponse = httpclient.execute(method);
				int status = httpResponse.getStatusLine().getStatusCode();
				if (status != HttpStatus.SC_OK)
					throw new Exception("Webscript response width status error: "+status);

				//ResponseEntity<Long> i = httpResponse.getEntity();


			}finally{
				if (method != null)
					method.releaseConnection();
			}

	}

		//assertTrue(responseEntity.getBody().compareTo(Long.decode("0"))>0);
	@Test
	public void testLamba(){
		EventPrint e = new EventPrint( "1", 2L,"ecoci",true);
		PrintSpooler printSpooler = new PrintSpooler();
		printSpooler.setReport("/doccont/doccont/vpg_man_rev_ass.jasper");
		printSpooler.setPriorita(1);
		printSpooler.setPrioritaServer(1);
		printSpooler.setTiVisibilita(PrintVisibility.P);
		printSpooler.setStato(PrintState.P);

		Set<PrintSpoolerParam> params = new HashSet<PrintSpoolerParam>();
		params.add(new PrintSpoolerParam(new PrintSpoolerParamKey(
				"Test", printSpooler),
				"",
				String.class.getCanonicalName()));
		printSpooler.setParams(params);
		 Optional.of(printSpooler).
				filter(print->PrintState.P.equals(print.getStato())).
					ifPresent(
						p1->{
						  Optional.of(e).filter(json-> StringUtils.isNotEmpty(e.getJson())).
						ifPresent( ev->{
							Optional.ofNullable(printSpooler.getParams()).orElseGet(()->{
										Set<PrintSpoolerParam> p = new HashSet<PrintSpoolerParam>();
										PrintSpoolerParam param =new PrintSpoolerParam(new PrintSpoolerParamKey(
												JRParameter.REPORT_DATA_SOURCE, printSpooler),
												"",
												String.class.getCanonicalName());
										p.add(param);
										printSpooler.setParams(p);
										return printSpooler.getParams();
									}).stream().
										filter(paramO -> paramO.getKey().getNomeParam().equalsIgnoreCase(JRParameter.REPORT_DATA_SOURCE)).
										findFirst().orElseGet(()->{
											PrintSpoolerParam param =new PrintSpoolerParam(new PrintSpoolerParamKey(
												JRParameter.REPORT_DATA_SOURCE, printSpooler),
												"",
												String.class.getCanonicalName());
												printSpooler.getParams().add(param);
												return param;
										}).
									setValoreParam( ev.getJson());
							});
						});

		 Set<PrintSpoolerParam> s = printSpooler.getParams();
		 s.toString();
	}
}