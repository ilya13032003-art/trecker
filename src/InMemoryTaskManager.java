import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    protected int indicator = 0;

    protected HashMap<Integer, Task> baseTask = new HashMap<>();
    protected HashMap<Integer, Epic> baseEpic = new HashMap<>();

    public HashMap<Integer, Task> getBaseTask() {
        return baseTask;
    }

    public HashMap<Integer, Epic> getBaseEpic() {
        return baseEpic;
    }

    @Override
    public int setIndicator() {
        return indicator++;
    }

    @Override
    public int getIndicator() {
        return indicator;
    }

    @Override
    public Task createTask(String name, String description, TaskType taskType) {
        Task task = new Task(name, description, ++indicator, taskType);
        baseTask.put(task.id, task);
        return task;
    }

    @Override
    public Epic createEpic(String name, String description, TaskType taskType) {
        Epic epic = new Epic(name, description, ++indicator, taskType);
        baseEpic.put(epic.id, epic);
        return epic;
    }

    @Override
    public Task createSubTask(String name, String description, int epicId, TaskType taskType) {
        Epic epic = baseEpic.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Эпика с таким ID нет");
        }

        Task task = new Task(name, description, ++indicator, taskType);
        epic.getSubTaskArray().put(task.id, task);
        checkStatus(epic);
        return task;
    }

    @Override
    public void updateStatus(int taskID, int taskType, int status) {
        if (taskType == 1 && !getBaseTask().containsKey(taskID)) {
            System.out.println("Задачи с таким ID нет");
        } else if (taskType == 2 && searchEpic(taskID) == null) {
            System.out.println("Задачи с таким ID нет");
        } else {
            switch (status) {
                case 1:
                    updateTaskStatus(taskType, taskID, TaskStatus.NEW);
                    return;
                case 2:
                    updateTaskStatus(taskType, taskID, TaskStatus.IN_PROGRESS);
                    return;
                case 3:
                    updateTaskStatus(taskType,  taskID, TaskStatus.DONE);
            }
        }
    }

    protected void updateTaskStatus(int taskType, int taskID, TaskStatus status) {
        if (taskType == 1) {
            getBaseTask().get(taskID).status = status;
        } else {
            searchEpic(taskID).getSubTaskArray().get(taskID).status = status;
            checkStatus(searchEpic(taskID));
        }
    }

    @Override
    public void removeTaskByType(int taskType, int taskId) {
        if (taskType == 1) {
            if (getBaseTask().get(taskId) == null) {
                System.out.println("Такой задачи нет");
            } else {
                getBaseTask().remove(taskId);
                System.out.println("Задача удалена");
            }
        } else if (taskType == 2) {
            if (getBaseEpic().get(taskId) == null) {
                System.out.println("Такого эпика - нет");
            } else {
                getBaseEpic().remove(taskId);
                System.out.println("Эпик удален");
            }
        }
    }

    @Override
    public void removeSubTask(int taskId) {
        if (searchEpic(taskId) == null) {
            System.out.println("Подзадачи с таким ID - нет");
        } else {
            searchEpic(taskId).getSubTaskArray().remove(taskId);
            System.out.println("Подзадача удалена");
            checkStatus(searchEpic(taskId));
        }
    }

    protected Epic searchEpic(int epicId) {
        Epic epic = null;
        for (Epic ep : getBaseEpic().values()) {
            if (ep.getSubTaskArray().containsKey(epicId)) {
                epic = ep;
            }
        }
        return epic;
    }

    public void checkStatus(Epic epic) {
        boolean done = true;
        boolean neww = true;
        for (Task subTask : epic.getSubTaskArray().values()) {
            switch (subTask.status) {
                case IN_PROGRESS -> {
                    done = false;
                    neww = false;
                }
                case NEW -> done = false;
                case DONE -> neww = false;
            }
        }
        if (done && !neww) {
            epic.status = TaskStatus.DONE;
        } else if (neww && !done) {
            epic.status = TaskStatus.NEW;
        } else {
            epic.status = TaskStatus.IN_PROGRESS;
        }
    }

    @Override
    public void removeAll() {
        getBaseTask().clear();
        getBaseEpic().clear();
        System.out.println("Все задачи удалены");
    }
}




