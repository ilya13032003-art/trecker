//я вот ну реально не понимаю зачем мне тут какие либо модификаторы доступа, я прошёл эту тему,
//никаких проблем у меня она не вызвала, как я понял протектед особо нигде не юзается, в основном
//все юзают публик и приват, но хоть убей я не вижу для чего они мне тут нужны...

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static int indicator = 1;

    public static void main(String[] args) {
        HashMap<Integer, ArrayList> base = new HashMap<>();

        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    if (base.isEmpty()) {
                        System.out.println("Список задач пуст");
                    }
                    for (ArrayList<Object> task : base.values()) {
                        Epic epic = (Epic) task.get(0);
                        System.out.println(epic);
                    }
                    break;
                case 2:
                    createEpic(base);
                    checks(base);
                    break;
                case 3:
                    updateStatus(base);
                    checks(base);
                    break;
                case 4:
                    System.out.println("Введите индефикатор задачи, у которой вы бы хотели посмотреть подзадачи");
                    int indef = scanner.nextInt();
                    if (base.get(indef) == null) {
                        System.out.println("Такой задачи нет");
                    } else {
                        ArrayList<Object> task = base.get(indef);
                        if (task.size() < 2) {
                            System.out.println("У этой задачи нет подзадач");
                        }
                        for (int i = 1; i < task.size(); i++) {
                            SubTask subTask = (SubTask) task.get(i);
                            System.out.println(subTask);
                        }
                    }
                    break;
                case 5:
                    removeTask(base);
                    break;
                case 6:
                    base.clear();
                    System.out.println("Все задачи удалены");
                    break;
                case 7:
                    System.out.println("Выход");
                    return;
                default:
                    System.out.println("Такой команды еще нет");
            }
        }
    }
    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    static void printMenu() {
        System.out.println("Выберите цифру, соответствующую тому, что бы вы хотели сделать:\n" +
            " 1 - получить список всех задач\n" +
            " 2 - создать новую задачу\n" +
            " 3 - обновить статус задачи\n" +
            " 4 - получить список подзадач определённой задачи\n" +
            " 5 - удалить задачу\n" +
            " 6 - удалить все задачи\n" +
            " 7 - выход\n");
    }

    static void createEpic(HashMap<Integer, ArrayList> base) {
        String empty = scanner.nextLine();  //исправил баг nextInt-nextLine
        System.out.println("Введите название задачи");
        String name = scanner.nextLine();
        System.out.println("Опишите вашу задачу");
        String description = scanner.nextLine();
        int identifier = indicator++;
        Epic epic = new Epic(name, description, identifier);
        ArrayList<Object> task = new ArrayList<>();
        task.add(epic);
        base.put(identifier, task);
        while (true) {
            System.out.println("Хотите добавить подзадачу?\n" +
                " 1 - да\n" +
                " 2 - нет\n");
            int choice = scanner.nextInt();
            String empty1 = scanner.nextLine(); //исправил баг nextInt-nextLine
            if (choice == 1) {
                System.out.println("Введите название подзадачи");
                String subTaskName = scanner.nextLine();
                System.out.println("Опишите вашу подзадачу");
                String subTaskDescription = scanner.nextLine();
                String status;
                while (true) {
                    System.out.println("На какой стадии выполнения находится ваша задача?\n" +
                        " 1 - NEW\n" +
                        " 2 - IN_PROGRESS\n");
                    int progress = scanner.nextInt();
                    String empty2 = scanner.nextLine(); //исправил баг nextInt-nextLine
                    if (progress == 1) {
                        status = "NEW";
                        break;
                    } else if (progress == 2) {
                        status = "IN_PROGRESS";
                        break;
                    } else {
                        System.out.println("Такой команды нет");
                    }
                }
                int subTaskIdentifier = indicator++;
                SubTask subTask = new SubTask(subTaskName, subTaskDescription, subTaskIdentifier, name, status);
                task.add(subTask);
                epic.subTaskArray.add(subTaskName);
            } else if (choice == 2) {
                break;
            } else {
                System.out.println("Такой команды нет");
            }
        }
    }

    static void updateStatus(HashMap<Integer, ArrayList> base) {
        while (true) {
            System.out.println("Выберите у чего бы вы хотели обновить статус: " +
                "\n 1 - задача" + "\n 2 - подзадача");
            int choice = scanner.nextInt();
            if (choice == 1) {
                System.out.println("Введите идентификатор задачи");
                int indef = scanner.nextInt();
                if (base.get(indef) == null) {
                    System.out.println("Задачи с таким индефикатором нет");
                    return;
                }
                ArrayList<Object> array = new ArrayList<>();
                array = base.get(indef);
                Epic epic = (Epic) array.get(0);
                if (epic.subTaskArray.size() == 0) {
                    while (true) {
                        System.out.println("Выберите новый статус задачи: " +
                            "\n 1 - NEW" +
                            "\n 2 - IN_PROGRESS" +
                            "\n 3 - DONE");
                        int choice1 = scanner.nextInt();
                        switch (choice1) {
                            case 1:
                                epic.status = "NEW";
                                return;
                            case 2:
                                epic.status = "IN_PROGRESS";
                                return;
                            case 3:
                                epic.status = "DONE";
                                return;
                            default:
                                System.out.println("Такой команды нет");
                        }
                    }
                } else {
                    System.out.println("Невозможно выполнить действие, в вашей задаче есть незакрытые подзадачи");
                    break;
                }
            } else if (choice == 2) {
                System.out.println("Введите идентификатор подзадачи");
                int indef = scanner.nextInt();
                int contains = 0;
                for (ArrayList<Object> task : base.values()) {
                    ArrayList<Object> array = task;
                    for (int i = 1; i < array.size(); i++) {
                        contains--;
                        SubTask subTask = (SubTask) array.get(i);
                        if (indef == subTask.identifier) {
                            contains += 10000;
                            while (true) {
                                System.out.println("Выберите новый статус подзадачи: " +
                                    "\n 1 - NEW" +
                                    "\n 2 - IN_PROGRESS" +
                                    "\n 3 - DONE");
                                int choice1 = scanner.nextInt();
                                if (choice1 == 1) {
                                    subTask.status = "NEW";
                                    break;
                                } else if (choice1 == 2) {
                                    subTask.status = "IN_PROGRESS";
                                    break;
                                } else if (choice1 == 3) {
                                    subTask.status = "DONE";
                                    break;
                                } else {
                                    System.out.println("Такой команды нет");
                                }
                            }
                        }
                    }
                }
                if (contains > 0) {
                    System.out.println("Статус подзадачи обновлён");
                } else {
                    System.out.println("Подзадачи с таким индетификатором нет");
                }
                break;
            } else {
                System.out.println("Такой команды нет");
            }
        }
    }

    static void checks(HashMap<Integer, ArrayList> base) {
        int done = 0;
        int nEw = 0;
        for (ArrayList<Object> task : base.values()) {
            ArrayList<Object> array = task;
            for (int i = 1; i < array.size(); i++) {
                SubTask subTask = (SubTask) array.get(i);
                if (subTask.status.equals("IN_PROGRESS")) {
                    done -= 1000;
                    nEw -= 1000;
                } else if (subTask.status.equals("NEW")) {
                    done -= 1000;
                    nEw++;
                } else if (subTask.status.equals("DONE")) {
                    done++;
                    nEw -= 1000;
                }
            }
            Epic epic = (Epic) array.get(0);
            if (done > 0) {
                epic.status = "DONE";
            } else if (nEw > 0) {
                epic.status = "NEW";
            } else {
                epic.status = "IN_PROGRESS";
            }
        }
    }

    static void removeTask(HashMap<Integer, ArrayList> base) {
        System.out.println("Выберите, что бы вы хотели удалить:" +
            "\n1 - задачу" +
            "\n2 - подзадачу");
        int choise2 = scanner.nextInt();
        if (choise2 == 1) {
            System.out.println("Введите индентификатор задачи");
            int indef = scanner.nextInt();
            if (base.get(indef) == null) {
                System.out.println("Такой задачи нет");
            } else {
                base.remove(indef);
                System.out.println("Задача удалена");
            }
        } else if (choise2 == 2) {
            System.out.println("Введите идентификатор подзадачи");
            int indef = scanner.nextInt();
            int contains = 0;
            for (ArrayList<Object> task : base.values()) {
                ArrayList<Object> array = task;
                for (int i = 1; i < array.size(); i++) {
                    contains--;
                    SubTask subTask = (SubTask) array.get(i);
                    if (indef == subTask.identifier) {
                        contains += 10000;
                        String ff = subTask.name;
                        Epic epic = (Epic) array.get(0);
                        epic.subTaskArray.remove(ff);
                        array.remove(i);
                    }
                }
            }
            if (contains > 0) {
                System.out.println("Задача удалена");
            } else {
                System.out.println("Такой подзадачи нет");
            }
        } else {
            System.out.println("Такой команды нет");
        }
    }
}