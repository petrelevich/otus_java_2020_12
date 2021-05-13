package ru.otus.crm.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Manager;

import java.util.List;


public interface ManagerRepository extends CrudRepository<Manager, String> {

    @Override
    @Query(value = "select c.id as client_id, c.name as client_name, c.manager_id as client_manager_id, " +
            " m.id as manager_id, m.label as manager_label, " +
            " lead(m.id) over (partition by m.id order by m.id) as new_manager_id " +
            "  from manager m " +
            " inner join client c on m.id = c.manager_id ",
            resultSetExtractorClass = ManagerResultSetExtractorClass.class)
    List<Manager> findAll();
}
