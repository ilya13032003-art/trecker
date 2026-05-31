import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Epic extends Task {

    HashMap<Integer, Task> subTaskArray = new HashMap<>();
    Scanner scanner = new Scanner(System.in);


    public Epic(String name, String description, int ID) {
        super(name, description, ID);
    }

    void createSubTask() {
        System.out.println("Введите название задачи");
        String name = scanner.nextLine();
        System.out.println("Опишите вашу задачу");
        String description = scanner.nextLine();
        int ID = Main.indicator++;
        Task task = new Task(name, description, ID);
        subTaskArray.put(task.ID, task);
        System.out.println("Подзадача успешно добавлена");
    }

    void updateSubTaskStatus(int indef6){
        while (true) {
            System.out.println("Выберите новый статус задачи: \n 1 - NEW\n 2 - IN_PROGRESS\n 3 - DONE");
            int choice1 = scanner.nextInt();
            switch (choice1) {
                case 1:
                    subTaskArray.get(indef6).status = "NEW";
                    System.out.println("Статус подзадачи обновлён");//я знаю что можно написать эту строчку 1 раз в мэйне и не парится,
                    return;                                        //но ты сам вогнал меня в рамки 150 строк))
                case 2:
                    subTaskArray.get(indef6).status = "IN_PROGRESS";
                    System.out.println("Статус подзадачи обновлён");
                    return;
                case 3:
                    subTaskArray.get(indef6).status = "DONE";
                    System.out.println("Статус подзадачи обновлён");
                    return;
                default:
                    System.out.println("Такой команды нет");
            }
        }
    }

    void checks() {
        boolean done = true;
        boolean nEw = true;
            for (Task subTask : subTaskArray.values()) {
                if (subTask.status.equals("IN_PROGRESS")) {
                    done = false;
                    nEw = false;
                } else if (subTask.status.equals("NEW")) {
                    done = false;
                } else if (subTask.status.equals("DONE")) {
                    nEw = false;
                }
            }
            if (done && !nEw) {
                status = "DONE";
            } else if (nEw && !done) {
                status = "NEW";
            } else {
                status = "IN_PROGRESS";
            }
    }

    @Override
    public String toString() {
        return
            "==============================" +
                "\n Название эпика:" + name +
                "\n Описание:" + description +
                "\n Индефикатор:" + ID +
                "\n Статус:" + status +
                "\n ID его подзадач:" + getSubTaskID();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Epic epic = (Epic) o;
        return Objects.equals(subTaskArray, epic.subTaskArray);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(subTaskArray);
        return result;
    }

    private ArrayList<Integer> getSubTaskID() {
        ArrayList<Integer> arrays = new ArrayList<>();
        for (Task task : subTaskArray.values()) {
            arrays.add(task.ID);
        }
        return arrays;
    }
}
