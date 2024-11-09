package ru.safiullina;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {

        String initialString = "RLRFR";
        char R = 'R';
        int lengthRoute = 100;
        int numberRoutes = 1000;

        for (int i = 0; i < numberRoutes; i++) {

            new Thread(() -> {

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
                }

            }).start();

        }

        // нНайдем максимум среди значений мап
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

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

}