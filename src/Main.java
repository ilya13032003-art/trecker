
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        Manager manager = null;
        InMemoryHistoryManager inMemoryHistoryManager = null;

        while (true) {
            System.out.println("ТЫ КРУТОЙ?\n 1 - ДА И Я БУДУ ХРАНИТЬ ВСЁ В ФАЙЛАХХХХ\n 2 - НЕТ, ВЕДЬ Я ЛЕФ");
            int JloxChoice = scanner.nextInt();
            if (JloxChoice == 1) {
                manager = new FileBackedTasksManager();
                inMemoryHistoryManager = new HistoryInFile();

                final String HOME = "C:\\Users\\activ\\Idea\\trecker";

                Path taskFile = Paths.get(HOME, "taskFile.txt");
                if (!Files.exists(taskFile)) {
                    Files.createFile(taskFile);
                }
                FileManager fileManager = new FileManager();
                List<String[]> newTasks = fileManager.cutTask();
                int maxId = 0; //нужно восстановить нормальный счётчик id
                if (!newTasks.isEmpty()) {
                    for (int i = 1; i < newTasks.size(); i++) {
                        String[] str = newTasks.get(i);
                        int id = Integer.parseInt(str[0]);
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileManager.stringToTask(newTasks.get(i), manager.getBaseTask(), manager.getBaseEpic());
                    }
                    String[] idInStr = newTasks.get(0);
                    for (String str : idInStr) {
                        if (str == null || str.isEmpty() || str.equals("-")) continue;
                        int id = Integer.parseInt(str);
                        if (manager.getBaseTask().containsKey(id)) {
                            Task task = manager.getBaseTask().get(id);
                            inMemoryHistoryManager.add(task);
                        } else if (manager.getBaseEpic().containsKey(id)) {
                            Task task = manager.getBaseEpic().get(id);
                            inMemoryHistoryManager.add(task);
                        } else {
                            System.out.println("Задачи с id=" + id + " нет");
                        }
                    }
                } else {
                    fileManager.save("-" + System.lineSeparator());
                }
                while (true) { //продвигаем счётчик до актуального id
                    manager.setIndicator();
                    if (manager.getIndicator() == maxId) {
                        break;
                    }
                }
                break;
            } else if (JloxChoice == 2) {
                manager = new Manager();
                inMemoryHistoryManager = new InMemoryHistoryManager();
                break;
            } else {
                System.out.println("Такой команды нет, дурачок, попробуй ещё раз");
            }
        }

        int choiceId;
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    if (manager.getBaseEpic().isEmpty() && manager.getBaseTask().isEmpty()) {
                        System.out.println("Списки пусты");
                    }
                    for (Task task : manager.getBaseTask().values()) {
                        System.out.println(task);
                    }
                    for (Epic epic : manager.getBaseEpic().values()) {
                        System.out.println(epic);
                    }
                    break;
                case 2:
                    scanner.nextLine();  //исправил баг nextInt-nextLine
                    System.out.println("Введите название задачи");
                    String name = scanner.nextLine();
                    System.out.println("Опишите вашу задачу");
                    String description = scanner.nextLine();
                    while (true) {
                        System.out.println("Выберите тип:\n 1 - задача\n 2 - эпик\n 3 - подзадача\n");
                        int type = scanner.nextInt();
                        if (type == 1 || type == 2 || type == 3) {
                            manager.createTaskByType(name, description, type);
                            break;
                        } else {
                            System.out.println("Такой команды нет");
                        }
                    }
                    System.out.println("Задача успешно создана!");
                    break;
                case 3:
                    System.out.println("Выберите у чего бы вы хотели обновить статус: \n 1 - задача \n 2 - подзадача");
                    int choiceCreate = scanner.nextInt();
                    if (choiceCreate != 1 && choiceCreate != 2) {
                        System.out.println("Такого типа задачи нет");
                        break;
                    }
                    System.out.println("Введите ID объекта, у которого хотите обновить статус");
                    choiceId = scanner.nextInt();
                    System.out.println("Выберите новый статус: \n 1 - NEW\n 2 - IN_PROGRESS\n 3 - DONE");
                    int status = scanner.nextInt();
                    manager.updateStatus(choiceId, choiceCreate, status);
                    break;
                case 4:
                    System.out.println("Введите ID задачи, которую хотите посмотреть");
                    choiceId = scanner.nextInt();
                    if (!manager.getBaseTask().containsKey(choiceId)) {
                        System.out.println("Задачи с таким ID - нет");
                    } else {
                        Task task = manager.getBaseTask().get(choiceId);
                        System.out.println(task);
                        inMemoryHistoryManager.add(task);
                    }
                    break;
                case 5:
                    System.out.println("Введите ID эпика, подзадачи которого вы бы хотели видеть");
                    choiceId = scanner.nextInt();
                    if (!manager.getBaseEpic().containsKey(choiceId)) {
                        System.out.println("Эпика с таким ID - нет");
                    } else {
                        if (manager.getBaseEpic().get(choiceId).getSubTaskArray().isEmpty()) {
                            System.out.println("У данного эпика нет подзадач");
                        }
                        for (Task task : manager.getBaseEpic().get(choiceId).getSubTaskArray().values()) {
                            System.out.println(task);
                        }
                        inMemoryHistoryManager.add(manager.getBaseEpic().get(choiceId));
                    }
                    break;
                case 6:
                    if (inMemoryHistoryManager.getHistory().isEmpty()) {
                        System.out.println("История просмотров пуста");
                    } else {
                        System.out.println("Ваша история просмотров:");
                        for (Task task : inMemoryHistoryManager.getHistory()) {
                            System.out.println(task);
                        }
                    }
                    break;
                case 7:
                    System.out.println("Выберите, что бы вы хотели удалить:\n 1 - задачу\n 2 - эпик\n 3 - подзадачу\n");
                    int taskType = scanner.nextInt();
                    System.out.println("Введите индентификатор объекта, который хотели бы удалить");
                    choiceId = scanner.nextInt();
                    if (taskType == 1 || taskType == 2) {
                        manager.removeTaskByType(taskType, choiceId);
                        inMemoryHistoryManager.removeInHistory(choiceId);
                    } else if (taskType == 3) {
                        manager.removeSubTask(choiceId);
                    } else {
                        System.out.println("Такой команды нет");
                    }
                    break;
                case 8:
                    manager.removeAll();
                    inMemoryHistoryManager.setArrayHistory();
                    break;
                case 9:
                    System.out.println("Выход");
                    return;
                default:
                    System.out.println("Такой команды еще нет");
            }
        }
    }

    private static void printMenu() {
        System.out.println("""
            Выберите цифру, соответствующую тому, что бы вы хотели сделать:
             1 - получить список всех задач
             2 - создать новую задачу
             3 - обновить статус задачи
             4 - просмотреть задачу
             5 - получить список подзадач определённого эпика
             6 - открыть историю просмотров
             7 - удалить задачу
             8 - удалить все задачи
             9 - выход
            """);
    }
}