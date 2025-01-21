package com.nikijava.multithread;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static final String CURRENT_PATH = System.getProperty("user.dir");
    public static void main(String[] args) {
        task1();
    }

    public static void task1() {
        List<Integer> array = new ArrayList<Integer>();
        Random random = new Random();

        try(ExecutorService executor = Executors.newFixedThreadPool(3)) {
            Future<?> preparedArray = executor.submit(() -> {

                for(int i = 0; i < 10; i++){
                    array.add(random.nextInt(10));
                }

            });

            preparedArray.get();

            Future<Integer> elementsSum = executor.submit(() -> {
                Integer sum = 0;

                for(Integer i : array){
                    sum += i;
                }

                return sum;
            });

            Future<Double> averageElement = executor.submit(() -> {
                Double sum = 0.0;

                for(Integer i : array){
                    sum += i;
                }

                return sum / 10;
            });

            for(Integer i : array) {
                System.out.print(i + " ");
            }
            System.out.println();
            System.out.println("Sum: " + elementsSum.get());
            System.out.println("Average: " + averageElement.get());
        }

        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void task2() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter path: ");
        String path = scanner.nextLine();
        Path filePath = Paths.get(CURRENT_PATH, path);
        Path resultingFilePath = Paths.get(CURRENT_PATH, "files/resulting.txt");

        try(ExecutorService executor = Executors.newFixedThreadPool(3);
            BufferedWriter writer = new BufferedWriter(new FileWriter(resultingFilePath.toFile()));
            BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))
        ) {

        }

        catch(Exception e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }
}
