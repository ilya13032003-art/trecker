import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Epic extends Task {

    public Epic(String name, String description, int id, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.taskType = taskType;
        this.status = TaskStatus.NEW;
    }

    public Epic(String name, String description, int id, TaskStatus status, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.taskType = taskType;
    }

    private HashMap<Integer, Task> subTaskArray = new HashMap<>();

    public HashMap<Integer, Task> getSubTaskArray() {
        return subTaskArray;
    }

    @Override
    public String toString() {
        return
            "==============================" +
                "\n Название эпика:" + name +
                "\n Описание:" + description +
                "\n ID:" + id +
                "\n Статус:" + status +
                "\n ID его подзадач:" + getSubTasksId();
    }

    public List<Integer> getSubTasksId() {
        List<Integer> subTasksId = new ArrayList<>();
        for (Integer id : getSubTaskArray().keySet()) {
            subTasksId.add(id);
        }
        return subTasksId;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime endTime = null;
        for (Task task : getSubTaskArray().values()) {
            if (endTime == null
                || task.getEndTime().isAfter(endTime)) {
                endTime = task.getEndTime();
            }
        }
        return endTime;
    }

    public LocalDateTime getStartTime() {
        startTime =  null;
        for (Task task : getSubTaskArray().values()) {
            if (startTime == null
                || task.startTime.isBefore(startTime)) {
                startTime = task.startTime;
            }
        }
        return startTime;
    }

    public Duration getDuration() {
        LocalDateTime start = getStartTime();
        LocalDateTime end = getEndTime();
        if (start == null || end == null) {
            return Duration.ZERO;
        }
        return Duration.between(start, end);
    }

    public void timing() {
        getStartTime();
        getDuration();
    }
}
