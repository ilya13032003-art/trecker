
import java.util.HashMap;

public interface TaskManager {

    Task createTask(String name, String description, TaskType taskType);
    Epic createEpic(String name, String description, TaskType taskType);
    Task createSubTask(String name, String description, int epicId, TaskType taskType);
    void updateStatus(int taskID, int taskType, int status);
    void removeTaskByType(int taskType, int choiceId);
    void removeSubTask(int choiceId);
    HashMap<Integer, Task> getBaseTask();
    HashMap<Integer, Epic> getBaseEpic();
    int setIndicator();
    int getIndicator();
    void removeAll();

}
