import java.util.LinkedHashSet;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory(LinkedHashSet<Task> browsingHistory);
    void removeInHistory (LinkedHashSet<Task> browsingHistory, int choiceId);
}
