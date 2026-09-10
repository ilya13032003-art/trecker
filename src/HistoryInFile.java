import java.io.IOException;

public class HistoryInFile extends InMemoryHistoryManager {

    FileManager fileManager = new FileManager();

    @Override
    public void add(Task task) {
        super.add(task);
        try {
            fileManager.historyInFile(getArrayHistory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     @Override
     public void removeInHistory(int taskId) {
        super.removeInHistory(taskId);
         try {
             fileManager.historyInFile(getArrayHistory());
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
     }
