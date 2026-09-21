import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileManager {
    public static final Path TASK_FILE =
        Paths.get("taskFile.txt").toAbsolutePath();
    private final Path taskFile;

    public FileManager() {
        this(TASK_FILE);
    }

    public FileManager(Path taskFile) {
        this.taskFile = taskFile;
    }

    public List<String> readStr() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(taskFile.toFile()))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        }
    }

    public List<String[]> cutStr(List<String> lines) {
        List<String[]> newTasks = new ArrayList<>();
        for (String line : lines) {
            newTasks.add(line.split("\\^", -1));
        }
        return newTasks;
    }

    public List<String[]> cutTask() {
        try {
            return cutStr(readStr());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл задач", e);
        }
    }


    public Task stringToTask(String[] arr, HashMap<Integer, Task> baseTask,
                             HashMap<Integer, Epic> baseEpic) {
        int id = Integer.parseInt(arr[0]);
        TaskStatus status = TaskStatus.valueOf(arr[4]);
        TaskType type = TaskType.valueOf(arr[1]);
        Task task = null;
        if (TaskType.TASK.equals(type)) {
            LocalDateTime startTime = LocalDateTime.parse(arr[5]);
            Duration duration = Duration.parse(arr[6]);
            baseTask.put(id, new Task(arr[2], arr[3], id, status, type, startTime, duration));
            task = baseTask.get(id);
        } else if (TaskType.SUB_TASK.equals(type)) {
            int epicId = Integer.parseInt(arr[5]);
            LocalDateTime startTime = LocalDateTime.parse(arr[6]);
            Duration duration = Duration.parse(arr[7]);
            for (Epic epic : baseEpic.values()) {
                if (epicId == epic.id) {
                    epic.getSubTaskArray().put(id, new Task(arr[2], arr[3], id, status, type, startTime, duration));
                    epic.timing();
                    task = epic.getSubTaskArray().get(id);
                }
            }
        } else if (TaskType.EPIC.equals(type)) {
            baseEpic.put(id, new Epic(arr[2], arr[3], id, status, type));
            task = baseEpic.get(id);
        }
        return task;
    }
}
