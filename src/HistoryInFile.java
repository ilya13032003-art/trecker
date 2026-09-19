public class HistoryInFile extends HistoryInMemory {
    private FileBackedTasksManager manager;

    public void setManager(FileBackedTasksManager manager) {
        this.manager = manager;
    }

    @Override
    public void add(Task task) {
        super.add(task);
        manager.save();
    }

    @Override
    public void removeInHistory(int taskId) {
        super.removeInHistory(taskId);
        manager.save();
    }

    @Override
    public void clearHistory() {
        super.clearHistory();
        manager.save();
    }
}
