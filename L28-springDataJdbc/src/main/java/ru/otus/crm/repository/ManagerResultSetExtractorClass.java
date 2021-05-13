package ru.otus.crm.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManagerResultSetExtractorClass implements ResultSetExtractor<List<Manager>> {

    @Override
    public List<Manager> extractData(ResultSet rs) throws SQLException, DataAccessException {
        var managerList = new ArrayList<Manager>();
        Set<Client> clients = new HashSet<>();
        while (rs.next()) {
            clients.add(new Client(rs.getLong("client_id"), rs.getString("client_name"), rs.getString("client_manager_id")));
            var newManagerId = rs.getString("new_manager_id");
            if (newManagerId != null) {
                var manager = new Manager(rs.getString("manager_id"), rs.getString("manager_label"), clients, false);
                managerList.add(manager);
                clients = new HashSet<>();
            }
        }
        return managerList;
    }
}
