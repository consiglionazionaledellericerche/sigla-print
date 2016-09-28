package it.cnr.si.web;

import it.cnr.si.dto.PrintRequest;
import it.cnr.si.service.PrintServiceTest;
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

    @Test
    public void testPrint() throws Exception {

        PrintRequest printRequest = new PrintRequest();
        printRequest.setPath(PrintServiceTest.ID);
        printRequest.setName("a-jasper-report");
        printResource.print(printRequest);

    }

}