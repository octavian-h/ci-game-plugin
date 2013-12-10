package hudson.plugins.cigame;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.model.User;
import org.jvnet.hudson.test.HudsonTestCase;

public class UserScorePropertyDescriptorIntegrationTest extends HudsonTestCase {

    public void testConfiguringWorksForNewUser() throws Exception {
        HtmlForm userConfigurationForm = new WebClient().goTo(User.get("test").getUrl() + "/configure").getFormByName("config");
        submit(userConfigurationForm);
    }
}
