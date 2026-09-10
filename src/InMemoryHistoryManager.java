import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    protected LinkedHashSet<Task> arrayHistory = new LinkedHashSet<>();

    public LinkedHashSet<Task> getArrayHistory() {
        return arrayHistory;
    }

    public LinkedHashSet<Task> setArrayHistory() {
        getArrayHistory().clear();
        return arrayHistory;
    }

    @Override
    public void add(Task task) {
        boolean isContains = getArrayHistory().add(task);
        if (!isContains) {
            getArrayHistory().remove(task);
            getArrayHistory().add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>(getArrayHistory());
        return history;
    }

    @Override
    public void removeInHistory(int taskId) {
        Task empty = new Task();
        for (Task task : getArrayHistory()) {
            if (task.id == taskId) {
                empty = task;
            }
        }
        getArrayHistory().remove(empty);
    }
}
