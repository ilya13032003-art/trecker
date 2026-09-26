import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверки удаления и статуса эпика из АКТУАЛЬНЫЕ_ЗАМЕЧАНИЯ.md и пример параметризации.
 * Проверки удаления и статуса ожидают исправлений; пример сохранения текста уже проходит.
 * Проверяют результат операций, а не число вызовов save() или расходование ID.
 */
class ActualRemarksTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 20, 10, 0);
    private static final Duration DURATION = Duration.ofMinutes(30);
    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    /**
     * Неизвестный ID обрабатывается без случайного исключения и изменения данных.
     * Допустим обычный возврат либо явный отказ через IllegalArgumentException
     * или NoSuchElementException (включая их подклассы).
     */
    @Test
    void removeUnknownSubtaskKeepsEmptyManager() {
        removeMissingSubtask(999);

        assertAll(
                () -> assertTrue(manager.getBaseTask().isEmpty()),
                () -> assertTrue(manager.getBaseEpic().isEmpty()),
                () -> assertTrue(manager.getPrioritizedTasks().isEmpty())
        );
    }

    /**
     * Ошибочный выбор типа при удалении не должен удалить или изменить обычную задачу.
     */
    @Test
    void removeSubtaskWithTaskIdKeepsTask() {
        Task task = manager.createTask("Task", "Description", TaskType.TASK, START, DURATION);
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
                () -> assertEquals("Task", stored.name),
                () -> assertEquals("Description", stored.description),
                () -> assertEquals(TaskStatus.NEW, stored.status),
                () -> assertEquals(TaskType.TASK, stored.taskType),
                () -> assertEquals(START, stored.startTime),
                () -> assertEquals(DURATION, stored.duration)
        );
    }

    /**
     * Пересчёт статуса изначально пустого эпика должен сохранять NEW.
     */
    @Test
    void emptyEpicStatusIsNew() {
        Epic epic = manager.createEpic("Epic", "Description", TaskType.EPIC);

        manager.checkStatus(epic);

        assertEquals(TaskStatus.NEW, epic.status);
    }

    /**
     * Удаление последней завершённой подзадачи оставляет пустой эпик со статусом NEW.
     */
    @Test
    void removingLastSubtaskResetsEpicToNew() {
        Epic epic = manager.createEpic("Epic", "Description", TaskType.EPIC);
        int epicId = epic.getId();
        Task subtask = manager.createSubTask("Subtask", "Description", epicId,
                TaskType.SUB_TASK, START, DURATION);
        manager.updateStatus(subtask.getId(), TaskType.SUB_TASK, TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, epic.status);

        manager.removeSubTask(subtask.getId());

        Epic storedEpic = manager.getBaseEpic().get(epicId);
        assertNotNull(storedEpic);
        assertAll(
                () -> assertEquals(1, manager.getBaseEpic().size()),
                () -> assertTrue(storedEpic.getSubTaskArray().isEmpty()),
                () -> assertEquals(TaskStatus.NEW, storedEpic.status)
        );
    }

    private void removeMissingSubtask(int id) {
        try {
            manager.removeSubTask(id);
        } catch (IllegalArgumentException | NoSuchElementException expected) {
            // Оба типа выражают явный отказ; NPE и другие случайные ошибки не перехватываются.
        }
    }
}
