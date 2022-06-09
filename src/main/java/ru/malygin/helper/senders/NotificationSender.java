package ru.malygin.helper.senders;

import ru.malygin.helper.model.Notification;

public interface NotificationSender {
    void send(Notification n);
}
