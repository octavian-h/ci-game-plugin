package hudson.plugins.cigame.model;

import hudson.model.AbstractBuild;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScoreCardTest {

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateThrownInGetScores() {
        ScoreCard sc = new ScoreCard();
        sc.getScores();
    }

    @Test
    public void assertThatEmptyRuleResultIsNotUsed() {
        Rule rule = mock(Rule.class);
        when(rule.evaluate(isA(AbstractBuild.class), isA(AbstractBuild.class))).thenReturn(RuleResult.EMPTY_RESULT);
        ScoreCard card = new ScoreCard();
        card.record(mock(AbstractBuild.class), new RuleSet("test", Arrays.asList(new Rule[]{rule})), null);
        assertThat(card.getScores().size(), is(0));
    }

    @Test
    public void assertRuleNull() {
        List<Rule> list = new ArrayList<Rule>();
        list.add(null);
        ScoreCard card = new ScoreCard();
        card.record(mock(AbstractBuild.class), new RuleSet("test", list), null);
    }

    @Test
    public void assertEmptyRuleBookDoesNotThrowIllegalException() {
        ScoreCard scoreCard = new ScoreCard();
        scoreCard.record(mock(AbstractBuild.class), new RuleBook(), null);
        assertThat(scoreCard.getTotalPoints(), is(0d));
    }
}
