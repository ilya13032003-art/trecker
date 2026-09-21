import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EpicTest {
    @Test
    void emptyEpicHasNoStartOrEndAndZeroDuration() {
        Epic epic = new Epic("Epic", "Description", 1, TaskType.EPIC);

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(Duration.ZERO, epic.getDuration());
    }

    @Test
    void calculatesTimingFromEarliestStartToLatestEnd() {
        Epic epic = new Epic("Epic", "Description", 1, TaskType.EPIC);
        Task later = new Task("Later", "Description", 3, TaskType.SUB_TASK,
            LocalDateTime.of(2026, 9, 20, 12, 0), Duration.ofMinutes(45));
        Task earlier = new Task("Earlier", "Description", 2, TaskType.SUB_TASK,
            LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30));
        epic.getSubTaskArray().put(later.getId(), later);
        epic.getSubTaskArray().put(earlier.getId(), earlier);

        assertEquals(LocalDateTime.of(2026, 9, 20, 10, 0), epic.getStartTime());
        assertEquals(LocalDateTime.of(2026, 9, 20, 12, 45), epic.getEndTime());
        assertEquals(Duration.ofMinutes(165), epic.getDuration());
    }

    @Test
    void returnsSubtaskIds() {
        Epic epic = new Epic("Epic", "Description", 1, TaskType.EPIC);
        epic.getSubTaskArray().put(2, new Task("Subtask", "Description", 2,
            TaskType.SUB_TASK, LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30)));

        assertEquals(List.of(2), epic.getSubTasksId());
    }
}
