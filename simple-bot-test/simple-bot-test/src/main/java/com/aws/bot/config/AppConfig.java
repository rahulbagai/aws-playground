import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AppConfig {

    private String slackSigningSecret;
    private String slackToken;
    private String witToken;

    public AppConfig() {
        Config config = ConfigFactory.load();
        String slackSigningSecret = config.getString("slack.signingSecret");
        String slackToken = config.getString("slack.token");
        String witToken = config.getString("wit.token");
    }

    public getSlackSigningSecret() {
        return slackSigningSecret;
    }

}
