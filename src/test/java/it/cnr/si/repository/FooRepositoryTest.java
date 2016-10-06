package it.cnr.si.repository;

import static org.junit.Assert.assertEquals;

import java.util.stream.StreamSupport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by francesco on 12/09/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class FooRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FooRepositoryTest.class);

    @Autowired
    private PrintRepository printRepository;


    @Test
    public void testFoo() {

//        long count = StreamSupport.stream(printRepository.findAll().spliterator(), false)
//                .peek(foo -> LOGGER.info(foo.getTitle()))
//                .count();
//
//        assertEquals(3l, count);
    }




}