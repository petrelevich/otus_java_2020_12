package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;
import ru.otus.crm.repository.ClientRepository;
import ru.otus.crm.repository.ManagerRepository;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DBServiceManager;
import java.util.HashSet;


@Component("actionDemo")
public class ActionDemo {
    private static final Logger log = LoggerFactory.getLogger(ActionDemo.class);

    private final ClientRepository clientRepository;
    private final ManagerRepository managerRepository;
    private final DBServiceClient dbServiceClient;
    private final DBServiceManager dbServiceManager;

    public ActionDemo(ClientRepository clientRepository, ManagerRepository managerRepository,
                      DBServiceClient dbServiceClient, DBServiceManager dbServiceManager) {
        this.managerRepository = managerRepository;
        this.clientRepository = clientRepository;
        this.dbServiceClient = dbServiceClient;
        this.dbServiceManager = dbServiceManager;
    }

    void action() {

//// создаем Manager
        dbServiceManager.saveManager(new Manager("m:" + System.currentTimeMillis(), "ManagerFirst", new HashSet<>(), true));

        var managerSecond = dbServiceManager.saveManager(new Manager("m:" + System.currentTimeMillis(), "ManagerSecond", new HashSet<>(), true));
        var managerSecondSelected = dbServiceManager.getManager(managerSecond.getId())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecond.getId()));
        log.info("managerSecondSelected:{}", managerSecondSelected);

//// обновляем Manager
        dbServiceManager.saveManager(new Manager(managerSecondSelected.getId(), "dbServiceSecondUpdated", new HashSet<>(), false));
        var managerUpdated = dbServiceManager.getManager(managerSecondSelected.getId())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecondSelected.getId()));
        log.info("managerUpdated:{}", managerUpdated);

/// создаем Client
        var firstClient = dbServiceClient.saveClient(new Client("dbServiceFirst" + System.currentTimeMillis(), managerSecond.getId()));

        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond" + System.currentTimeMillis(), managerSecond.getId()));
        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);

/// обновляем Client
        dbServiceClient.saveClient(new Client(clientSecondSelected.getId(), "dbServiceSecondUpdated", managerSecond.getId()));
        var clientUpdated = dbServiceClient.getClient(clientSecondSelected.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
        log.info("clientUpdated:{}", clientUpdated);


/// получаем все сущности
        log.info("All clients");
        dbServiceClient.findAll().forEach(client -> log.info("client:{}", client));

        log.info("All managers");
        dbServiceManager.findAll().forEach(manager -> log.info("manager:{}", manager));

/// применяем переопределенные методы репозитариев
        var clientFoundByName = clientRepository.findByName(firstClient.getName())
                .orElseThrow(() -> new RuntimeException("client not found, name:" + firstClient.getName()));
        log.info("clientFoundByName:{}", clientFoundByName);

        var clientFoundByNameIgnoreCase = clientRepository.findByNameIgnoreCase(firstClient.getName().toLowerCase())
                .orElseThrow(() -> new RuntimeException("client not found, name:" + firstClient.getName()));
        log.info("clientFoundByNameIgnoreCase:{}", clientFoundByNameIgnoreCase);

        clientRepository.updateName(firstClient.getId(), "newName");
        var updatedClient = clientRepository.findById(firstClient.getId())
                .orElseThrow(() -> new RuntimeException("client not found"));

        log.info("updatedClient:{}", updatedClient);

/// проверяем проблему N+1
        log.info("checking N+1 problem");
        var managerN = managerRepository.findById(managerSecond.getId())
                .orElseThrow(() -> new RuntimeException("Manager not found, name:" + managerSecond.getId()));
        log.info("managerN:{}", managerN);

        /*
        получаем основную сущность:
        [SELECT "manager"."id" AS "id", "manager"."label" AS "label" FROM "manager" WHERE "manager"."id" = ?]
        получаем дочерние:
        [SELECT "client"."id" AS "id", "client"."name" AS "name", "client"."manager_id" AS "manager_id" FROM "client" WHERE "client"."manager_id" = ?]
        */
    }
}
