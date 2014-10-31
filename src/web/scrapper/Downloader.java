package web.scrapper;

import com.jaunt.*;
import java.io.*;
import java.util.*;

public class Downloader {

    private String baseUrl = "http://ikartinka.com";
    public String categoriesFile = "categories.txt";
    public int commandStartIndex = 2;
    private Map<Integer, Category> categories = new HashMap<>();
    private UserAgent userAgent;

    public Downloader() throws IOException, ClassNotFoundException {
        userAgent = new UserAgent();
        loadCategoriesList();
    }

    public Map<Integer, Category> findCategories() throws JauntException {
        userAgent.visit(baseUrl);
        Elements elements = userAgent.doc.findFirst("<ul class=box-category>").findEvery("<a>");

        categories.clear();
        int commandIndex = commandStartIndex;
        for (Element element : elements) {
            categories.put(commandIndex++, new Category(
                    element.getText().trim(),
                    element.getAttx("href"),
                    userAgent
            ));
        }
        return categories;
    }

    public void refreshCategories() throws JauntException, IOException {
        findCategories();
        saveCategoriesList();
    }

    private void saveCategoriesList() throws IOException {
        DataOutputStream outputStream = new DataOutputStream(
                new FileOutputStream(categoriesFile));
        outputStream.writeInt(categories.size());

        for (Map.Entry<Integer, Category> entry : categories.entrySet()) {
            outputStream.writeInt(entry.getKey());
            Category category = entry.getValue();
            outputStream.writeUTF(category.getName());
            outputStream.writeUTF(category.getUrl());
        }
    }

    private void loadCategoriesList() throws IOException, ClassNotFoundException {
        File f = new File(categoriesFile);
        if (f.exists()) {
            DataInputStream inputStream = new DataInputStream(
                    new FileInputStream(f));
            int count = inputStream.readInt();
            categories = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                Integer commandIndex = inputStream.readInt();
                Category category = new Category(inputStream.readUTF(), inputStream.readUTF(), userAgent);
                categories.put(commandIndex, category);
            }
        }
    }

    public Map<Integer,Category> getCategories() {
        return categories;
    }

    public Category getCategory(int index) {
        return categories.get(index);
    }
}
