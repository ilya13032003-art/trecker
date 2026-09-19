import java.time.Duration;
import java.time.LocalDateTime;


public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected TaskType taskType;
    protected LocalDateTime startTime;
    protected Duration duration;


    public Task() {
    }

    public Task(String name, String description, int id, TaskType taskType,
        LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        status = TaskStatus.NEW;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, int id, TaskStatus status, TaskType taskType,
        LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    protected LocalDateTime getEndTime() {
        LocalDateTime endTime = startTime.plus(duration);
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return
            "==============================" +
                "\n Название задачи:" + name +
                "\n Описание:" + description +
                "\n ID:" + id +
                "\n Статус:" + status +
                "\n Время старта" + startTime +
                "\n Время выполнения" + duration;
    }


}
