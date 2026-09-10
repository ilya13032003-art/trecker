import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class FileManager {



    public void save(String str) throws IOException {
        try (Writer fileWriter = new FileWriter("taskFile.txt", true)) {
            fileWriter.write(str);
        }
    }

    public String taskToString(Task task) {
        String str = task.id + "," + task.name + "," + task.description + "," + task.status;
        return str;
    }

    public List<String> readStr() throws IOException {
        FileReader reader = new FileReader("taskFile.txt");
        BufferedReader br = new BufferedReader(reader);
        List<String> lines = new ArrayList<>();
        while (br.ready()) {
            String line = br.readLine();
            lines.add(line);
        }
        br.close();
        return lines;
    }

    public List<String[]> cutStr(List<String> lines) {
        List<String[]> newTasks = new ArrayList<>();
        for (String line : lines) {
            String[] task = line.split(",", -1);
            newTasks.add(task);
        }
        return newTasks;
    }

    public List<String[]> cutTask() { //возвращает список порезанных строк
        List<String> lines;
        try {
            lines = readStr();  //разрезал csv на строки
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String[]> newTask = cutStr(lines);//каждую строку разрезал на составляющие для задачи
        return newTask;
    }

    public void stringToTask(String[] arr, HashMap<Integer, Task> baseTask, HashMap<Integer, Epic> baseEpic) {
        int id = Integer.parseInt(arr[0]);
        TaskStatus status = TaskStatus.valueOf(arr[3]);
        if (arr.length == 4) {
            Task task = new Task(arr[1], arr[2], id, status);
            baseTask.put(id, task);
        } else if ("subTask".equals(arr[4])) {
            Task task = new Task(arr[1], arr[2], id, status);
            for (Epic epic : baseEpic.values()) {
                for (String idSubTask : epic.getSubTasksId()) {
                    if (arr[0].equals(idSubTask)) {
                        epic.getSubTaskArray().put(id, task);
                    }
                }
            }
        } else {
            ArrayList<String> subTasksId = new ArrayList<>();
            for (int i = 4; i < arr.length; i++) {
                subTasksId.add(arr[i]);
            }
            Epic epic = new Epic(arr[1], arr[2], id, status, subTasksId);
            baseEpic.put(id, epic);
        }
    }

    public List<String> removeTaskInFile (int idTask) {
        List<String> subTaskId = new ArrayList<>();
        List<String[]> tasks = cutTask();
        List<String[]> newTasks = new ArrayList<>();
        for (int i = 1; i < tasks.size(); i++) {
            String[] task = tasks.get(i);
            int id = Integer.parseInt(task[0]);
            if (idTask == id) {
                if (task.length == 5 && !"subTask".equals(task[4])) {
                    String[] subTask = task[4].split(",");
                    subTaskId.addAll(Arrays.asList(subTask));
                }
            } else {
                newTasks.add(task);
            }
        }
        String[] empty = new String[1]; // rewriting(newTasks); начинает итерацию с i = 1, поэтому кинул пустышку на съедение
        newTasks.add(0, empty);
        try {
            rewriting(newTasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subTaskId;
    }

    public void rewriting(List<String[]> newTasks) throws IOException {
        clearFile("taskFile.txt");
        List<String> lines = new ArrayList<>();
        for (int i = 1; i < newTasks.size(); i++) {
            String str = String.join(",", newTasks.get(i));
            lines.add(str);
        }
        for (String str : lines) {
            save(str + System.lineSeparator());
        }
    }

    public void clearFile(String fileName) throws IOException {  //чищу файл, оставляя 1 строчку с историей просмотров
        String firstLine = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            firstLine = reader.readLine();
        }
        try (FileWriter writer = new FileWriter(fileName, false)) {
            if (firstLine != null) {
                writer.write(firstLine + System.lineSeparator());
            }
        }
    }

   public void historyInFile(LinkedHashSet<Task> arrayHistory) throws IOException {
       List<String> history = new ArrayList<>();
       for (Task task : arrayHistory) {
           if (task != null) {
               String str = String.valueOf(task.id);
               history.add(str);
           }
       }
       String historyStr = String.join(",", history);
       List<String> arrStr = readStr();
       arrStr.set(0, historyStr);
       try (Writer fileWriter = new FileWriter("taskFile.txt", false)) {
       }
       for (String str : arrStr) {
           save(str + System.lineSeparator());
       }
   }
}
