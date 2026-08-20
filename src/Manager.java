import java.util.HashMap;
import java.util.Scanner;

public class Manager implements  TaskManager {

    static Scanner scanner = new Scanner(System.in);
    protected static int indicator = 0;

    protected HashMap<Integer, Task> baseTask = new HashMap<>();
    protected HashMap<Integer, Epic> baseEpic = new HashMap<>();

    public HashMap<Integer, Task> getBaseTask() {
        return baseTask;
    }

    public HashMap<Integer, Epic> getBaseEpic() {
        return baseEpic;
    }

    public static int setIndicator() {
        return indicator++;
    }

    public static int getIndicator() {
        return indicator;
    }

    @Override
    public void createTaskByType(String name, String description, int type) {
        setIndicator();
        if (type == 1) {
            Task task = new Task(name, description, getIndicator());
            getBaseTask().put(task.id, task);
        } else if (type == 2) {
            Epic task = new Epic(name, description, getIndicator());
            getBaseEpic().put(task.id, task);
        } else if (type == 3) {
            while (true) {
                System.out.println("Введите ID эпика, которому принадлежит эта подзадача");
                int iD = scanner.nextInt();
                if (getBaseEpic().containsKey(iD)) {
                    Task task = new Task(name, description, getIndicator());
                    getBaseEpic().get(iD).getSubTaskArray().put(getIndicator(), task);
                    checkStatus(getBaseEpic().get(iD));
                    break;
                } else {
                    System.out.println("Эпика с таким ID нет");
                }
            }
        }
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

    protected void removeAll() {
        getBaseTask().clear();
        getBaseEpic().clear();
        System.out.println("Все задачи удалены");
    }
}




