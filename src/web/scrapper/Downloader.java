package web.scrapper;

import com.jaunt.*;
import java.io.*;

public class Downloader {

    private String baseUrl = "http://ikartinka.com";
    public String categoriesFile = "categories.txt";

    private UserAgent userAgent;

    public Downloader() {
        userAgent = new UserAgent();
    }

    public Downloader(String baseUrl) {
        this();
        this.baseUrl = baseUrl;
    }

    public Elements findCategories() throws JauntException {
        userAgent.visit(baseUrl);
        return userAgent.doc.findFirst("<ul class=box-category>").findEvery("<a>");
    }

    public void refreshCategories() throws JauntException, IOException {
        Elements categories = findCategories();

        DataOutputStream outputStream = new DataOutputStream(
                new FileOutputStream(categoriesFile));
        outputStream.writeInt(categories.size());
        int commandIndex = 2;
        for (Element elem : categories) {
            outputStream.writeInt(commandIndex);
            outputStream.writeUTF(elem.getAttx("href"));
            outputStream.writeUTF(elem.getText());
            commandIndex++;
        }
    }
}
