import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public Epic(String name, String description, int id, TaskStatus status, ArrayList<String> subTasksId) {
        super(name, description, id);
        this.status = status;
        this.subTasksId = subTasksId;
    }

    private HashMap<Integer, Task> subTaskArray = new HashMap<>();

    public List<String> getSubTasksId() {
        return subTasksId;
    }

    private List<String> subTasksId = new ArrayList<>();

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
                "\n ID его подзадач:" + createSubTasksId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Epic epic = (Epic) o;
        return Objects.equals(getSubTaskArray(), epic.getSubTaskArray());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(getSubTaskArray());
        return result;
    }

    public List<String> createSubTasksId() {
        getSubTasksId().clear();
        for (Task task : getSubTaskArray().values()) {
            String strId = Integer.toString(task.id);
            subTasksId.add(strId);
        }
        return subTasksId;
    }
}
