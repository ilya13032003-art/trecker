import java.util.ArrayList;
import java.util.Objects;


public class Epic {
    String name;
    String description;
    int identifier;
    String status;
    ArrayList<String> subTaskArray = new ArrayList<>();

public Epic(String name, String description, int identifier) {
    this.name = name;
    this.description = description;
    this.identifier = identifier;
    status = "NEW";
}


    @Override
    public String toString() {
        return
            "==============================" +
            "\n Название задачи:" + name +
            "\n Описание:" + description +
            "\n Индефикатор:" + identifier +
            "\n Статус:" + status +
            "\n Её подзадачи:" + subTaskArray;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Epic epic = (Epic) o;
        return identifier == epic.identifier && Objects.equals(name, epic.name) && Objects.equals(description, epic.description) &&
            Objects.equals(status, epic.status) && Objects.equals(subTaskArray, epic.subTaskArray);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + identifier;
        result = 31 * result + Objects.hashCode(status);
        result = 31 * result + Objects.hashCode(subTaskArray);
        return result;
    }
}
