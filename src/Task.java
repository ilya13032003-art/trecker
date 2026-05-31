import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int ID;
    protected String status;

    public Task(String name, String description, int ID) {
        this.name = name;
        this.description = description;
        this.ID = ID;
        status = "NEW";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return ID == task.ID && Objects.equals(name, task.name) && Objects.equals(description, task.description) &&
            Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + ID;
        result = 31 * result + Objects.hashCode(status);
        return result;
    }

    @Override
    public String toString() {
        return
            "==============================" +
                "\n Название задачи:" + name +
                "\n Описание:" + description +
                "\n Индефикатор:" + ID +
                "\n Статус:" + status;
    }

}
