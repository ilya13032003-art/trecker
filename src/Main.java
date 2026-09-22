
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        HistoryManager historyManager = null;
        TaskManager manager = null;


        while (true) {
            System.out.println("ТЫ КРУТОЙ?\n 1 - ДА И Я БУДУ ХРАНИТЬ ВСЁ В ФАЙЛАХХХХ\n 2 - НЕТ, ВЕДЬ Я ЛЕФ");
            int JloxChoice = scanner.nextInt();
            if (JloxChoice == 1) {
                HistoryInFile historyInFile = new HistoryInFile();
                FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
                fileBackedTasksManager.setHistory(historyInFile);
                historyInFile.setManager(fileBackedTasksManager);
                historyManager = historyInFile;
                manager = fileBackedTasksManager;

                Path taskFile = FileManager.TASK_FILE;
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
                        Task task = fileManager.stringToTask(newTasks.get(i), manager.getBaseTask(), manager.getBaseEpic());
                        if (TaskType.EPIC.equals(task.taskType)) {
                            //ля ля ля жу жу жу я с мозгами не дружу
                        } else {
                            manager.timeCheck(task.startTime, (int) task.duration.toMinutes());
                            manager.getPrioritizedTasks().add(task);
                        }
                    }
                    String idInStr = newTasks.get(0)[0];
                    for (String str : idInStr.split(",")) {
                        if (str == null || str.isEmpty() || str.equals("-")) continue;
                        int id = Integer.parseInt(str);
                        if (manager.getBaseTask().containsKey(id)) {
                            Task task = manager.getBaseTask().get(id);
                            historyManager.add(task);
                        } else if (manager.getBaseEpic().containsKey(id)) {
                            Task task = manager.getBaseEpic().get(id);
                            historyManager.add(task);
                        } else {
                            System.out.println("Задачи с id=" + id + " нет");
                        }
                    }
                } else {
                    fileBackedTasksManager.save();
                }
                while (manager.getIndicator() < maxId) {
                    manager.setIndicator();
                }
                break;
            } else if (JloxChoice == 2) {
                manager = new InMemoryTaskManager();
                historyManager = new HistoryInMemory();
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
                case 2: //тут, наверное можно было бы использовать стринг билдер и при нахождении запрещённого символа отменять
                        //результат и заставлять переписывать, но мне кажется, что лучше не заморачиваться с этой историей
                    System.out.println("ВНИМАНИЕ: в названии и в опиcании задачи нельзя использовать символ \"^\" или перенос строки");
                    scanner.nextLine();//исправил баг nextInt-nextLine
                    String name = censorship("название");
                    String description = censorship("описание");
                    while (true) {
                        System.out.println("Выберите тип:\n 1 - задача\n 2 - эпик\n 3 - подзадача\n");
                        int type = scanner.nextInt();
                        scanner.nextLine(); //пустышка
                        if (type == 1) {
                                System.out.println("Введите продолжительность выполнения задачи(В МИНУТАХ)");
                                int durationInt = scanner.nextInt();
                            scanner.nextLine(); //пустышка
                            Duration duration = Duration.ofMinutes(durationInt);
                                 LocalDateTime startTime = correctTime(manager, durationInt);
                                 manager.createTask(name, description, TaskType.TASK, startTime, duration);
                                 break;
                        } else if (type == 2) {
                                manager.createEpic(name, description, TaskType.EPIC);
                                break;
                        } else if (type == 3) {
                            System.out.println("Введите id эпика, которому принадлежит подзадача");
                            int epicId = scanner.nextInt();
                            scanner.nextLine(); //пустышка
                            if (manager.getBaseEpic().containsKey(epicId)) {
                                System.out.println("Введите продолжительность выполнения задачи(В МИНУТАХ)");
                                int durationInt = scanner.nextInt();
                                scanner.nextLine(); //пустышка
                                Duration duration = Duration.ofMinutes(durationInt);
                                LocalDateTime startTime = correctTime(manager, durationInt);
                                manager.createSubTask(name, description, epicId, TaskType.SUB_TASK, startTime, duration);

                            } else {
                                System.out.println("Эпика с таким id нет");
                            }
                            break;
                        } else {
                            System.out.println("Такой команды нет");
                        }
                    }
                    break;
                case 3:
                    System.out.println("Выберите у чего бы вы хотели обновить статус: \n 1 - задача \n 2 - подзадача");
                    int choiceCreate = scanner.nextInt();
                    TaskType taskType;
                    if (choiceCreate == 1) {
                        taskType = TaskType.TASK;
                    } else if (choiceCreate == 2) {
                        taskType = TaskType.SUB_TASK;
                    } else {
                        System.out.println("Такого типа задачи нет");
                        break;
                    }
                    System.out.println("Введите ID объекта, у которого хотите обновить статус");
                    choiceId = scanner.nextInt();
                    System.out.println("Выберите новый статус: \n 1 - NEW\n 2 - IN_PROGRESS\n 3 - DONE");
                    int statusChoice = scanner.nextInt();
                    TaskStatus status;
                    if (statusChoice == 1) {
                        status = TaskStatus.NEW;
                    } else if (statusChoice == 2) {
                        status = TaskStatus.IN_PROGRESS;
                    } else if (statusChoice == 3) {
                        status = TaskStatus.DONE;
                    } else {
                        System.out.println("Такого статуса нет");
                        break;
                    }
                    manager.updateStatus(choiceId, taskType, status);
                    break;
                case 4:
                    System.out.println("Введите ID задачи, которую хотите посмотреть");
                    choiceId = scanner.nextInt();
                    if (!manager.getBaseTask().containsKey(choiceId)) {
                        System.out.println("Задачи с таким ID - нет");
                    } else {
                        Task task = manager.getBaseTask().get(choiceId);
                        System.out.println(task);
                        historyManager.add(task);
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
                        historyManager.add(manager.getBaseEpic().get(choiceId));
                    }
                    break;
                case 6:
                    if (historyManager.getHistory().isEmpty()) {
                        System.out.println("История просмотров пуста");
                    } else {
                        System.out.println("Ваша история просмотров:");
                        for (Task task : historyManager.getHistory()) {
                            System.out.println(task);
                        }
                    }
                    break;
                case 7:
                    System.out.println("Список задач, отсортированных по приоритетности выполнения: " + manager.getPrioritizedTasks());
                    break;
                case 8:
                    System.out.println("Выберите, что бы вы хотели удалить:\n 1 - задачу\n 2 - эпик\n 3 - подзадачу\n");
                    int taskTypeChoice = scanner.nextInt();
                    System.out.println("Введите индентификатор объекта, который хотели бы удалить");
                    choiceId = scanner.nextInt();
                    if (taskTypeChoice == 1) {
                        manager.removeTaskByType(TaskType.TASK, choiceId);
                        historyManager.removeInHistory(choiceId);
                    } else if (taskTypeChoice == 2) {
                        manager.removeTaskByType(TaskType.EPIC, choiceId);
                        historyManager.removeInHistory(choiceId);
                    } else if (taskTypeChoice == 3) {
                        manager.removeSubTask(choiceId);
                    } else {
                        System.out.println("Такой команды нет");
                    }
                    break;
                case 9:
                    manager.removeAll();
                    historyManager.clearHistory();
                    break;
                case 10:
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
             7 - посмотреть список задач по приоретету их выполнения
             8 - удалить задачу
             9 - удалить все задачи
             10 - выход
            """);
    }

    private static LocalDateTime correctTime(TaskManager manager, int durationInt) {
        boolean ok = true;
        LocalDateTime startTime = null;
        while (ok) {
            System.out.println("Введите дату и время старта выполнения вашей задачи в формате \"dd.MM.yyyy, HH:mm\"");
            String startStr = scanner.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
            startTime = LocalDateTime.parse(startStr, formatter);
            if (manager.timeCheck(startTime, durationInt)) {
                ok = false;
            } else {
                System.out.println("Введеная вами дата занята другой задачей");
            }
        }
        return startTime;
    }

    private static boolean validateText(String text) {
        if (text == null
            || text.contains("^")
            || text.contains("\n")
            || text.contains("\r")) {
            return false;
        }
        return true;
    }

    private static String censorship(String str) {
        while (true) {
            System.out.println("Введите " + str + " задачи");
            String text = scanner.nextLine();
            if (validateText(text)) {
                return text;
            } else {
                System.out.println("ВНИМАНИЕ: в названии и в опиcании задачи нельзя " +
                    "использовать символ \"^\" или перенос строки");
            }
        }
    }
}
