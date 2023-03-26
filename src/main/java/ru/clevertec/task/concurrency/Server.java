package ru.clevertec.task.concurrency;

import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Getter
public class Server {

    private final List<Integer> resourceList;

    public Server() {
        resourceList = new ArrayList<>();
    }

    @SneakyThrows
    public Response processRequest(Future<Client.Request> future) {
        int delayTime = new Random().ints(100, 1000)
                .findFirst()
                .orElse(100);
        TimeUnit.MILLISECONDS.sleep(delayTime);
        int message = future.get().getMessage();
        resourceList.add(message);

        return new Response(resourceList.size());
    }

    public record Response(int message) {
    }

}
