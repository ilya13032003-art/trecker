import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    private Task task(int id) {
        return new Task("Task", "Description", id, TaskType.TASK,
            LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30));
    }

    @Test
    void newTaskHasNewStatusAndSpecifiedId() {
        Task task = task(7);

        assertEquals(7, task.getId());
        assertEquals(TaskStatus.NEW, task.status);
    }

    @Test
    void tasksWithSameIdAreEqual() {
        Task first = task(1);
        Task second = task(1);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void tasksWithDifferentIdsAreNotEqual() {
        assertNotEquals(task(1), task(2));
        assertFalse(task(1).equals(null));
    }

    @Test
    void calculatesTaskEndTime() {
        assertEquals(LocalDateTime.of(2026, 9, 20, 10, 30), task(1).getEndTime());
        assertTrue(task(1).toString().contains("ID:1"));
    }
}
