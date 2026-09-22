import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {
    private static final LocalDateTime FIRST_START = LocalDateTime.of(2026, 9, 20, 10, 0);
    private static final Duration HALF_HOUR = Duration.ofMinutes(30);

    @Test
    void createsTaskAndAddsItToStorageAndPriorities() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = manager.createTask("Task", "Description", TaskType.TASK, FIRST_START, HALF_HOUR);

        assertEquals(1, task.getId());
        assertSame(task, manager.getBaseTask().get(task.getId()));
        assertTrue(manager.getPrioritizedTasks().contains(task));
    }

    @Test
    void createsEpicAndSubtask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = manager.createEpic("Epic", "Description", TaskType.EPIC);

        Task subtask = manager.createSubTask("Subtask", "Description", epic.getId(),
            TaskType.SUB_TASK, FIRST_START, HALF_HOUR);

        assertEquals(2, subtask.getId());
        assertSame(subtask, epic.getSubTaskArray().get(subtask.getId()));
        assertTrue(manager.getPrioritizedTasks().contains(subtask));
    }

    @Test
    void rejectsSubtaskForUnknownEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        assertThrows(IllegalArgumentException.class, () -> manager.createSubTask(
            "Subtask", "Description", 99, TaskType.SUB_TASK, FIRST_START, HALF_HOUR));
    }

    @Test
    void updatesStatusOfRegularTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = manager.createTask("Task", "Description", TaskType.TASK, FIRST_START, HALF_HOUR);

        manager.updateStatus(task.getId(), TaskType.TASK, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.status);
    }

    @Test
    void epicIsNewWhenAllSubtasksAreNew() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = manager.createEpic("Epic", "Description", TaskType.EPIC);
        manager.createSubTask("Subtask", "Description", epic.getId(), TaskType.SUB_TASK,
            FIRST_START, HALF_HOUR);

        assertEquals(TaskStatus.NEW, epic.status);
    }

    @Test
    void epicIsDoneWhenAllSubtasksAreDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = manager.createEpic("Epic", "Description", TaskType.EPIC);
        Task first = manager.createSubTask("First", "Description", epic.getId(), TaskType.SUB_TASK,
            FIRST_START, HALF_HOUR);
        Task second = manager.createSubTask("Second", "Description", epic.getId(), TaskType.SUB_TASK,
            FIRST_START.plusHours(1), HALF_HOUR);

        manager.updateStatus(first.getId(), TaskType.SUB_TASK, TaskStatus.DONE);
        manager.updateStatus(second.getId(), TaskType.SUB_TASK, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, epic.status);
    }

    @Test
    void epicIsInProgressWhenSubtaskIsInProgress() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = manager.createEpic("Epic", "Description", TaskType.EPIC);
        Task subtask = manager.createSubTask("Subtask", "Description", epic.getId(),
            TaskType.SUB_TASK, FIRST_START, HALF_HOUR);

        manager.updateStatus(subtask.getId(), TaskType.SUB_TASK, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, epic.status);
    }

    @Test
    void checksTimeIntersections() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        assertTrue(manager.timeCheck(FIRST_START, 60));
        assertFalse(manager.timeCheck(FIRST_START.plusMinutes(30), 30));
        assertTrue(manager.timeCheck(FIRST_START.plusHours(1), 30));
    }

    @Test
    void removesTaskFromStoragePrioritiesAndSchedule() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.timeCheck(FIRST_START, 30);
        Task task = manager.createTask("Task", "Description", TaskType.TASK, FIRST_START, HALF_HOUR);

        manager.removeTaskByType(TaskType.TASK, task.getId());

        assertFalse(manager.getBaseTask().containsKey(task.getId()));
        assertFalse(manager.getPrioritizedTasks().contains(task));
        assertTrue(manager.timeCheck(FIRST_START, 30));
    }

    @Test
    void removesSubtaskFromEpicPrioritiesAndSchedule() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = manager.createEpic("Epic", "Description", TaskType.EPIC);
        manager.timeCheck(FIRST_START, 30);
        Task subtask = manager.createSubTask("Subtask", "Description", epic.getId(),
            TaskType.SUB_TASK, FIRST_START, HALF_HOUR);

        manager.removeSubTask(subtask.getId());

        assertTrue(epic.getSubTaskArray().isEmpty());
        assertFalse(manager.getPrioritizedTasks().contains(subtask));
        assertTrue(manager.timeCheck(FIRST_START, 30));
    }

    @Test
    void removesEpicSubtasksFromPrioritiesAndSchedule() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = manager.createEpic("Epic", "Description", TaskType.EPIC);
        manager.timeCheck(FIRST_START, 30);
        Task subtask = manager.createSubTask("Subtask", "Description", epic.getId(),
            TaskType.SUB_TASK, FIRST_START, HALF_HOUR);

        manager.removeTaskByType(TaskType.EPIC, epic.getId());

        assertFalse(manager.getBaseEpic().containsKey(epic.getId()));
        assertFalse(manager.getPrioritizedTasks().contains(subtask));
        assertTrue(manager.timeCheck(FIRST_START, 30));
    }

    @Test
    void clearsAllTasksPrioritiesAndSchedule() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.timeCheck(FIRST_START, 30);
        manager.createTask("Task", "Description", TaskType.TASK, FIRST_START, HALF_HOUR);
        manager.createEpic("Epic", "Description", TaskType.EPIC);

        manager.removeAll();

        assertTrue(manager.getBaseTask().isEmpty());
        assertTrue(manager.getBaseEpic().isEmpty());
        assertTrue(manager.getPrioritizedTasks().isEmpty());
        assertTrue(manager.timeCheck(FIRST_START, 30));
    }

    @Test
    void ordersPrioritizedTasksByStartTime() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task later = manager.createTask("Later", "Description", TaskType.TASK,
            FIRST_START.plusHours(2), HALF_HOUR);
        Task earlier = manager.createTask("Earlier", "Description", TaskType.TASK,
            FIRST_START, HALF_HOUR);

        assertEquals(List.of(earlier, later), new ArrayList<>(manager.getPrioritizedTasks()));
    }
}
