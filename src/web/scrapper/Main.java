package web.scrapper;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("iKartinka Web Scrapper");
        System.out.println("Select option: ");
        System.out.println("1 - Refresh categories");

        Downloader downloader = new Downloader();

        try {
            File f = new File(downloader.categoriesFile);
            if (f.exists()) {
                DataInputStream inputStream = new DataInputStream(
                        new FileInputStream(f));
                int count = inputStream.readInt();
                for (int i = 0; i < count; i++) {
                    int commandIndex = inputStream.readInt();
                    String commandName = inputStream.readUTF();
                    inputStream.readUTF();
                    System.out.println(commandIndex + " - " + commandName);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();

        try {
            if (input == 1) {
                downloader.refreshCategories();
            }
//            Elements categories = downloader.findCategories();
//            for (Element elem : categories) {
//                System.out.println(elem);
//            }

        }
        catch (JauntException|IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
