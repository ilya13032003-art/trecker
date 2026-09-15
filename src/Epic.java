import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    public Epic(String name, String description, int id, TaskType taskType) {
        super(name, description, id, taskType);
    }

    public Epic(String name, String description, int id, TaskStatus status, TaskType taskType) {
        super(name, description, id, taskType);
        this.status = status;
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
}
