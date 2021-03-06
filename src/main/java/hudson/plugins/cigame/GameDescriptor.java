package hudson.plugins.cigame;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.plugins.cigame.model.RuleBook;
import hudson.plugins.cigame.model.RuleSet;
import hudson.plugins.cigame.model.ScoreLevel;
import hudson.plugins.cigame.rules.build.BuildResultRule;
import hudson.plugins.cigame.rules.build.BuildRuleSet;
import hudson.plugins.cigame.rules.plugins.checkstyle.CheckstyleRuleSet;
import hudson.plugins.cigame.rules.plugins.findbugs.FindBugsRuleSet;
import hudson.plugins.cigame.rules.plugins.opentasks.OpenTasksRuleSet;
import hudson.plugins.cigame.rules.plugins.pmd.PmdRuleSet;
import hudson.plugins.cigame.rules.plugins.violation.ViolationsRuleSet;
import hudson.plugins.cigame.rules.plugins.warnings.WarningsRuleSet;
import hudson.plugins.cigame.rules.unittesting.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;

// Config page for the application (descriptor of the game plugin)
@Extension
public class GameDescriptor extends BuildStepDescriptor<Publisher> {

    public static final String ACTION_LOGO_SMALL = "/plugin/ci-game/icons/24x24/game.png"; //$NON-NLS-1$
    private transient RuleBook rulebook;
    private transient List<ScoreLevel> scoreLevels;
    private boolean namesAreCaseSensitive = true;
    private int passedTestIncreasingPoints = IncreasingPassedTestsRule.DEFAULT_POINTS;
    private int passedTestDecreasingPoints = DecreasingPassedTestsRule.DEFAULT_POINTS;
    private int failedTestIncreasingPoints = IncreasingFailedTestsRule.DEFAULT_POINTS;
    private int failedTestDecreasingPoints = DecreasingFailedTestsRule.DEFAULT_POINTS;
    private int skippedTestIncreasingPoints = IncreasingSkippedTestsRule.DEFAULT_POINTS;
    private int skippedTestDecreasingPoints = DecreasingSkippedTestsRule.DEFAULT_POINTS;
    private int buildSuccessPoints = BuildResultRule.DEFAULT_SUCCESS_POINTS;
    private int buildFailurePoints = BuildResultRule.DEFAULT_FAILURE_POINTS;

    public GameDescriptor() {
        super(GamePublisher.class);
        load();
    }

    /**
     * Returns the default rule book
     *
     * @return the rule book that is configured for the game.
     */
    public RuleBook getRuleBook() {
        if (rulebook == null) {
            rulebook = new RuleBook();

            addRuleSetIfAvailable(rulebook, new BuildRuleSet());
            addRuleSetIfAvailable(rulebook, new UnitTestingRuleSet());
            addRuleSetIfAvailable(rulebook, new OpenTasksRuleSet());
            addRuleSetIfAvailable(rulebook, new ViolationsRuleSet());
            addRuleSetIfAvailable(rulebook, new PmdRuleSet());
            addRuleSetIfAvailable(rulebook, new FindBugsRuleSet());
            addRuleSetIfAvailable(rulebook, new WarningsRuleSet());
            addRuleSetIfAvailable(rulebook, new CheckstyleRuleSet());
        }
        return rulebook;
    }

    private void addRuleSetIfAvailable(RuleBook book, RuleSet ruleSet) {
        if (ruleSet.isAvailable()) {
            book.addRuleSet(ruleSet);
        }
    }

    public List<ScoreLevel> getScoreLevels() {
        if (scoreLevels == null) {
            String jsonScoreLevels = "[]";
            try {
                InputStream inputStream = this.getClass().getResourceAsStream("/score-levels.json");
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer, "UTF-8");
                jsonScoreLevels = writer.toString();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ScoreLevel>>() {
            }.getType();
            scoreLevels = gson.fromJson(jsonScoreLevels, listType);
            String rootUrl = Hudson.getInstance().getRootUrl();
            for (ScoreLevel scoreLevel : scoreLevels) {
                String imageUrl = scoreLevel.getImageUrl();
                if (imageUrl.startsWith("/")) {
                    scoreLevel.setImageUrl(rootUrl + imageUrl);
                }
            }
        }
        return scoreLevels;
    }

    // config page heading
    @Override
    public String getDisplayName() {
        return Messages.Plugin_Title();
    }

    // creates a instance with form data; but this only creates empty new object
    @Override
    public GamePublisher newInstance(StaplerRequest req, JSONObject formData)
            throws hudson.model.Descriptor.FormException {
        return new GamePublisher();
    }

    // invoked when even properties are updated (global properties configured with global.jelly
    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    public boolean getNamesAreCaseSensitive() {
        return namesAreCaseSensitive;
    }

    public void setNamesAreCaseSensitive(boolean namesAreCaseSensitive) {
        this.namesAreCaseSensitive = namesAreCaseSensitive;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> arg0) {
        return true;
    }

    public int getPassedTestIncreasingPoints() {
        return passedTestIncreasingPoints;
    }

    public void setPassedTestIncreasingPoints(int passedTestIncreasingPoints) {
        this.passedTestIncreasingPoints = passedTestIncreasingPoints;
    }

    public int getPassedTestDecreasingPoints() {
        return passedTestDecreasingPoints;
    }

    public void setPassedTestDecreasingPoints(int passedTestDecreasingPoints) {
        this.passedTestDecreasingPoints = passedTestDecreasingPoints;
    }

    public int getFailedTestIncreasingPoints() {
        return failedTestIncreasingPoints;
    }

    public void setFailedTestIncreasingPoints(int failedTestIncreasingPoints) {
        this.failedTestIncreasingPoints = failedTestIncreasingPoints;
    }

    public int getFailedTestDecreasingPoints() {
        return failedTestDecreasingPoints;
    }

    public void setFailedTestDecreasingPoints(int failedTestDecreasingPoints) {
        this.failedTestDecreasingPoints = failedTestDecreasingPoints;
    }

    public int getSkippedTestIncreasingPoints() {
        return skippedTestIncreasingPoints;
    }

    public void setSkippedTestIncreasingPoints(int skippedTestIncreasingPoints) {
        this.skippedTestIncreasingPoints = skippedTestIncreasingPoints;
    }

    public int getSkippedTestDecreasingPoints() {
        return skippedTestDecreasingPoints;
    }

    public void setSkippedTestDecreasingPoints(int skippedTestDecreasingPoints) {
        this.skippedTestDecreasingPoints = skippedTestDecreasingPoints;
    }

    public int getBuildSuccessPoints() {
        return buildSuccessPoints;
    }

    public void setBuildSuccessPoints(int buildSuccessPoints) {
        this.buildSuccessPoints = buildSuccessPoints;
    }

    public int getBuildFailurePoints() {
        return buildFailurePoints;
    }

    public void setBuildFailurePoints(int buildFailurePoints) {
        this.buildFailurePoints = buildFailurePoints;
    }
}
