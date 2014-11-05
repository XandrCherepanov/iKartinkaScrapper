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
    private int viewed = 0;
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
        visit(url);
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

        if (allCount > viewed) {
            try {
                while (lastPage <= countOfPages) {
                    System.out.println("Page " + lastPage);
                    visit(url + "?sort=p.date_added&order=ASC&limit=" + perPage + "&page=" + lastPage);
                    Elements pictures = userAgent.doc.findFirst("<ul class=normal>")
                            .findEvery("<li>");
                    for (Element elem : pictures) {
                        int imageId = Integer.parseInt(elem.findFirst("<div data-id>").getAt("data-id"));
                        downloadImage(imageId);
                        viewed++;
                        writeStatus();
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
        File imageFile = new File(name + File.separator + name + "_" + imageId + ".jpg");
        if (imageFile.exists()) {
            lastId = imageId;
            downloaded++;
            writeStatus();
            return;
        }

        visit("http://ikartinka.com/index.php?route=product/download/window&product_id=" +
                imageId + "&size=1920x1200");

        String imageSrc = userAgent.doc.findFirst("<div id=image>")
                .getElement(0)
                .getAt("src");

        if (imageSrc.equals("")) return;

        userAgent.setHandler("image/jpeg", handlerForBinary);
        visit(imageSrc);
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


    private void writeStatus() {
        File statusFile = new File(name + File.separator + "status.txt");
        try {
            DataOutputStream outputStream = new DataOutputStream(
                    new FileOutputStream(statusFile));
            outputStream.writeInt(downloaded);
            outputStream.writeInt(viewed);
            outputStream.writeInt(lastId);
            outputStream.writeInt(lastPage);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void visit(String url) {
        boolean success = false;
        while (!success) {
            try {
                userAgent.visit(url);
                success = true;
            } catch (ResponseException e) {
                HttpResponse response = e.getResponse();
                if(response != null) {
                    System.err.println("Requested url: " + response.getRequestedUrlMsg());
                    System.err.println("HTTP error code: " + response.getStatus());
                    System.err.println("Error message: " + response.getMessage());
                } else {
                    System.out.println("Connection error, no response!");
                }
            }
        }
    }

    private void loadStatus() {
        File statusFile = new File(name + File.separator + "status.txt");
        try {
            if (statusFile.exists()) {
                DataInputStream inputStream = new DataInputStream(
                        new FileInputStream(statusFile));
                downloaded = inputStream.readInt();
                viewed = inputStream.readInt();
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

    public int getViewedCount() {
        return viewed;
    }
}
