package com.nikijava.multithread;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static Random random = new Random();
    public static Scanner scanner = new Scanner(System.in);
    public static final String CURRENT_PATH = System.getProperty("user.dir");
    public static void main(String[] args) {
        // task1();
        // task2();
        // task3();
        task4();
        scanner.close();
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

        System.out.print("Enter path: ");
        String path = scanner.nextLine();
        Path filePath = Paths.get(CURRENT_PATH, path);
        Path primeFilePath = Paths.get(CURRENT_PATH, "files/primes.txt");
        Path factorialFilePath = Paths.get(CURRENT_PATH, "files/factorials.txt");

        try(ExecutorService executor = Executors.newFixedThreadPool(3);
            BufferedWriter primeWriter = new BufferedWriter(new FileWriter(primeFilePath.toFile()));
            BufferedWriter factorialWriter = new BufferedWriter(new FileWriter(factorialFilePath.toFile()))
        ) {
            Future <?> initialFileFilled = executor.submit(() -> {
                setNumbers(filePath);
            });
            initialFileFilled.get();
            Future<?> primesFileFilled = executor.submit(() -> {
                setPrimes(filePath, primeWriter);
            });
            primesFileFilled.get();
            Future<?> factorialsFileFilled = executor.submit(() -> {
                setFactorials(filePath, factorialWriter);
            });
            factorialsFileFilled.get();
        }

        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void task3() {
        System.out.print("Enter existing path: ");
        String existingPath = scanner.nextLine();
        System.out.print("Enter new path: ");
        String newPath = scanner.nextLine();

        Path existingDirectoryPath = Paths.get(CURRENT_PATH, existingPath);
        Path newDirectoryPath = Paths.get(CURRENT_PATH, newPath);

        try(ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<?> filesCopied = executor.submit(() -> {
                copyDirectory(existingDirectoryPath, newDirectoryPath);
            });
            filesCopied.get();
        }

        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void task4() {
        System.out.print("Enter path: ");
        String path = scanner.nextLine();
        System.out.print("Word: ");
        String word = scanner.nextLine();
        Path filePath = Paths.get(CURRENT_PATH, path);
        Path restrictedWordsPath = Paths.get(CURRENT_PATH, "files/restrictedWords.txt");
        Path commonWordsPath = Paths.get(CURRENT_PATH, "files/commonWords.txt");

        try(ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future <?> commonFileSet = executor.submit(() -> fillCommonFile(commonWordsPath, filePath, word));
            commonFileSet.get();
            Future <?> commonFileRefactored = executor.submit(() -> refactorCommonFile(commonWordsPath, restrictedWordsPath));
            commonFileRefactored.get();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    private static void refactorCommonFile(Path commonWordsPath, Path restrictedWordsPath) {
        List<String> restrictedWords = getRestrictedWords(restrictedWordsPath);
        try(BufferedReader reader = new BufferedReader(new FileReader(commonWordsPath.toFile()))) {
            List<String> commonWords = new ArrayList<>();
            String line;
            while((line = reader.readLine()) != null) {
                commonWords.add(line);
            }
            commonWords = commonWords.stream()
                    .filter(w -> !isRestricted(w, restrictedWords))
                    .toList();

            System.out.print("Refactored file words: ");
            reader.close();

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(commonWordsPath.toFile()))) {
                for(String commonWord : commonWords) {
                    writer.write(commonWord);
                    writer.newLine();
                    System.out.print(commonWord + " ");
                }
                System.out.println();
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
            }

        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean isRestricted(String word, List<String> restrictedWords) {
        for(String restrictedWord : restrictedWords) {
            if(word.equals(restrictedWord)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> getRestrictedWords(Path restrictedWordsPath) {
        List<String> restrictedWords = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(restrictedWordsPath.toFile()))) {
            String line;

            while((line = reader.readLine()) != null)
                restrictedWords.add(line);

        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return restrictedWords;
    }

    private static void fillCommonFile(Path commonWordsPath, Path filePath, String word) {
        try{
            System.out.print("Common file words: ");
            Files.walkFileTree(filePath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    boolean isMatch = false;
                    try(BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))){
                        String line;
                        List<String> lines = new ArrayList<>();

                        while((line = reader.readLine()) != null) {
                            lines.add(line);

                            if(line.equals(word))
                                isMatch = true;

                        }

                        if(isMatch){
                            try(BufferedWriter writer = new BufferedWriter(new FileWriter(commonWordsPath.toFile(), true))){
                                for(String l : lines){
                                    writer.write(l);
                                    writer.newLine();
                                    System.out.print(l + " ");
                                }
                            }

                            catch(Exception e){
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                    catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    private static void copyDirectory(Path source, Path target) {
        try {
            final int[] count = {0};
            if (Files.notExists(target))
                Files.createDirectories(target);

            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = target.resolve(source.relativize(dir));
                    if (Files.notExists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = target.resolve(source.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    count[0]++;
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println(count[0] + " files copied");
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void setFactorials(Path filePath, BufferedWriter factorialWriter) {
        System.out.print("Factorials: ");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                int number = Integer.parseInt(line);
                int factorial = getFactorial(number);
                factorialWriter.write(String.valueOf(factorial));
                factorialWriter.newLine();
                System.out.print(factorial + " ");
            }

            System.out.println();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void setPrimes(Path filePath, BufferedWriter primeWriter) {
        System.out.print("Prime numbers: ");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int number = Integer.parseInt(line);

                if (isPrime(number)) {
                    primeWriter.write(String.valueOf(number));
                    primeWriter.newLine();
                    System.out.print(number + " ");
                }

            }
            System.out.println();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void setNumbers(Path filePath){
        System.out.print("Numbers: ");

        try(BufferedWriter initialWriter = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            for (int i = 0; i < 10; i++) {
                int number = random.nextInt(10);
                initialWriter.write(String.valueOf(number));
                initialWriter.newLine();
                System.out.print(number + " ");
            }
        }

        catch(Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println();
    }

    private static boolean isPrime(int number) {
        if(number <= 1)
            return false;

        int count = 0;

        for(int i = 1; i <= number; i++) {
            if(number % i == 0)
                count++;
        }

        return count == 2;
    }

    private static int getFactorial(int number) {
        if(number == 0)
            return 0;

        int factorial = 1;

        for(int i = 1; i <= number; i++)
            factorial *= i;

        return factorial;
    }

}
