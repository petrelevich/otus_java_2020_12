package ru.otus.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;
import java.util.Set;

@Table("manager")
public class Manager implements Persistable<String> {

    @Id
    @Nonnull
    private final String id;
    private final String label;

    @MappedCollection(idColumn = "manager_id")
    private final Set<Client> clients;

    @Transient
    private final boolean isNew;

    public Manager(String id, String label, Set<Client> clients, boolean isNew) {
        this.id = id;
        this.label = label;
        this.clients = clients;
        this.isNew = isNew;
    }

    @PersistenceConstructor
    private Manager(String id, String label, Set<Client> clients) {
        this(id, label, clients, false);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Set<Client> getClients() {
        return clients;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", clients=" + clients +
                ", isNew=" + isNew +
                '}';
    }
}
