package web.scrapper;

import java.io.Serializable;

public class Category implements Serializable {

    private static final long serialVersionUID = 42L;
    private String name;
    private String url;

    public Category(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }
}
