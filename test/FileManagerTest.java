import java.time.Duration;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class FileManagerTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void readsRecordsFromConfiguredFile() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        List<String> expected = List.of("", "1^EPIC^Epic^Description^NEW");
        Files.write(file, expected);
        FileManager fileManager = new FileManager(file);

        assertEquals(expected, fileManager.readStr());
        assertEquals(2, fileManager.cutTask().size());
    }

    @Test
    void splitsRecordsWithoutDroppingEmptyFields() {
        FileManager fileManager = new FileManager();

        List<String[]> records = fileManager.cutStr(List.of("1^TASK^Name^^NEW^2026-09-20T10:00^PT30M"));

        assertArrayEquals(new String[] {"1", "TASK", "Name", "", "NEW", "2026-09-20T10:00", "PT30M"},
            records.get(0));
    }

    @Test
    void restoresRegularTaskFromRecord() {
        FileManager fileManager = new FileManager();
        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        String[] record = {"7", "TASK", "Task", "Description", "IN_PROGRESS", "2026-09-20T10:00", "PT30M"};

        Task task = fileManager.stringToTask(record, tasks, epics);

        assertSame(task, tasks.get(7));
        assertEquals(TaskStatus.IN_PROGRESS, task.status);
        assertEquals(LocalDateTime.of(2026, 9, 20, 10, 0), task.startTime);
        assertEquals(Duration.ofMinutes(30), task.duration);
    }

    @Test
    void restoresEpicAndSubtaskFromRecords() {
        FileManager fileManager = new FileManager();
        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        Epic epic = (Epic) fileManager.stringToTask(
            new String[] {"1", "EPIC", "Epic", "Description", "NEW"}, tasks, epics);

        Task subtask = fileManager.stringToTask(
            new String[] {"2", "SUB_TASK", "Subtask", "Description", "DONE", "1", "2026-09-20T10:00", "PT30M"},
            tasks, epics);

        assertSame(epic, epics.get(1));
        assertSame(subtask, epic.getSubTaskArray().get(2));
        assertEquals(TaskStatus.DONE, subtask.status);
    }
}
