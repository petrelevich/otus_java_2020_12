package ru.otus.core.model;

/**
 * @author sergey
 * created on 03.02.19.
 */
public class Client {
    private final Long id;
    private final String name;

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
