package web.scrapper;

import com.jaunt.*;

public class Downloader {

    private String baseUrl = "http://ikartinka.com";

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

    public static void main(String[] args) {
        System.out.println("iKartinka Web Scrapper");
        Downloader downloader = new Downloader();
        try {
            Elements categories = downloader.findCategories();
            for (Element elem : categories) {
                System.out.println(elem);
            }

        }
        catch(JauntException e) {
            System.err.println(e);
        }
    }
}
