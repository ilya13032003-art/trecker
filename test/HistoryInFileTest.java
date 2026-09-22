import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryInFileTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void savesHistoryAfterAddingAndRemovingViewedTask() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        HistoryInFile history = new HistoryInFile();
        manager.setHistory(history);
        history.setManager(manager);
        Task task = manager.createTask("Task", "Description", TaskType.TASK,
            LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30));

        history.add(task);
        assertEquals("1", Files.readAllLines(file).get(0));

        history.removeInHistory(task.getId());
        assertEquals("", Files.readAllLines(file).get(0));
    }

    @Test
    void savesEmptyHistoryAfterClear() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        HistoryInFile history = new HistoryInFile();
        manager.setHistory(history);
        history.setManager(manager);
        Task task = manager.createTask("Task", "Description", TaskType.TASK,
            LocalDateTime.of(2026, 9, 20, 10, 0), Duration.ofMinutes(30));
        history.add(task);

        history.clearHistory();

        assertEquals("", Files.readAllLines(file).get(0));
    }
}
