package it.cnr.si.repository;

import it.cnr.si.domain.Foo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

/**
 * Created by francesco on 12/09/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class FooRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FooRepositoryTest.class);

    @Autowired
    private FooRepository fooRepository;

    @Test
    public void testFoo() {

        fooRepository.deleteAll();

        fooRepository.save(new Foo("foo"));
        fooRepository.save(new Foo("bar"));
        fooRepository.save(new Foo("baz"));

        long count = StreamSupport.stream(fooRepository.findAll().spliterator(), false)
                .peek(foo -> LOGGER.info(foo.getTitle()))
                .count();

        assertEquals(3l, count);
    }




}