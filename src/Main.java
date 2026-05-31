
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static int indicator = 1;

    public static void main(String[] args) {
        HashMap<Integer, Task> baseTask = new HashMap<>();
        HashMap<Integer, Epic> baseEpic = new HashMap<>();
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
                    createObj(baseTask,baseEpic);
                    System.out.println("Задача успешно создана!");
                    break;
                case 3:
                    System.out.println("Введите ID эпика, в который хотели бы добавить подзадачу");
                    int indef = scanner.nextInt();
                    if (baseEpic.get(indef) == null) {
                        System.out.println("Эпика с таким ID - нет");
                    } else {
                    baseEpic.get(indef).createSubTask();
                    }
                    break;
                case 4:
                        System.out.println("Выберите у чего бы вы хотели обновить статус: \n 1 - задача \n 2 - подзадача");
                        int choice1 = scanner.nextInt();
                        System.out.println("Введите ID объекта, у которого хотите обновить статус");
                        int indef6 = scanner.nextInt();
                        if (choice1 == 1) {
                            updateStatus(baseTask, indef6);
                        } else if (choice1 == 2) {
                            boolean found = false;
                            for (Epic epic : baseEpic.values()) {
                                if (epic.subTaskArray.containsKey(indef6)) {
                                    epic.updateSubTaskStatus(indef6);
                                    epic.checks();
                                    found = true;
                                }
                            }
                            if (!found) {
                                System.out.println("Подзадачи с таким ID - нет");
                            }
                        }
                    break;
                case 5:
                    System.out.println("Введите ID эпика, подзадачи которого вы бы хотели видеть");
                    int indef1 = scanner.nextInt();
                    Epic epic = baseEpic.get(indef1);
                    if (epic == null) {
                        System.out.println("Эпика с таким ID - нет");
                    } else {
                        if (epic.subTaskArray.isEmpty()) {
                            System.out.println("У данного эпика нет подзадач");
                        }
                        for (Task task : epic.subTaskArray.values()) {
                            System.out.println(task);
                            epic.checks();
                        }
                    }
                    break;
                case 6:
                    System.out.println("Выберите, что бы вы хотели удалить:\n 1 - задачу\n 2 - эпик\n 3 - подзадачу\n");
                    int choise2 = scanner.nextInt();
                    System.out.println("Введите индентификатор объекта, который хотели бы удалить");
                    int indef7 = scanner.nextInt();
                    if (choise2 == 1 || choise2 == 2) {
                        removeObj(baseTask, baseEpic, choise2, indef7);
                    } else if (choise2 == 3) {
                        removeSubTask(indef7, baseEpic);
                    } else {
                        System.out.println("Такой команды нет");
                    }
                    break;
                case 7:
                    baseTask.clear();
                    baseEpic.clear();
                    System.out.println("Все задачи удалены");
                    break;
                case 8:
                    System.out.println("Выход");
                    return;
                default:
                    System.out.println("Такой команды еще нет");
            }
        }
    }

    static void printMenu() {
        System.out.println("Выберите цифру, соответствующую тому, что бы вы хотели сделать:\n" +
            " 1 - получить список всех задач\n" +
            " 2 - создать новую задачу\n" +
            " 3 - добавить подзадачу\n" +
            " 4 - обновить статус задачи\n" +
            " 5 - получить список подзадач определённого эпика\n" +
            " 6 - удалить задачу\n" +
            " 7 - удалить все задачи\n" +
            " 8 - выход\n");
    }

    static void createObj(HashMap<Integer, Task> baseTask,HashMap<Integer, Epic> baseEpic) {
        scanner.nextLine();  //исправил баг nextInt-nextLine
        System.out.println("Введите название задачи");
        String name = scanner.nextLine();
        System.out.println("Опишите вашу задачу");
        String description = scanner.nextLine();
        int ID = indicator++;
        while (true) {
            System.out.println("Выберите тип:\n 1 - задача\n 2 - эпик\n");
            int choise = scanner.nextInt();
            if (choise == 1) {
                Task task = new Task(name, description, ID);
                baseTask.put(task.ID, task);
                break;
            } else if (choise == 2) {
                Epic epic = new Epic(name, description, ID);
                baseEpic.put(epic.ID, epic);
                break;
            } else {
                System.out.println("Такой команды нет");
            }
        }
    }

    static void updateStatus(HashMap<Integer, Task> baseTask, int indef6) {
        if (!baseTask.containsKey(indef6)) {
            System.out.println("Задачи с таким индефикатором нет");
        } else {
            while (true) {
                System.out.println("Выберите новый статус задачи: \n 1 - NEW\n 2 - IN_PROGRESS\n 3 - DONE");
                int choice1 = scanner.nextInt();
                switch (choice1) {
                    case 1:
                        baseTask.get(indef6).status = "NEW";
                        return;
                    case 2:
                        baseTask.get(indef6).status = "IN_PROGRESS";
                        return;
                    case 3:
                        baseTask.get(indef6).status = "DONE";
                        return;
                    default:
                        System.out.println("Такой команды нет");
                }
            }
        }
    }

    static void removeObj(HashMap<Integer, Task> baseTask,HashMap<Integer, Epic> baseEpic, int choise2, int indef) {
        if (choise2 == 1) {
            if (baseTask.get(indef) == null) {
                System.out.println("Такой задачи нет");
            } else {
                baseTask.remove(indef);
                System.out.println("Задача удалена");
            }
        } else if (choise2 == 2) {
            if (baseEpic.get(indef) == null) {
                System.out.println("Такого эпика - нет");
            } else {
                baseEpic.remove(indef);
                System.out.println("Эпик удален");
            }
        }
    }

    static void removeSubTask(int indef, HashMap<Integer, Epic> baseEpic) {
            boolean found = false;
            for (Epic epic : baseEpic.values()) {
                if (epic.subTaskArray.containsKey(indef)) {
                    epic.subTaskArray.remove(indef);
                    found = true;
                }
            }
            if (found) {
                System.out.println("Подзадача удалена");
            } else {
                System.out.println("Подзадачи с таким ID - нет");
            }
        }
}