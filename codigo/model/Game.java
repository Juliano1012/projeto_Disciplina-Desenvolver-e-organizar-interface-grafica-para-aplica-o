
package ldj.model;

public class Game {
    public String title;
    public String imageUrl;
    public String openUrl;
    public String portalLabel;
    public String portalUrl;

    public Game(String title, String imageUrl,
                String openUrl, String portalLabel, String portalUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.openUrl = openUrl;
        this.portalLabel = portalLabel;
        this.portalUrl = portalUrl;
    }
}
