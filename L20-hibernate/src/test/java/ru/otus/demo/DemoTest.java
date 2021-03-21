package ru.otus.demo;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.TransientObjectException;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.base.AbstractHibernateTest;
import ru.otus.crm.model.Client;

import javax.persistence.Query;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.Assert.assertThrows;


@DisplayName("Демо работы с hibernate (без абстракций) должно ")
class DemoTest extends AbstractHibernateTest {

    @DisplayName(" корректно сохранять и загружать клиента выполняя заданное кол-во запросов в нужное время")
    @ParameterizedTest(name = "клиент отключен от контекста (detached) перед загрузкой: {0}")
    @ValueSource(booleans = {false, true})
    void shouldCorrectSaveAndLoadClientWithExpectedQueriesCount(boolean clientDetachedBeforeGet) {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var savedClient = new Client("Ivan");

            session.save(savedClient);
            // Не должно быть выполнено ни одной вставки в БД не смотря на то что метод save был вызван
            assertThat(getUsageStatistics().getInsertCount()).isZero();

            session.getTransaction().commit();
            // Реальная вставка произошла в момент коммита транзакции (А что если GenerationType.IDENTITY?)
            assertThat(getUsageStatistics().getInsertCount()).isEqualTo(1);

            // Мы не ожидаем ни одного обращения к БД для загрузки клиента если он не отсоединен от контекста
            var expectedLoadCount = 0;
            if (clientDetachedBeforeGet) {
                session.detach(savedClient);
                // И ожидаем обращения к БД для загрузки если клиента в контексте нет
                expectedLoadCount = 1;
            }

            var loadedClient = session.get(Client.class, savedClient.getId());

            // Проверка, что количество загрузок из БД соответствует ожиданиям
            assertThat(getUsageStatistics().getLoadCount()).isEqualTo(expectedLoadCount);
            // И что мы достали того же клиента, что сохраняли
            assertThat(loadedClient).isNotNull().usingRecursiveComparison().isEqualTo(savedClient);
        }
    }

    @DisplayName(" корректно сохранять клиента в одной сессии и загружать в другой выполнив один запрос к БД для загрузки")
    @Test
    void shouldCorrectSaveAndLoadClientWithExpectedQueriesCountInTwoDifferentSessions() {
        var savedClient = new Client("Ivan");
        // Сохранили клиента в отдельной сессии
        saveClient(savedClient);

        try (var session = sessionFactory.openSession()) {
            // Загрузка клиента в отдельной сессии
            var loadedClient = session.get(Client.class, savedClient.getId());

            // Проверка, что для получения клиента было сделано обращение к БД
            // (т.е. клиент не сохранился в контексте при смене сессии)
            assertThat(getUsageStatistics().getLoadCount()).isEqualTo(1);

            // И что мы достали того же клиента, что сохраняли
            assertThat(loadedClient).isNotNull().usingRecursiveComparison().isEqualTo(savedClient);
        }
    }

    @DisplayName(" показывать в каких случаях загруженный с помощью load объект является прокси для сценария: ")
    @ParameterizedTest(name = "{2}")
    @CsvSource({"true, false, клиент не существует ",
            "false, false, клиент существует и он persistent",
            "false, true, клиент существует и он detached"})
    void shouldLoadProxyObjectWithLoadMethod(boolean loadedNotExistingClient, boolean clientDetachedBeforeLoad, String scenarioDescription) {

        var savedClient = new Client("Ivan");
        try (Session session = sessionFactory.openSession()) {
            // Сохранили клиента в рамках текущей сессии
            saveClient(session, savedClient);

            if (clientDetachedBeforeLoad) {
                // Отсоединили клиента от контекста если это нужно по текущему сценарию
                session.detach(savedClient);
            }

            // Если по сценарию нужно загружать клиента не существующего в БД, выставляем id=-1
            var id = loadedNotExistingClient ? -1L : savedClient.getId();
            var loadedClient = session.load(Client.class, id);

            // Метод load должен вернуть клиента не зависимо от того, существует ли он в БД или нет
            assertThat(loadedClient).isNotNull();

            if (loadedNotExistingClient || clientDetachedBeforeLoad) {
                // Если загружен не существующий в БД клиент или он был отсоединен от контекста, то загруженный объект д.б. Proxy
                assertThat(loadedClient).isInstanceOf(HibernateProxy.class);

                // Если загружен не существующий в БД клиент обращение к полю должно привести к ObjectNotFoundException
                if (loadedNotExistingClient) {
                    assertThrows(ObjectNotFoundException.class, loadedClient::getName);
                } else {
                    assertThatCode(loadedClient::getName).doesNotThrowAnyException();
                }
            } else {
                assertThat(loadedClient).isInstanceOf(Client.class);
            }
        }
    }

    @DisplayName(" показывать что если загрузить, с помощью load, не существующий объект, то с ним можно нормально работать после того, как он был добавлен в БД")
    @Test
    void shouldLoadNotExistingObjectAndWorkWithHimAfterItSaved() {
        var name = "Ivan";
        Client savedClient = new Client(name);
        Client loadedClient;
        try (Session session = sessionFactory.openSession()) {
            // На момент загрузки такого юзера в БД нет
            loadedClient = session.load(Client.class, 1L);
            // Проверяем, что вернулся прокси
            assertThat(loadedClient).isInstanceOf(HibernateProxy.class);
            // И не произошло обращения к БД
            assertThat(getUsageStatistics().getLoadCount()).isZero();

            // Сохраняем клиента в другой сессии
            saveClient(savedClient);
            // Теперь объект есть в БД. Проверяем что с объектом можно нормально работать
            assertThat(loadedClient.getName()).isEqualTo(name);
            // И в момент обращения к свойству произошла загрузка из БД
            assertThat(getUsageStatistics().getLoadCount()).isEqualTo(1);
        }
    }

    @DisplayName(" показывать, что загруженный с помощью get объект не является прокси")
    @Test
    void shouldLoadNotAProxyObjectWithGetMethod() {
        try (Session session = sessionFactory.openSession()) {
            Client savedClient = new Client("Ivan");
            saveClient(session, savedClient);

            // Загрузка с помощью метода get не существующего в БД клиента должна приводить к возврату null
            assertThat(session.get(Client.class, -1L)).isNull();

            // Метод get для существующего в БД клиента должен вернуть объект клиента не являющегося прокси
            assertThat(session.get(Client.class, savedClient.getId())).isNotNull()
                    .usingRecursiveComparison().isEqualTo(savedClient)
                    .isNotInstanceOf(HibernateProxy.class);
        }
    }

    @DisplayName(" показывать, что несколько обновлений в одной транзакции станут одним запросом к БД")
    @Test
    void shouldExecuteOneUpdateQueryForMultipleUpdateInOneTransaction() {
        try (var session = sessionFactory.openSession()) {
            var savedClient = new Client("Ivan");
            // Сохранили клиента в рамках текущей сессии
            saveClient(session, savedClient);

            session.beginTransaction();

            // Изменили имя клиента
            savedClient.setName("updated_1");
            session.update(savedClient);

            // Еще раз изменили имя клиента
            savedClient.setName("updated_2");
            session.update(savedClient);

            // И еще Еще раз изменили имя клиента
            savedClient.setName("updated_3");
            session.update(savedClient);

            session.getTransaction().commit();

            // Проверка, что в итоге был только один запрос к БД на обновление
            assertThat(getUsageStatistics().getUpdateCount()).isEqualTo(1);
        }
    }

    @DisplayName(" показывать, что вызов метода save на detached объекте приводит к генерации нового id")
    @Test
    void shouldGenerateNewIdWhenExecuteSaveMethodOnSameEntity() {
        try (var session = sessionFactory.openSession()) {
            var savedClient = new Client("Ivan");
            // Сохранили клиента в рамках текущей сессии
            saveClient(session, savedClient);
            // Запомнили его id
            var id = savedClient.getId();

            // Отсоединили клиента от контекста
            session.detach(savedClient);

            // Еще раз сохранили
            saveClient(session, savedClient);

            // Проверка, что второй раз сохраненный клиент имеет новый id
            assertThat(id).isNotEqualTo(savedClient.getId());
        }
    }

    @DisplayName(" показывать, что вызов метода saveOrUpdate на detached объекте не приводит к генерации нового id")
    @Test
    void shouldGenerateNewIdWhenExecuteSaveOrUpdateMethodOnSameEntity() {
        try (var session = sessionFactory.openSession()) {
            var savedClient = new Client("Ivan");
            // Сохранили клиента в рамках текущей сессии
            saveClient(session, savedClient);
            // Запомнили его id
            var id = savedClient.getId();

            // Отсоединили клиента от контекста
            session.detach(savedClient);

            // Еще раз сохранили с помощью saveOrUpdate
            session.beginTransaction();
            savedClient.setName("updated_1");
            session.saveOrUpdate(savedClient);
            session.getTransaction().commit();

            var loadedClient = loadClient(id);

            // Проверка, что второй раз сохраненный клиент имеет тот же id
            assertThat(loadedClient).usingRecursiveComparison().isEqualTo(savedClient);
        }
    }

    @DisplayName(" показывать, что вызов метода update на transient объекте приводит к исключению")
    @Test
    void shouldThrowExceptionWhenCommitTransactionAfterUpdateTransientEntity() {
        try (var session = sessionFactory.openSession()) {
            // Создали нового клиента, но не сохранили его
            var savedClient = new Client("Ivan");

            // Вызвали для данного клиента update
            session.beginTransaction();
            assertThrows(TransientObjectException.class, () -> session.update(savedClient));

            // Проверка, что id у него не появился
            assertThat(savedClient.getId()).isNull();
        }
    }

    @DisplayName(" показывать, что изменение persistent объекта внутри транзакции приводит к его изменению в БД")
    @Test
    void shouldUpdatePersistentEntityInDBWhenChangedFieldsInTransaction() {
        var originalName = "Ivan";
        var updatedName = "updatedName";
        var savedClient = new Client(originalName);
        try (var session = sessionFactory.openSession()) {
            // Открыли транзакцию
            session.beginTransaction();

            // Сохранили клиента
            session.save(savedClient);

            // Убедились, что его имя соответствует ожидаемому
            assertThat(savedClient.getName()).isEqualTo(savedClient.getName());
            // Сменили имя на новое
            savedClient.setName(updatedName);

            // Завершили транзакцию
            session.getTransaction().commit();
            // И сессию
        }
        // Загрузили клиента в новой сессии
        var loadedClient = loadClient(savedClient.getId());
        // Проверка, что имя загруженного клиента соответствует тому, что дали после сохранения
        assertThat(loadedClient.getName()).isEqualTo(updatedName);
    }

    private void saveClient(Session session, Client client) {
        session.beginTransaction();
        session.save(client);
        session.getTransaction().commit();
    }

    protected Client loadClient(long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Client.class, id);
        }
    }

    protected void saveClient(Client client) {
        try (Session session = sessionFactory.openSession()) {
            saveClient(session, client);
        }
    }
}
