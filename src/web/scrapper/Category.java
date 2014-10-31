package web.scrapper;

import com.jaunt.NodeNotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Category implements Serializable {

    private static final long serialVersionUID = 42L;
    private String name;
    private String url;
    private int allCount;
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
}
