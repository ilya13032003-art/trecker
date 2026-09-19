import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    protected int indicator = 0;
    protected static final int SLOT_MINUTES = 5;
    protected static final int SLOTS_PER_YEAR = 365 * 24 * 12;
    protected static final LocalDateTime DAY_Z = LocalDateTime.of(2026, 9, 17, 0,0);
    //именно в этот день я сел писать эту часть кода, задачам можно будет присваивать время до 1 года вперёд от этой даты

    protected HashMap<Integer, Task> baseTask = new HashMap<>();
    protected HashMap<Integer, Epic> baseEpic = new HashMap<>();
    protected boolean[] slots =  new boolean[SLOTS_PER_YEAR];

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    protected final Set<Task> prioritizedTasks =
        new TreeSet<>(Comparator.comparing(task -> task.startTime));

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
    public Task createTask(String name, String description, TaskType taskType, LocalDateTime startTime, Duration duration) {
        Task task = new Task(name, description, ++indicator, taskType, startTime, duration);
        baseTask.put(task.id, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(String name, String description, TaskType taskType) {
        Epic epic = new Epic(name, description, ++indicator, taskType);
        baseEpic.put(epic.id, epic);
        return epic;
    }

    @Override
    public Task createSubTask(String name, String description, int epicId,
        TaskType taskType, LocalDateTime startTime, Duration duration) {
        Epic epic = baseEpic.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Эпика с таким ID нет");
        }

        Task task = new Task(name, description, ++indicator, taskType, startTime, duration);
        epic.getSubTaskArray().put(task.id, task);
        epic.timing();
        checkStatus(epic);
        prioritizedTasks.add(task);
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
                Task task = getBaseTask().get(taskId);
                int startSlot = dateInSlot(task.startTime);
                for (int i = startSlot; i < startSlot + task.duration.toMinutes() / 5; i++) {
                    slots[i] = false;
                }
                prioritizedTasks.remove(getBaseTask().get(taskId));
                getBaseTask().remove(taskId);
                System.out.println("Задача удалена");
            }
        } else if (taskType == 2) {
            if (getBaseEpic().get(taskId) == null) {
                System.out.println("Такого эпика - нет");
            } else {
                for (Task task : getBaseEpic().get(taskId).getSubTaskArray().values()) {
                    int startSlot = dateInSlot(task.startTime);
                    prioritizedTasks.remove(task);
                    for (int i = startSlot; i < startSlot + task.duration.toMinutes() / 5; i++) {
                        slots[i] = false;
                    }
                }
                getBaseEpic().remove(taskId);
                System.out.println("Эпик удален");
            }
        }
    }

    @Override
    public void removeSubTask(int taskId) {
        Epic epic = searchEpic(taskId);
        Task task = epic.getSubTaskArray().get(taskId);
        if (epic == null) {
            System.out.println("Подзадачи с таким ID - нет");
        } else {
            int startSlot = dateInSlot(task.startTime);
            for (int i = startSlot; i < startSlot + task.duration.toMinutes() / 5; i++) {
                slots[i] = false;
            }
            prioritizedTasks.remove(task);
            epic.getSubTaskArray().remove(taskId);
            if (!epic.getSubTaskArray().isEmpty()) {
                epic.timing();
            } else {
                epic.duration = null;
                epic.startTime = null;
            }
            System.out.println("Подзадача удалена");
            checkStatus(epic);
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
        for (int i = 0; i < slots.length; i++) {
            slots[i] = false;
        }
        System.out.println("Все задачи удалены");
        prioritizedTasks.clear();
    }

    protected boolean isFree(int startSlot, int numberOfSlots) {
        for (int i = startSlot; i < startSlot + numberOfSlots; i++) {
            if (slots[i]) {
                return false;
            }
        }
        return true;
    }

    protected void occupy(int startSlot, int numberOfSlots) {
        if (isFree(startSlot, numberOfSlots)) {
            for (int i = startSlot; i < startSlot + numberOfSlots; i++) {
                slots[i] = true;
            }
        }
    }

    protected Integer dateInSlot(LocalDateTime startTime) {
        Duration difference = Duration.between(DAY_Z, startTime);
        int min = (int) difference.toMinutes();
        int startSlot = min / 5;
        return startSlot;
    }

    @Override
    public boolean timeCheck(LocalDateTime startTime, int durationInt) {
        int startSlot = dateInSlot(startTime);
        int numberOfSlots = durationInt / 5;
        if (isFree(startSlot, numberOfSlots)) {
            occupy(startSlot, numberOfSlots);
            return true;
        } else {
            return false;
        }
    }
}




