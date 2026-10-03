package managerTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import models.Epic;
import models.Task;
import models.field.TaskStatus;
import models.field.TaskType;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {
    private static final LocalDateTime FIRST_START = LocalDateTime.of(2026, 9, 20, 10, 0);
    private static final Duration HALF_HOUR = Duration.ofMinutes(30);
    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void createsTaskAndAddsItToStorageAndPriorities() {
        Task task = manager.createTask("tasks.Task", "Description", TaskType.TASK, FIRST_START, HALF_HOUR);

        assertEquals(1, task.getId());
        assertSame(task, manager.getBaseTask().get(task.getId()));
        assertTrue(manager.getPrioritizedTasks().contains(task));
    }

    @Test
    void createsEpicAndSubtask() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);

        Task subtask = manager.createSubTask("Subtask", "Description", epic.getId(),
            TaskType.SUB_TASK, FIRST_START, HALF_HOUR);

        assertEquals(2, subtask.getId());
        assertSame(subtask, epic.getSubTaskArray().get(subtask.getId()));
        assertTrue(manager.getPrioritizedTasks().contains(subtask));
    }

    @Test
    void rejectsSubtaskForUnknownEpic() {
        assertThrows(IllegalArgumentException.class, () -> manager.createSubTask(
            "Subtask", "Description", 99, TaskType.SUB_TASK, FIRST_START, HALF_HOUR));
    }

    @Test
    void updatesStatusOfRegularTask() {
        Task task = manager.createTask("tasks.Task", "Description", TaskType.TASK, FIRST_START, HALF_HOUR);

        manager.updateStatus(task.getId(), TaskType.TASK, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void epicIsNewWhenAfterCreation() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void epicIsNewWhenAllSubtasksAreNew() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);
        manager.createSubTask("Subtask", "Description", epic.getId(), TaskType.SUB_TASK,
            FIRST_START, HALF_HOUR);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void epicIsNewWhenDeleteLastSubtask() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);
        Task subtask = manager.createSubTask("Subtask", "Description", epic.getId(), TaskType.SUB_TASK,
            FIRST_START, HALF_HOUR);

        manager.updateStatus(subtask.getId(), TaskType.SUB_TASK, TaskStatus.DONE);
        manager.removeSubTask(subtask.getId());

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void epicIsDoneWhenAllSubtasksAreDone() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);
        Task first = manager.createSubTask("First", "Description", epic.getId(), TaskType.SUB_TASK,
            FIRST_START, HALF_HOUR);
        Task second = manager.createSubTask("Second", "Description", epic.getId(), TaskType.SUB_TASK,
            FIRST_START.plusHours(1), HALF_HOUR);

        manager.updateStatus(first.getId(), TaskType.SUB_TASK, TaskStatus.DONE);
        manager.updateStatus(second.getId(), TaskType.SUB_TASK, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void epicIsInProgressWhenSubtaskIsInProgress() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);
        Task subtask = manager.createSubTask("Subtask", "Description", epic.getId(),
            TaskType.SUB_TASK, FIRST_START, HALF_HOUR);

        manager.updateStatus(subtask.getId(), TaskType.SUB_TASK, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void epicIsInProgressWhenFirstSubtaskIsNewSecondSubtaskIsDone() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);
        Task first = manager.createSubTask("Subtask", "Description", epic.getId(),
            TaskType.SUB_TASK, FIRST_START, HALF_HOUR);
        Task second = manager.createSubTask("Subtask", "Description", epic.getId(),
            TaskType.SUB_TASK, FIRST_START.plusHours(1), HALF_HOUR);

        manager.updateStatus(second.getId(), TaskType.SUB_TASK, TaskStatus.DONE);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void checksTimeIntersections() {
        assertTrue(manager.timeCheck(FIRST_START, 60));
        assertFalse(manager.timeCheck(FIRST_START.plusMinutes(30), 30));
        assertTrue(manager.timeCheck(FIRST_START.plusHours(1), 30));
    }

    @Test
    void removesTaskFromStoragePrioritiesAndSchedule() {
        manager.timeCheck(FIRST_START, 30);
        Task task = manager.createTask("tasks.Task", "Description", TaskType.TASK, FIRST_START, HALF_HOUR);

        manager.removeTaskByType(TaskType.TASK, task.getId());

        assertFalse(manager.getBaseTask().containsKey(task.getId()));
        assertFalse(manager.getPrioritizedTasks().contains(task));
        assertTrue(manager.timeCheck(FIRST_START, 30));
    }

    @Test
    void removesSubtaskFromEpicPrioritiesAndSchedule() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);
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
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);
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
        manager.timeCheck(FIRST_START, 30);
        manager.createTask("tasks.Task", "Description", TaskType.TASK, FIRST_START, HALF_HOUR);
        manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);

        manager.removeAll();

        assertTrue(manager.getBaseTask().isEmpty());
        assertTrue(manager.getBaseEpic().isEmpty());
        assertTrue(manager.getPrioritizedTasks().isEmpty());
        assertTrue(manager.timeCheck(FIRST_START, 30));
    }

    @Test
    void ordersPrioritizedTasksByStartTime() {
        Task later = manager.createTask("Later", "Description", TaskType.TASK,
            FIRST_START.plusHours(2), HALF_HOUR);
        Task earlier = manager.createTask("Earlier", "Description", TaskType.TASK,
            FIRST_START, HALF_HOUR);

        assertEquals(List.of(earlier, later), new ArrayList<>(manager.getPrioritizedTasks()));
    }

    @Test
    void ordersPrioritizedTasksByStartTimeAfterRemoveMidTask() {
        Task later = manager.createTask("Later", "Description", TaskType.TASK,
            FIRST_START.plusHours(2), HALF_HOUR);
        Task mid = manager.createTask("Mid", "Description", TaskType.TASK,
            FIRST_START.plusHours(1), HALF_HOUR);
        Task earlier = manager.createTask("Earlier", "Description", TaskType.TASK,
            FIRST_START, HALF_HOUR);
        manager.removeTaskByType(mid.getTaskType(), mid.getId());

        assertEquals(List.of(earlier, later), new ArrayList<>(manager.getPrioritizedTasks()));
    }

    @Test
    void removeSubtaskWithTaskIdKeepsTask() {
        Task task = manager.createTask("tasks.Task", "Description", TaskType.TASK, FIRST_START, HALF_HOUR);
        int id = task.getId();

        removeMissingSubtask(id);

        Task stored = manager.getBaseTask().get(id);
        assertNotNull(stored);
        assertAll(
            () -> assertEquals(1, manager.getBaseTask().size()),
            () -> assertTrue(manager.getBaseEpic().isEmpty()),
            () -> assertEquals(1, manager.getPrioritizedTasks().size()),
            () -> assertTrue(manager.getPrioritizedTasks().contains(stored)),
            () -> assertEquals(id, stored.getId()),
            () -> assertEquals("tasks.Task", stored.getName()),
            () -> assertEquals("Description", stored.getDescription()),
            () -> assertEquals(TaskStatus.NEW, stored.getStatus()),
            () -> assertEquals(TaskType.TASK, stored.getTaskType()),
            () -> assertEquals(FIRST_START, stored.getStartTime()),
            () -> assertEquals(HALF_HOUR, stored.getDuration())
        );
    }

    private void removeMissingSubtask(int id) {
        try {
            manager.removeSubTask(id);
        } catch (IllegalArgumentException | NoSuchElementException expected) {
        }
    }

    @Test
    void removeUnknownSubtaskKeepsEmptyManager() {
        removeMissingSubtask(999);

        assertAll(
            () -> assertTrue(manager.getBaseTask().isEmpty()),
            () -> assertTrue(manager.getBaseEpic().isEmpty()),
            () -> assertTrue(manager.getPrioritizedTasks().isEmpty())
        );
    }

    @Test
    void emptyEpicStatusIsNew() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);

        manager.checkStatus(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void removingLastSubtaskResetsEpicToNew() {
        Epic epic = manager.createEpic("tasks.Epic", "Description", TaskType.EPIC);
        int epicId = epic.getId();
        Task subtask = manager.createSubTask("Subtask", "Description", epicId,
            TaskType.SUB_TASK, FIRST_START, HALF_HOUR);
        manager.updateStatus(subtask.getId(), TaskType.SUB_TASK, TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, epic.getStatus());

        manager.removeSubTask(subtask.getId());

        Epic storedEpic = manager.getBaseEpic().get(epicId);
        assertNotNull(storedEpic);
        assertAll(
            () -> assertEquals(1, manager.getBaseEpic().size()),
            () -> assertTrue(storedEpic.getSubTaskArray().isEmpty()),
            () -> assertEquals(TaskStatus.NEW, storedEpic.getStatus())
        );
    }
}
