package hudson.plugins.cigame.rules.unittesting;

import hudson.model.Hudson;
import hudson.plugins.cigame.GameDescriptor;
import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule that gives points for increasing the number of passed tests. By default 1 mark given.
 *
 * @author Unknown
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class IncreasingPassedTestsRule extends AbstractPassedTestsRule {

    public static final int DEFAULT_POINTS = 1;

    private int getPoints() {
        GameDescriptor gameDescriptor = Hudson.getInstance().getDescriptorByType(GameDescriptor.class);
        return gameDescriptor != null ? gameDescriptor.getPassedTestIncreasingPoints() : DEFAULT_POINTS;
    }

    public String getName() {
        return Messages.UnitTestingRuleSet_IncreasingPassedRule_Name();
    }

    @Override
    protected String getResultDescription(Integer testDiff) {
        return Messages.UnitTestingRuleSet_IncreasingPassedRule_Count(testDiff);
    }

    @Override
    protected RuleResult<Integer> evaluate(int passedTestDiff) {
        if (passedTestDiff > 0) {
            return new RuleResult<Integer>(passedTestDiff * getPoints(),
                    Messages.UnitTestingRuleSet_IncreasingPassedRule_Count(passedTestDiff),
                    passedTestDiff);
        }
        return null;
    }

}
