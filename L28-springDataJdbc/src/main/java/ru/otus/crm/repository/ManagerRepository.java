package ru.otus.crm.repository;

import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Manager;

import java.util.List;


public interface ManagerRepository extends CrudRepository<Manager, String> {

    @Override
    List<Manager> findAll();
}
