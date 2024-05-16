package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static ArrayBlockingQueue symbolA = new ArrayBlockingQueue(100);
    public static ArrayBlockingQueue symbolB = new ArrayBlockingQueue(100);
    public static ArrayBlockingQueue symbolC = new ArrayBlockingQueue(100);

    public static void main(String[] args) throws InterruptedException {

        Thread textGenerator = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    symbolA.put(text);
                    symbolB.put(text);
                    symbolC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        });
        textGenerator.start();

        Thread a = new Thread(() -> {
            char letter = 'a';
            int maxA = maxCount(symbolA, letter);
            System.out.println("Максимальное количество символов " + letter + ": " + maxA);
        });
        a.start();

        Thread b = new Thread(() -> {
            char letter = 'b';
            int maxB = maxCount(symbolB, letter);
            System.out.println("Максимальное количество символов " + letter + ": " + maxB);
        });
        b.start();

        Thread c = new Thread(() -> {
            char letter = 'c';
            int maxC = maxCount(symbolC, letter);
            System.out.println("Максимальное количество символов " + letter + ": " + maxC);
        });
        c.start();


        a.join();
        b.join();
        c.join();

    }

    private static int maxCount(ArrayBlockingQueue symbol, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            for (int i = 0; i < 10_000; i++) {
                text = (String) symbol.take();
                for (char c : text.toCharArray()) {
                    if (c == letter) {
                        count++;
                    }
                    if (c > max) {
                        max = count;
                        count = 0;
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + "прервано");
            return -1;
        }
        return max;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}