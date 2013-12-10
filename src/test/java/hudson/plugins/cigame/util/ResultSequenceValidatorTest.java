package hudson.plugins.cigame.util;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class ResultSequenceValidatorTest {

    @Test
    public void assertResultBelowThresholdIsNotValidated() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        assertThat(new ResultSequenceValidator(Result.SUCCESS, 1).isValid(build), is(false));
    }

    @Test
    public void assertShortSequenceIsNotValidated() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(build.getPreviousBuild()).thenReturn(null);
        assertThat(new ResultSequenceValidator(Result.SUCCESS, 2).isValid(build), is(false));
    }

    @Test
    public void assertLastBuildIsBelowThresholdIsNotValidated() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(previousBuild.getResult()).thenReturn(Result.UNSTABLE);
        assertThat(new ResultSequenceValidator(Result.SUCCESS, 2).isValid(build), is(false));
    }

    @Test
    public void assertBuildSequenceAboveThresholdIsValidated() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(previousBuild.getResult()).thenReturn(Result.UNSTABLE);
        assertThat(new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build), is(true));
    }
}
