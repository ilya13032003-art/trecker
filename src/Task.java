import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected TaskType taskType;

    public Task() {
    }

    public Task(String name, String description, int id, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.id = id;
        status = TaskStatus.NEW;
        this.taskType = taskType;
    }

    public Task(String name, String description, int id, TaskStatus status, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.taskType = taskType;
    }

    public int getId() {
        return id;
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
                "\n Статус:" + status;
    }
}
