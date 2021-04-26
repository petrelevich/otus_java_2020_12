package ru.otus.crm.repository;

import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Manager;


public interface ManagerRepository extends CrudRepository<Manager, String> {

}
