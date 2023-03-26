package ru.clevertec.task;

import ru.clevertec.task.concurrency.Client;

public class Main {

    public static void main(String[] args) {
        Client client = new Client(100, 8);
        client.sendRequest();
    }

}
