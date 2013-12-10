package hudson.plugins.cigame.rules.plugins.findbugs;

import hudson.maven.MavenBuild;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.findbugs.FindBugsResult;
import hudson.plugins.findbugs.FindBugsResultAction;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class NewFindBugsWarningsRuleTest {

    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        addFindBugsWarnings(build, 10);

        AbstractBuild prevBuild = mock(AbstractBuild.class);
        when(prevBuild.getResult()).thenReturn(Result.SUCCESS);
        addFindBugsWarnings(prevBuild, 5);

        NewFindBugsWarningsRule rule = new NewFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(prevBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }

    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(null);
        addFindBugsWarnings(build, 42);

        NewFindBugsWarningsRule rule = new NewFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(null, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }

    @Test
    public void assertIfPreviousBuildFailedResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.FAILURE);
        FindBugsResult result = mock(FindBugsResult.class);
        FindBugsResult previosResult = mock(FindBugsResult.class);
        FindBugsResultAction action = new FindBugsResultAction(build, mock(HealthDescriptor.class), result);
        FindBugsResultAction previousAction = new FindBugsResultAction(previousBuild, mock(HealthDescriptor.class), previosResult);
        when(build.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(action));
        when(previousBuild.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(previousAction));

        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);

        RuleResult ruleResult = new NewFindBugsWarningsRule(Priority.LOW, -4).evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }

    @Test
    public void assertIfPreviousHasErrorsResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        FindBugsResult result = mock(FindBugsResult.class);
        FindBugsResult previosResult = mock(FindBugsResult.class);
        when(previosResult.hasError()).thenReturn(true);
        FindBugsResultAction action = new FindBugsResultAction(build, mock(HealthDescriptor.class), result);
        FindBugsResultAction previousAction = new FindBugsResultAction(previousBuild, mock(HealthDescriptor.class), previosResult);
        when(build.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(action));
        when(previousBuild.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(previousAction));

        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);

        RuleResult ruleResult = new NewFindBugsWarningsRule(Priority.LOW, -4).evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }

    @Test
    public void assertNewMavenModuleCountsAsNewWarnings() {
        AbstractBuild build = mock(MavenBuild.class);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        addFindBugsWarnings(build, 7);

        RuleResult ruleResult = new NewFindBugsWarningsRule(Priority.LOW, -1).evaluate(null, build);
        assertNotNull(ruleResult);
        assertThat("Points should be -7", ruleResult.getPoints(), is(-7d));
    }

    private static void addFindBugsWarnings(AbstractBuild<?, ?> build, int numberOfWarnings) {
        FindBugsResult result = mock(FindBugsResult.class);
        FindBugsResultAction action = new FindBugsResultAction(build, mock(HealthDescriptor.class), result);
        when(build.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(action));

        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(numberOfWarnings);
    }
}
