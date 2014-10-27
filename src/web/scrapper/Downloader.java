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
                    element.getAttx("href")
            ));
        }
        return categories;
    }

    public void refreshCategories() throws JauntException, IOException {
        findCategories();
        saveCategoriesList();
    }

    private void saveCategoriesList() throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(
                new FileOutputStream(categoriesFile));
        outputStream.writeInt(categories.size());

        for (Map.Entry<Integer, Category> entry : categories.entrySet()) {
            outputStream.writeObject(entry.getKey());
            outputStream.writeObject(entry.getValue());
        }
    }

    private void loadCategoriesList() throws IOException, ClassNotFoundException {
        File f = new File(categoriesFile);
        if (f.exists()) {
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream(f));
            int count = inputStream.readInt();
            categories = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                Integer commandIndex = (Integer) inputStream.readObject();
                Category category = (Category) inputStream.readObject();
                categories.put(commandIndex, category);
            }
        }
    }

    public Map<Integer,Category> getCategories() {
        return categories;
    }
}
