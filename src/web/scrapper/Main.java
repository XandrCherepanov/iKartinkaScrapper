package web.scrapper;

import com.jaunt.JauntException;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static void showMainMenu(Map<Integer, Category> categories) {
        System.out.println("1 - Refresh categories");

        for (Map.Entry<Integer, Category> entry : categories.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue().getName());
        }
        System.out.println("q - Quit");
        System.out.print("Select option [q]: ");
    }

    private static void showSubmenu(Category category) {
        if (category == null) {
            return;
        }
        System.out.println("Download " + category.getName() + " category");
    }

    public static void main(String[] args) {
        System.out.println("Welcome to iKartinka Web Scrapper");

        try {
            Downloader downloader = new Downloader();
            Scanner scanner = new Scanner(System.in);

            String input = "q";
            do {
                showMainMenu(downloader.getCategories());
                input = scanner.nextLine().trim();

                if (input.equals("q") || input.equals("")) break;

                int command = Integer.parseInt(input);
                if (command == 1) {
                    downloader.refreshCategories();
                } else {
                    Category category = downloader.getCategory(command);
                    showSubmenu(category);
                }
            } while (!input.equals("q"));

        } catch (IOException|ClassNotFoundException|JauntException e) {
            System.err.println(e.getMessage());
        }
    }
}
