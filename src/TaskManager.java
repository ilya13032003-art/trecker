
public interface TaskManager {

    void createTaskByType(String name, String description, int type);
    void updateStatus(int taskID, int taskType, int status);
    void removeTaskByType(int taskType, int choiceId);
    void removeSubTask(int choiceId);

    }
