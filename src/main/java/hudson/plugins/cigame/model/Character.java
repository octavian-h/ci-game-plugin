package hudson.plugins.cigame.model;

/**
 * @author hasna
 * @since 1.20-SNAPSHOT
 */
public class Character {
    private String name;
    private String imageUrl;

    public Character(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
