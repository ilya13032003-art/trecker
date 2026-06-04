
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        HashMap<Integer, Task> baseTask = new HashMap<>();
        HashMap<Integer, Epic> baseEpic = new HashMap<>();
        TaskManager taskManager = new TaskManager();
        int choiceId;
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    if (baseEpic.isEmpty() && baseTask.isEmpty()) {
                        System.out.println("Списки пусты");
                    }
                    for (Task task : baseTask.values()) {
                        System.out.println(task);
                    }
                    for (Epic epic : baseEpic.values()) {
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
                            TaskManager.createObj(baseTask, baseEpic, name, description, type);
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
                    TaskManager.updateStatus(baseTask, baseEpic, choiceId, choiceCreate, status);
                    break;
                case 4:
                    System.out.println("Введите ID задачи, которую хотите посмотреть");
                    choiceId = scanner.nextInt();
                    if (!baseTask.containsKey(choiceId)) {
                        System.out.println("Задачи с таким ID - нет");
                    } else {
                        Task task = baseTask.get(choiceId);
                        System.out.println(task);
                        taskManager.add(task);
                    }
                    break;
                case 5:
                    System.out.println("Введите ID эпика, подзадачи которого вы бы хотели видеть");
                    choiceId = scanner.nextInt();
                    if (!baseEpic.containsKey(choiceId)) {
                        System.out.println("Эпика с таким ID - нет");
                    } else {
                        if (baseEpic.get(choiceId).subTaskArray.isEmpty()) {
                            System.out.println("У данного эпика нет подзадач");
                        }
                        for (Task task : baseEpic.get(choiceId).subTaskArray.values()) {
                            System.out.println(task);
                        }
                        taskManager.add(baseEpic.get(choiceId));
                    }
                    break;
                case 6:
                    if (taskManager.getHistory().isEmpty()) {
                        System.out.println("История просмотров пуста");
                    } else {
                        for (Task task : taskManager.getHistory()) {
                            System.out.println(task);
                            if (!baseEpic.containsKey(task.id) && !baseTask.containsKey(task.id)) {
                                System.out.println(" Объект удалён)");
                            }
                        }
                    }
                    break;
                case 7:
                    System.out.println("Выберите, что бы вы хотели удалить:\n 1 - задачу\n 2 - эпик\n 3 - подзадачу\n");
                    int choiceRemove = scanner.nextInt();
                    System.out.println("Введите индентификатор объекта, который хотели бы удалить");
                    choiceId = scanner.nextInt();
                    if (choiceRemove == 1 || choiceRemove == 2) {
                        TaskManager.removeObj(baseTask, baseEpic, choiceRemove, choiceId);
                    } else if (choiceRemove == 3) {
                        TaskManager.removeSubTask(choiceId, baseEpic);
                    } else {
                        System.out.println("Такой команды нет");
                    }
                    break;
                case 8:
                    baseTask.clear();
                    baseEpic.clear();
                    System.out.println("Все задачи удалены");
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