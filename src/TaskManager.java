//исходя из описания InMemoryTaskManager, мне показалось, что это копия этого класса, поэтому я просто напишу интерфейс
//хистори мэнеджер и добавлю его сюда

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TaskManager implements HistoryManager {

    static Scanner scanner = new Scanner(System.in);
    private static int indicator = 0;

    public static int setIndicator() {
        return indicator++;
    }

    public static int getIndicator() {
        return indicator;
    }

    private static ArrayList<Task> browsingHistory = new ArrayList<>();

    static void createObj(HashMap<Integer, Task> baseTask, HashMap<Integer, Epic> baseEpic, String name, String description, int type) {
        setIndicator();
        if (type == 1) {
            Task task = new Task(name, description, getIndicator());
            baseTask.put(task.id, task);
        } else if (type == 2) {
            Epic epic = new Epic(name, description, getIndicator());
            baseEpic.put(epic.id, epic);
        } else if (type == 3) {
            while (true) {
                System.out.println("Введите ID эпика, которому принадлежит эта подзадача");
                int iD = scanner.nextInt();
                if (baseEpic.containsKey(iD)) {
                    Task subTask = new Task(name, description, getIndicator());
                    baseEpic.get(iD).subTaskArray.put(getIndicator(), subTask);
                    baseEpic.get(iD).checkStatus();
                    break;
                } else {
                    System.out.println("Эпика с таким ID нет");
                }
            }
        }
    }

    // в этом методе строчка:searchEpic(baseEpic, choiceId).subTaskArray.get(choiceId) капец какая тяжёлая, наверное её нужно разбить
    //на 2-3 строчки с переменными. Сначала на эпик, потом на подзадачу, но это столько лишнего кода появляется, что смысл от метода
    // searchEpic мгновенно пропадает как-будто. Могу ли я если уж допускаю такие строчки, просто писать расшифровку для читающего
    //точно так же через двойной слэш?
    public static void updateStatus(HashMap<Integer, Task> baseTask, HashMap<Integer, Epic> baseEpic, int choiceId, int choiceCreate, int status) {
        if (choiceCreate == 1 && !baseTask.containsKey(choiceId)) {
            System.out.println("Задачи с таким ID нет");
        } else if (choiceCreate == 2 && searchEpic(baseEpic, choiceId) == null) {
            System.out.println("Задачи с таким ID нет");
        } else {
            switch (status) {
                case 1:
                    if (choiceCreate == 1) {
                        baseTask.get(choiceId).status = "NEW";
                    } else  {
                        searchEpic(baseEpic, choiceId).subTaskArray.get(choiceId).status = "NEW";
                        searchEpic(baseEpic, choiceId).checkStatus();
                    }
                    return;
                case 2:
                    if (choiceCreate == 1) {
                        baseTask.get(choiceId).status = "IN_PROGRESS";
                    } else {
                        searchEpic(baseEpic, choiceId).subTaskArray.get(choiceId).status = "IN_PROGRESS";
                        searchEpic(baseEpic, choiceId).checkStatus();
                    }
                    return;
                case 3:
                    if (choiceCreate == 1) {
                        baseTask.get(choiceId).status = "DONE";
                    } else  {
                        searchEpic(baseEpic, choiceId).subTaskArray.get(choiceId).status = "DONE";
                        searchEpic(baseEpic, choiceId).checkStatus();
                    }
            }
        }
    }

    public static void removeObj(HashMap<Integer, Task> baseTask, HashMap<Integer, Epic> baseEpic, int choise2, int choiceId) {
        if (choise2 == 1) {
            if (baseTask.get(choiceId) == null) {
                System.out.println("Такой задачи нет");
            } else {
                baseTask.remove(choiceId);
                System.out.println("Задача удалена");
            }
        } else if (choise2 == 2) {
            if (baseEpic.get(choiceId) == null) {
                System.out.println("Такого эпика - нет");
            } else {
                baseEpic.remove(choiceId);
                System.out.println("Эпик удален");
            }
        }
    }

    public static void removeSubTask(int choiceId, HashMap<Integer, Epic> baseEpic) {
        if (searchEpic(baseEpic, choiceId) == null) {
            System.out.println("Подзадачи с таким ID - нет");
        } else {
            searchEpic(baseEpic, choiceId).subTaskArray.remove(choiceId);
            System.out.println("Подзадача удалена");
        }
    }

    private static Epic searchEpic(HashMap<Integer, Epic> baseEpic, int choiceId) { //моя личная гордость, как же эта штука
        Epic epic = null;                                                           // упростила жизнь, мой любимый метод
        for (Epic ep : baseEpic.values()) {
            if (ep.subTaskArray.containsKey(choiceId)) {
                epic = ep;
            }
        }
        return epic;
    }

    @Override
    public void add(Task task) {
        if (browsingHistory.size() >= 10) {
            browsingHistory.remove(9);
        }
        browsingHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return browsingHistory;
    }
}




