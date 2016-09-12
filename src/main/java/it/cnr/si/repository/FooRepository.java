package it.cnr.si.repository;

import it.cnr.si.domain.Foo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by francesco on 12/09/16.
 */
public interface FooRepository extends CrudRepository<Foo, Long> {
}
