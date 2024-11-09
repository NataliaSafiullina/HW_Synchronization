package ru.safiullina;

import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static int[] currentLeader = new int[2];

    public static void main(String[] args) throws InterruptedException {

        String initialString = "RLRFR";
        char R = 'R';
        int lengthRoute = 100;
        int numberRoutes = 1000;

        // Создаем список для хранения создаваемых потоков
        List<Thread> threads = new ArrayList<>();

        // Создаем поток для вывода лидера в мап
        Thread leader = new Thread(() -> {
            // Выполняем пока поток не будет прерван
            while (!Thread.interrupted()) {
                // Пытаемся попасть в критическую секцию
                synchronized (sizeToFreq) {
                    // Проверим есть записи в мап
                    if (sizeToFreq.isEmpty()) {
                        try {
                            // Вызываем метод ожидания сигнала у монитора
                            sizeToFreq.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    // Вычисляем и выводим лидера
                    // (знаю что плохо делать подсчет вместе с побочным эффектом вывода на экран, это для быстроты)
                    leaderMap();
                }
            }
        });
        // Стартуем поток вывода лидеров
        leader.start();


        for (int i = 0; i < numberRoutes; i++) {

            // Создаем поток для генерации маршрутов и подсчета частоты R
            Thread thread = new Thread(() -> {

                // Генерируем маршрут
                String route = generateRoute(initialString, lengthRoute);
                // Подсчитываем количество R
                int numberR = route.length() - route.replace(String.valueOf(R), "").length();

                // Пытаемся записать в мап пару значений: количество R и сколько раз такое количество встречается
                synchronized (sizeToFreq) {
                    // Если значение по ключу существует, получаем это значение,
                    // если значения по ключу нет, то значение будет равно 0.
                    // Плюсуем к значению 1 и записываем в мап,
                    // если такой ключ есть, то эта пара заменяется,
                    // если такого ключа нет, то пара добавляется.
                    int value = sizeToFreq.getOrDefault(numberR, 0);
                    sizeToFreq.put(numberR, value + 1);

                    // Сигнализируем
                    sizeToFreq.notify();
                }

            });
            // Кладем поток в список
            threads.add(thread);
            // Запускаем поток на исполнение
            thread.start();

        }

        for (Thread thread : threads) {
            thread.join(); // зависаем, ждём когда поток, объект которого лежит в thread завершится
        }

        // После завершения всех считающих потоков прервите печатающий поток через thread.interrupt()
        leader.interrupt();

        // Найдем максимум среди значений мап
        int max = sizeToFreq.values().stream().max(Integer::compare).get();
        int total = 0;

        // Выводим наиболее частые значения
        for (Map.Entry<Integer, Integer> item : sizeToFreq.entrySet()) {
            if (item.getValue() == max) {
                System.out.printf("Самое частое количество повторений %d (встретилось %d раз) \n",
                        item.getKey(), item.getValue());
                total += item.getValue();
            }
        }

        // Выводим все остальные значения
        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> item : sizeToFreq.entrySet()) {
            if (item.getValue() != max) {
                System.out.printf("- %d (%d раз) \n",
                        item.getKey(), item.getValue());
                total += item.getValue();
            }
        }
        System.out.println("\nВсего раз = " + total);

    }

    public static void leaderMap() {

        // Найдем максимум среди значений мап
        int max = sizeToFreq.values().stream().max(Integer::compare).get();

        // Выводим наиболее частые значения, т.е. значение которых равны максимальному
        for (Map.Entry<Integer, Integer> item : sizeToFreq.entrySet()) {
            if (item.getValue() == max) {
                // Выводим, если предыдущее отличается от текущего
                if (currentLeader == null ||
                        !((currentLeader[0] == item.getKey()) && (currentLeader[1] == item.getValue()))) {
                    System.out.printf("Текущий лидер %d (встречается %d раз) \n", item.getKey(), item.getValue());
                    currentLeader[0] = item.getKey();
                    currentLeader[1] = item.getValue();
                }
            }
        }

    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

}