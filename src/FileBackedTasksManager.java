import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private HistoryInMemory historyInMemory;

    public void setHistory(HistoryInMemory historyInMemory) {
        this.historyInMemory = historyInMemory;
    }

    @Override
    public Task createTask(String name, String description, TaskType taskType, LocalDateTime startTask, Duration duration) {
        Task task = super.createTask(name, description, taskType, startTask, duration);
        save();
        return task;
    }

    @Override
    public Epic createEpic(String name, String description, TaskType taskType) {
        Epic epic = super.createEpic(name, description, taskType);
        save();
        return epic;
    }

    @Override
    public Task createSubTask(String name, String description, int epicId,
        TaskType taskType, LocalDateTime startTask, Duration duration) {
        Task task = super.createSubTask(name, description, epicId, taskType, startTask, duration);
        save();
        return task;
    }

    @Override
    public void updateStatus(int taskID, int taskType, int status) {
        super.updateStatus(taskID, taskType, status);
        save();
    }

    @Override
    public void removeTaskByType(int taskType, int taskId) {
        super.removeTaskByType(taskType, taskId);
        save();
    }

    @Override
    public void removeSubTask(int taskId) {
        super.removeSubTask(taskId);
        save();
    }

    @Override
    public void removeAll() {
        super.removeAll();
        save();
    }

    private String taskToString(Task task) {
        String str = null;
        if (TaskType.SUB_TASK.equals(task.taskType)) {
            for (Epic epic : baseEpic.values()) {
                for (int id : epic.getSubTasksId()) {
                    if (task.id == id) {
                        str = task.id + "^" + task.taskType + "^" + task.name + "^"
                            + task.description + "^" + task.status + "^" + epic.id + "^" + task.startTime + "^" + task.duration;
                    }
                }
            }
        } else if (TaskType.TASK.equals(task.taskType)){
            str = task.id + "^" + task.taskType + "^" + task.name + "^"
                + task.description + "^" + task.status + "^" + task.startTime + "^" + task.duration;
        } else {
            str = task.id + "^" + task.taskType + "^" + task.name + "^"
                + task.description + "^" + task.status;
        }
        return str;
    }

    public void save() {
        List<String> allTasks = new ArrayList<>(
            Stream.concat(
                    Stream.concat(baseTask.values().stream(), baseEpic.values().stream()),
                    baseEpic.values().stream()
                        .flatMap(epic -> epic.getSubTaskArray().values().stream())
                )
                .sorted((task1, task2) -> Integer.compare(task1.getId(), task2.getId()))
                .map(this::taskToString)
                .toList()
        );

        allTasks.add(0, historyInStr(historyInMemory.getArrayHistory()));

        try (Writer writer = new FileWriter(FileManager.TASK_FILE.toFile())) {
            for (String task : allTasks) {
                writer.write(task);
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить задачи в файл", e);
        }
    }

    private String historyInStr(LinkedHashSet<Task> arrayHistory) {
        List<String> history = new ArrayList<>();
        for (Task task : arrayHistory) {
            history.add(String.valueOf(task.id));
        }
        return String.join(",", history);
    }

    @Override
    public boolean timeCheck(LocalDateTime startTime, int durationInt) {
        return super.timeCheck(startTime, durationInt);
    }
    }
