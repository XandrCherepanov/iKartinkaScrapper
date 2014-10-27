package web.scrapper;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static void showMainMenu(Map<Integer, Category> categories) {
        System.out.println("Select option: ");
        System.out.println("1 - Refresh categories");

        for (Map.Entry<Integer, Category> entry : categories.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue().getName());
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to iKartinka Web Scrapper");

        try {
            Downloader downloader = new Downloader();
            Scanner scanner = new Scanner(System.in);

            int input = 1;
            do {
                showMainMenu(downloader.getCategories());
                input = scanner.nextInt();
                if (input == 1) {
                    downloader.refreshCategories();
                }
            } while (input == 1);

        } catch (IOException|ClassNotFoundException|JauntException e) {
            System.err.println(e.getMessage());
        }
    }
}
