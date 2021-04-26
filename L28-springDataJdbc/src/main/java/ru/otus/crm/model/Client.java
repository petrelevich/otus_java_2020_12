package ru.otus.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;


@Table("client")
public class Client {

    @Id
    private final Long id;
    @Nonnull
    private final String name;

    @Nonnull
    private final String managerId;

    public Client(String name, String managerId) {
        this.id = null;
        this.managerId = managerId;
        this.name = name;
    }

    @PersistenceConstructor
    public Client(Long id, String name, String managerId) {
        this.id = id;
        this.name = name;
        this.managerId = managerId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nonnull
    public String getManagerId() {
        return managerId;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", managerId='" + managerId + '\'' +
                '}';
    }
}
