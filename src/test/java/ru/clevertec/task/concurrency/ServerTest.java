package ru.clevertec.task.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class ServerTest {

    private static final int DATA_LIST_SIZE = 10;
    private static final int NUMBER_OF_THREADS = 8;
    private Server server;
    private Client client;

    @BeforeEach
    void setup() {
        server = new Server();
        client = new Client(DATA_LIST_SIZE, NUMBER_OF_THREADS);
    }

    @Test
    @DisplayName("test processRequest method should return Response with expected resourceList size")
    void testProcessRequestMethodShouldReturnResponseWithExpectedResourceListSize() {
        Future<Client.Request> future = client.getExecutor().submit(client.new Request(1));
        Server.Response response = server.processRequest(future);
        int result = response.message();

        assertThat(result).isEqualTo(server.getResourceList().size());
    }

    @Test
    @DisplayName("test that expected values was added to resourceList")
    void testThatExpectedValuesWasAddedToResourceList() throws ExecutionException, InterruptedException {
        List<Future<Client.Request>> futures = List.of(
                client.getExecutor().submit(client.new Request(1)),
                client.getExecutor().submit(client.new Request(2))
        );
        futures.forEach(future -> server.processRequest(future));

        int result1 = futures.get(0).get().getMessage();
        int result2 = futures.get(1).get().getMessage();

        assertThat(server.getResourceList()).contains(result1, result2);
    }

}
