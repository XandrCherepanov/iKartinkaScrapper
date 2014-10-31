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
    private final int perPage = 50;
    private UserAgent userAgent;
    HandlerForBinary handlerForBinary = new HandlerForBinary();

    public Category(String name, String url, UserAgent userAgent) {
        this.name = name;
        this.url = url;
        this.userAgent = userAgent;
        loadStatus();
        handlerForBinary = new HandlerForBinary();

    }

    public String getName() {
        return name;
    }

    public int getAllCount() throws ResponseException, NotFound {
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

        if (allCount > downloaded) {
            try {
                while (lastPage <= countOfPages) {
                    userAgent.visit(url + "?sort=p.date_added&order=ASC&limit=" + perPage + "&page=" + lastPage);
                    Elements pictures = userAgent.doc.findFirst("<ul class=normal>")
                            .findEvery("<li>");
                    for (Element elem : pictures) {
                        int imageId = Integer.parseInt(elem.findFirst("<div data-id>").getAt("data-id"));
                        if (lastId < imageId) {
                            downloadImage(imageId);
                        }
                    }
                    lastPage++;
                    writeStatus();
                }

            } catch (ResponseException | NotFound | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadImage(int imageId)
            throws ResponseException, NotFound, IOException {
        userAgent.visit("http://ikartinka.com/index.php?route=product/download/window&product_id=" +
                imageId + "&size=1920x1200");
        String imageSrc = userAgent.doc.findFirst("<div id=image>")
                .getElement(0)
                .getAt("src");

        File imageFile = new File(name + File.separator + name + "_" + imageId + ".jpg");
        if (!imageFile.exists()) {
            userAgent.setHandler("image/jpeg", handlerForBinary);
            userAgent.visit(imageSrc);
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(
                    imageFile));
            byte[] image = handlerForBinary.getContent();
            outputStream.write(image);
            outputStream.close();

            lastId = imageId;
            downloaded++;
            writeStatus();

            System.out.println("Image " + imageId + " (" + image.length / 1024 + "Kb) downloaded");
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
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStatus() {
        File statusFile = new File(name + File.separator + "status.txt");
        try {
            if (statusFile.exists()) {
                DataInputStream inputStream = new DataInputStream(
                        new FileInputStream(statusFile));
                downloaded = inputStream.readInt();
                lastId = inputStream.readInt();
                lastPage = inputStream.readInt();
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDownloadedCount() {
        return downloaded;
    }
}
