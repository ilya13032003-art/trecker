
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

public interface TaskManager {

    boolean timeCheck(LocalDateTime startTime, int durationInt);
    Task createTask(String name, String description, TaskType taskType, LocalDateTime startTime, Duration duration);
    Epic createEpic(String name, String description, TaskType taskType);
    Task createSubTask(String name, String description, int epicId, TaskType taskType, LocalDateTime startTime, Duration duration);
    void updateStatus(int taskID, int taskType, int status);
    void removeTaskByType(int taskType, int choiceId);
    void removeSubTask(int choiceId);
    int setIndicator();
    int getIndicator();
    void removeAll();
    Set<Task> getPrioritizedTasks();
    HashMap<Integer, Task> getBaseTask();
    HashMap<Integer, Epic> getBaseEpic();

}
