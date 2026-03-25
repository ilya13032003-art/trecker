import java.util.Objects;

public class SubTask extends Epic {
    String epicName;

    public SubTask(String name, String description, int identifier, String epicName, String status) {
        super(name, description, identifier);
        this.epicName = epicName;
        this.status = status;
    }

    @Override
    public String toString() {
        return
            "==============================" +
                "\n Название подзадачи:" + name +
                "\n Относится к задаче:" + epicName +
                "\n Описание:" + description +
                "\n Индефикатор:" + identifier +
                "\n Статус:" + status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SubTask subTask = (SubTask) o;
        return Objects.equals(epicName, subTask.epicName);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(epicName);
        return result;
    }
}
