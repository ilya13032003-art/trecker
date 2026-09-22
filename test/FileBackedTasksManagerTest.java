import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest {
    @TempDir
    Path temporaryDirectory;

    private FileBackedTasksManager manager(Path file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        HistoryInMemory history = new HistoryInMemory();
        manager.setHistory(history);
        return manager;
    }

    @Test
    void savesCreatedTaskToProvidedFile() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        FileBackedTasksManager manager = manager(file);

        manager.createTask("Task", "Description", TaskType.TASK,
            LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30));

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertEquals("", lines.get(0));
        assertTrue(lines.get(1).contains("^TASK^Task^Description^NEW^"));
    }

    @Test
    void savesEpicAndItsSubtask() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        FileBackedTasksManager manager = manager(file);
        Epic epic = manager.createEpic("Epic", "Description", TaskType.EPIC);
        manager.createSubTask("Subtask", "Description", epic.getId(), TaskType.SUB_TASK,
            LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30));

        List<String> lines = Files.readAllLines(file);
        assertEquals(3, lines.size());
        assertTrue(lines.get(1).contains("^EPIC^Epic^Description^NEW"));
        assertTrue(lines.get(2).contains("^SUB_TASK^Subtask^Description^NEW^" + epic.getId() + "^"));
    }

    @Test
    void persistsUpdatedStatusAndDeletion() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        FileBackedTasksManager manager = manager(file);
        Task task = manager.createTask("Task", "Description", TaskType.TASK,
            LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30));

        manager.updateStatus(task.getId(), TaskType.TASK, TaskStatus.DONE);
        assertTrue(Files.readAllLines(file).get(1).contains("^DONE^"));

        manager.removeTaskByType(TaskType.TASK, task.getId());
        assertEquals(List.of(""), Files.readAllLines(file));
    }

    @Test
    void removesAllTasksFromFile() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        FileBackedTasksManager manager = manager(file);
        manager.createTask("Task", "Description", TaskType.TASK,
            LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30));

        manager.removeAll();

        assertEquals(List.of(""), Files.readAllLines(file));
        assertFalse(Files.size(file) == 0);
    }
}
