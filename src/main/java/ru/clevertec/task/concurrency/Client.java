package ru.clevertec.task.concurrency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@Slf4j
@Getter
public class Client {

    private final List<Integer> dataList;
    private final ExecutorService executor;
    private final Lock lock;
    private final AtomicInteger accumulator;
    private final Server server;

    public Client(int dataListSize, int numberOfThreads) {
        server = new Server();
        dataList = new ArrayList<>(dataListSize);
        IntStream.rangeClosed(1, dataListSize)
                .forEach(dataList::add);
        executor = Executors.newFixedThreadPool(numberOfThreads);
        lock = new ReentrantLock();
        accumulator = new AtomicInteger(0);
    }

    @SneakyThrows
    public void sendRequest() {
        executor.invokeAll(createRequests())
                .stream()
                .map(server::processRequest)
                .forEach(response -> {
                    accumulator.accumulateAndGet(response.message(), Integer::sum);
                    log.info("Resource size: {}", response.message());
                });

        executor.shutdown();
        log.info("Client -> All requests sent. Data list size: " + dataList.size());
        log.info("Server -> All requests processed. Resource list is: {}", server.getResourceList());
        log.info("Accumulator -> " + accumulator);
    }

    private List<Request> createRequests() {
        return dataList.stream()
                .map(Request::new)
                .toList();
    }

    @Getter
    @AllArgsConstructor
    public class Request implements Callable<Request> {

        private int message;

        @Override
        public Request call() {
            Random random = new Random();
            lock.lock();
            int removedValue = dataList.remove(random.nextInt(dataList.size()));
            lock.unlock();
            return new Request(removedValue);
        }

    }

}
