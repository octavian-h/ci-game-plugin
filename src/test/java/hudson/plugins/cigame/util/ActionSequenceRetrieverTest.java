package hudson.plugins.cigame.util;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Build;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class ActionSequenceRetrieverTest {

    @Test
    public void assertNullIsReturnedWithBuildWithoutRequestedAction() {
        Build<?, ?> build = mock(Build.class);
        when(build.getActions(Action.class)).thenReturn(null);
        assertThat(new ActionSequenceRetriever(Action.class, 1).getSequence(build), is(nullValue()));
    }

    @Test
    public void assertNullIsReturnedWithSingleBuildWhenLongerSequenceIsRequested() {
        AbstractBuild build = mock(Build.class);
        Action action = mock(Action.class);
        when(build.getActions(Action.class)).thenReturn(Arrays.asList(action));
        assertThat(new ActionSequenceRetriever(Action.class, 2).getSequence(build), is(nullValue()));
    }

    @Test
    public void assertNullIsReturnedWithLastBuildWithoutRequestedAction() {
        AbstractBuild build = mock(Build.class);
        AbstractBuild previousBuild = mock(Build.class);
        Action action = mock(Action.class);
        when(build.getActions(Action.class)).thenReturn(Arrays.asList(action));
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(previousBuild.getActions(Action.class)).thenReturn(null);
        assertThat(new ActionSequenceRetriever(Action.class, 2).getSequence(build), is(nullValue()));
    }

    @Test
    public void assertListIsReturnedForCompleteSequence() {
        AbstractBuild build = mock(Build.class);
        AbstractBuild previousBuild = mock(Build.class);
        Action action = mock(Action.class);
        when(build.getActions(Action.class)).thenReturn(Arrays.asList(action));
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(previousBuild.getActions(Action.class)).thenReturn(Arrays.asList(action));

        List<List<Action>> actionList = new ActionSequenceRetriever(Action.class, 2).getSequence(build);
        assertThat(actionList, is(notNullValue()));
        assertThat(actionList.size(), is(2));
    }
}
