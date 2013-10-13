package hudson.plugins.cigame;

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.recipes.LocalData;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScoreCardActionIntegrationTest extends HudsonTestCase {

    @LocalData
    public void testThatUsernameWithDifferentCasingIsDisplayedAsOne() throws Exception {
        hudson.getDescriptorByType(GameDescriptor.class).setNamesAreCaseSensitive(false);
        HtmlTable table = (HtmlTable) new WebClient().goTo("job/multiple-culprits/4/ci-game/").getHtmlElementById("game.culprits");
        assertThat(table.getRowCount(), is(2));
    }
    
    @LocalData
    public void testThatUsernameWithDifferentCasingIsNotDisplayedAsOne() throws Exception {
        HtmlTable table = (HtmlTable) new WebClient().goTo("job/multiple-culprits/4/ci-game/").getHtmlElementById("game.culprits");
        assertThat(table.getRowCount(), is(3));        
    }
}
