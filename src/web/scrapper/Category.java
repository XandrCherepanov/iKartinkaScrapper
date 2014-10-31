package web.scrapper;

import com.jaunt.*;
import com.jaunt.util.HandlerForBinary;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Category implements Serializable {

    private static final long serialVersionUID = 42L;
    private String name;
    private String url;
    private int allCount;
    private int downloaded = 0;
    private int lastId = 0;
    private int lastPage = 1;
    private final int perPage = 150;
    private UserAgent userAgent;

    public Category(String name, String url, UserAgent userAgent) {
        this.name = name;
        this.url = url;
        this.userAgent = userAgent;
    }

    public String getName() {
        return name;
    }

    public int getAllCount() throws ResponseException, NodeNotFound {
        userAgent.visit(url);
        String results = userAgent.doc.findFirst("<div class=results>").getText();

        Pattern pattern = Pattern.compile("(\\d+)\\s\\(");
        Matcher matcher = pattern.matcher(results);
        while (matcher.find()) {
            allCount = Integer.parseInt(matcher.group(1));
        }

        return allCount;
    }

    public String getUrl() {
        return url;
    }

    public void download() {
        File directory = new File(name);
        if (!directory.exists()) {
            directory.mkdir();
        }

        writeStatus();

        int countOfPages = (int) Math.ceil((double) allCount / perPage);
//        System.out.println("Count of pages: " + countOfPages);
        if (allCount > downloaded) {
            try {
                userAgent.visit(url + "?sort=p.date_added&order=ASC&limit=" + perPage + "&page=" + lastPage);
                Elements pictures = userAgent.doc.findFirst("<ul class=normal>")
                        .findEvery("<li>");
                Element elemLi = pictures.getElement(0);
//                String link = elemLi.findFirst("<a class=zoom>").getAt("href");
                int imageId = Integer.parseInt(elemLi.findFirst("<div data-id>").getAt("data-id"));

//                System.out.println("Link: " + link);
//                System.out.println("ID: " + imageId);
                userAgent.visit("http://ikartinka.com/index.php?route=product/download/window&product_id=" +
                        imageId + "&size=1920x1200");
                String imageSrc = userAgent.doc.findFirst("<div id=image>")
                        .getElement(0)
                        .getAt("src");

                HandlerForBinary handlerForBinary = new HandlerForBinary();
                userAgent.setHandler("image/jpeg", handlerForBinary);
                userAgent.visit(imageSrc);
                DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(
                        new File(name + File.separator + name + "_" + imageId + ".jpg")));
                byte[] image = handlerForBinary.getContent();
                outputStream.write(image);

                System.out.println("Image " + imageId + " (" + image.length / 1024 + "Kb) downloaded");

            } catch (ResponseException | NodeNotFound | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeStatus() {
        File statusFile = new File(name + File.separator + "status.txt");
        try {
            DataOutputStream outputStream = new DataOutputStream(
                    new FileOutputStream(statusFile));
            outputStream.writeInt(downloaded);
            outputStream.writeInt(lastId);
            outputStream.writeInt(lastPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
