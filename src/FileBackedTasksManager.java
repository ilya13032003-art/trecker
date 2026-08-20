import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBackedTasksManager extends Manager {

    FileManager fileManager = new FileManager();

    @Override
    public void createTaskByType(String name, String description, int type) { //да, это ужас, понимаю, франкенштейн какойто
        super.createTaskByType(name, description, type);                      //понимаю что неправильно такое оставлять, но силы
        int id = getIndicator();                                              //покидают меня, мб в след спринте перепишу
        String str = null;
        if (type == 2) {
            Epic epic = getBaseEpic().get(id);
            str = fileManager.taskToString(epic) + ",";
        } else if (type == 1) {
            Task task = getBaseTask().get(id);
            str = fileManager.taskToString(task);
        } else if (type == 3) {
            for (Epic epic : baseEpic.values()) {
                for (int idSubTask : epic.getSubTaskArray().keySet()) {
                    if (id == idSubTask) {
                        Task task = epic.getSubTaskArray().get(id);
                        str = fileManager.taskToString(task) + "," + "subTask";

                        String strId = String.join(",", epic.createSubTasksId());
                        String strEpic = fileManager.taskToString(epic) + "," + strId;
                        String[] changeElement = strEpic.split(",", -1);

                        List<String[]> newTasks = fileManager.cutTask();
                        for (int i = 1; i < newTasks.size(); i++) {
                            String[] qq = newTasks.get(i);
                            if (qq.length == 0 || qq[0] == null || qq[0].equals("null")) continue;

                            int epicId = Integer.parseInt(qq[0]);
                            if (epic.id == epicId) {
                                newTasks.set(i, changeElement);
                            }
                        }
                        try {
                            fileManager.rewriting(newTasks);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                if (str != null) break;
            }
        }
        if (str != null && !str.isEmpty()) {
            try {
                fileManager.save(str + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateStatus(int taskID, int taskType, int status) {
        super.updateStatus(taskID, taskType, status);
        List<String[]> newTask = fileManager.cutTask();
        for (int i = 1; i < newTask.size(); i++) {
            String[] task = newTask.get(i);
            int id = Integer.parseInt(task[0]);
            if (id == taskID) {
                if (status == 1) {
                    task[3] = "NEW";
                } else if (status == 2) {
                    task[3] = "IN_PROGRESS";
                } else {
                    task[3] = "DONE";
                }
            }
        }
        try {
            fileManager.rewriting(newTask);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeTaskByType(int taskType, int taskId) {
        super.removeTaskByType(taskType, taskId);
        if (taskType == 1) {
            fileManager.removeTaskInFile(taskId);
        } else {
            List<String> subTaskId = fileManager.removeTaskInFile(taskId);
            for (String str : subTaskId) {
                int id = Integer.parseInt(str);
                fileManager.removeTaskInFile(id);
            }
        }
    }

    @Override
    public void removeSubTask(int taskId) {
        super.removeSubTask(taskId);
        fileManager.removeTaskInFile(taskId);
        int iInd = 0;
        int jInd = 0;
        List<String[]> tasks = fileManager.cutTask();
        for (int i = 1; i < tasks.size(); i++) {
            String[] task = tasks.get(i);
            if (task.length < 5 || "subTask".equals((task[4]))) continue;
            for (int j = 4; j < task.length; j++) {
                int idSubTask = Integer.parseInt(task[j]);
                if (taskId == idSubTask) {
                    jInd = j;
                    iInd = i;
                    break;
                }
            }
        }
        List<String> newTask = new ArrayList<>(Arrays.asList(tasks.get(iInd)));
        newTask.remove(jInd);
        String[] arr = newTask.toArray(new String[0]);
        tasks.set(iInd, arr);
        try {
            fileManager.rewriting(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkStatus(Epic epic) {
        super.checkStatus(epic);
        String str = String.valueOf(epic.status);
        List<String[]> newTask = fileManager.cutTask();
        for (int i = 1; i < newTask.size(); i++) {
            String[] task = newTask.get(i);
            int id = Integer.parseInt(task[0]);
            if (id == epic.id) {
                task[3] = str;
            }
            try {
                fileManager.rewriting(newTask);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void removeAll() {
        super.removeAll();
        String str = "-";
        try (Writer fileWriter = new FileWriter("taskFile.txt", false)) {
            fileWriter.write(str + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }

