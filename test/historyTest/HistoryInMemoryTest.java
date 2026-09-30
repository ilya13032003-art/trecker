package historyTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import history.HistoryInMemory;
import org.junit.jupiter.api.Test;
import models.Task;
import models.field.TaskType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryInMemoryTest {
    private Task task(int id) {
        return new Task("tasks.Task " + id, "Description", id, TaskType.TASK,
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
    void emptyHistory() {
        HistoryInMemory history = new HistoryInMemory();

        assertEquals(List.of(), history.getHistory());
    }

    @Test
    void historyAfterRemoveMidTask() {
        HistoryInMemory history = new HistoryInMemory();
        Task earler = task(1);
        Task mid = task(2);
        Task later = task(3);

        history.add(earler);
        history.add(mid);
        history.add(later);

        history.removeInHistory(2);

        assertEquals(List.of(earler, later), history.getHistory());
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
