package ru.clevertec.task.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class ClientTest {

    private static final int DATA_LIST_SIZE = 10;
    private static final int NUMBER_OF_THREADS = 8;
    private Client client;

    @BeforeEach
    void setup() {
        client = new Client(DATA_LIST_SIZE, NUMBER_OF_THREADS);
    }

    @Test
    @DisplayName("test thread Request should return not zero value")
    void testThreadRequestShouldReturnNotZeroValue() throws ExecutionException, InterruptedException {
        Future<Client.Request> future = client.getExecutor().submit(client.new Request(1));
        int result = future.get().getMessage();

        assertThat(result).isNotZero();
    }

    @Test
    @DisplayName("test thread Request should return valid value")
    void testThreadRequestShouldReturnValidValue() throws ExecutionException, InterruptedException {
        Future<Client.Request> future = client.getExecutor().submit(client.new Request(1));
        int result = future.get().getMessage();

        assertThat(result >= 1 && result <= DATA_LIST_SIZE).isTrue();
    }

    @Test
    @DisplayName("test thread Request should remove different values from dataList")
    void testThreadRequestShouldRemoveDifferentValuesFromDataList() throws ExecutionException, InterruptedException {
        List<Future<Client.Request>> futures = List.of(
                client.getExecutor().submit(client.new Request(1)),
                client.getExecutor().submit(client.new Request(2))
        );
        int result1 = futures.get(0).get().getMessage();
        int result2 = futures.get(1).get().getMessage();

        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    @DisplayName("test that dataList size was reduced by Request thread")
    void testThatDataListSizeWasReducedByRequestThread() {
        List<Future<Client.Request>> futures = List.of(
                client.getExecutor().submit(client.new Request(1)),
                client.getExecutor().submit(client.new Request(2))
        );
        futures.forEach(future -> client.getServer().processRequest(future));

        assertThat(client.getDataList()).hasSize(DATA_LIST_SIZE - 2);
    }

    @Test
    @DisplayName("test that values was removed from dataList by Request thread")
    void testThatValuesWasRemovedFromDataListByRequestThread() throws ExecutionException, InterruptedException {
        List<Future<Client.Request>> futures = List.of(
                client.getExecutor().submit(client.new Request(1)),
                client.getExecutor().submit(client.new Request(2))
        );
        int result1 = futures.get(0).get().getMessage();
        int result2 = futures.get(1).get().getMessage();

        assertThat(client.getDataList()).doesNotContain(result1, result2);
    }

}
