import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryInMemoryTest {
    private Task task(int id) {
        return new Task("Task " + id, "Description", id, TaskType.TASK,
            LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30));
    }

    @Test
    void addsViewedTaskToHistory() {
        HistoryInMemory history = new HistoryInMemory();
        Task task = task(1);

        history.add(task);

        assertEquals(List.of(task), history.getHistory());
    }

    @Test
    void repeatedViewMovesTaskToEndWithoutDuplicates() {
        HistoryInMemory history = new HistoryInMemory();
        Task first = task(1);
        Task second = task(2);
        history.add(first);
        history.add(second);

        history.add(first);

        assertEquals(List.of(second, first), history.getHistory());
    }

    @Test
    void removesTaskFromHistoryById() {
        HistoryInMemory history = new HistoryInMemory();
        Task first = task(1);
        Task second = task(2);
        history.add(first);
        history.add(second);

        history.removeInHistory(first.getId());

        assertEquals(List.of(second), history.getHistory());
    }

    @Test
    void removingMissingIdDoesNotChangeHistory() {
        HistoryInMemory history = new HistoryInMemory();
        Task task = task(1);
        history.add(task);

        history.removeInHistory(99);

        assertEquals(List.of(task), history.getHistory());
    }

    @Test
    void clearsHistory() {
        HistoryInMemory history = new HistoryInMemory();
        history.add(task(1));

        history.clearHistory();

        assertTrue(history.getHistory().isEmpty());
    }
}
