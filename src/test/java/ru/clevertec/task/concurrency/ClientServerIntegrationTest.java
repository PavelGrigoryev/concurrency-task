package ru.clevertec.task.concurrency;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ClientServerIntegrationTest {

    private static final int DATA_LIST_SIZE = 10;
    private static final int NUMBER_OF_THREADS = 8;
    private static Client client;
    private static Server server;

    @BeforeAll
    static void setup() {
        client = new Client(DATA_LIST_SIZE, NUMBER_OF_THREADS);
        client.sendRequest();
        server = client.getServer();
    }

    @Test
    @DisplayName("test clients dataList should be empty after all request have been sent")
    void testDataListShouldBeEmptyAfterAllRequestHaveBeenSent() {
        List<Integer> dataList = client.getDataList();

        assertThat(dataList).isEmpty();
    }

    @Test
    @DisplayName("test clients accumulator should have expected sum")
    void testAccumulatorShouldHaveExpectedSum() {
        int expectedSum = (1 + DATA_LIST_SIZE) * (DATA_LIST_SIZE / 2);
        int actualSum = client.getAccumulator().get();

        assertThat(actualSum).isEqualTo(expectedSum);
    }

    @Test
    @DisplayName("test servers resourceList should have expected size")
    void testResourceListShouldHaveExpectedSize() {
        assertThat(server.getResourceList()).hasSize(DATA_LIST_SIZE);
    }

    @Test
    @DisplayName("test servers resourceList should have unique values")
    void testResourceListShouldHaveUniqueValues() {
        Set<Integer> resourceSet = new HashSet<>(server.getResourceList());

        assertThat(server.getResourceList()).containsAll(resourceSet);
    }

}
