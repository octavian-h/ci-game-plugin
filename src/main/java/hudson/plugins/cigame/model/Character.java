package hudson.plugins.cigame.model;

/**
 * @author hasna
 * @since 1.0-SNAPSHOT
 */
public class Character {
    private String name;
    private String url;

    public Character(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
