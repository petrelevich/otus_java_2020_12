package ru.otus.listener;

import ru.otus.model.Message;

public interface Listener {

    void onUpdated(Message oldMsg, Message newMsg);

    //todo: 4. Сделать Listener для ведения истории: старое сообщение - новое (подумайте, как сделать, чтобы сообщения не портились)
    /*
     Сделайте такой тест:
        - положите сообщение в лисенер
        - поменяйте сообщение
        - проверьте, сто в лисенере сообщение не изменилось
    */
}
