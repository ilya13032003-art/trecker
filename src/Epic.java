import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {

    HashMap<Integer, Task> subTaskArray = new HashMap<>();

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public void checkStatus() {
        boolean done = true;
        boolean nEw = true;
            for (Task subTask : subTaskArray.values()) {
                switch (subTask.status) {
                    case "IN_PROGRESS" -> {
                        done = false;
                        nEw = false;
                    }
                    case "NEW" -> done = false;
                    case "DONE" -> nEw = false;
                }
            }
            if (done && !nEw) {
                status = "DONE";
            } else if (nEw && !done) {
                status = "NEW";
            } else {
                status = "IN_PROGRESS";
            }
    }

    @Override
    public String toString() {
        return
            "==============================" +
                "\n Название эпика:" + name +
                "\n Описание:" + description +
                "\n ID:" + id +
                "\n Статус:" + status +
                "\n ID его подзадач:" + getSubTaskID();
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
        return Objects.equals(subTaskArray, epic.subTaskArray);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(subTaskArray);
        return result;
    }

    private ArrayList<Integer> getSubTaskID() {
        ArrayList<Integer> arrays = new ArrayList<>();
        for (Task task : subTaskArray.values()) {
            arrays.add(task.id);
        }
        return arrays;
    }
}
