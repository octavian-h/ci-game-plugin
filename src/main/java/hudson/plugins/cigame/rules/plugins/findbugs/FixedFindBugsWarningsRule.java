package hudson.plugins.cigame.rules.plugins.findbugs;

import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.RuleResult;

import java.util.Collection;

public class FixedFindBugsWarningsRule extends AbstractFindBugsWarningsRule {

    private int pointsForEachFixedWarning;

    public FixedFindBugsWarningsRule(Priority priority, int pointsForEachFixedWarning) {
        super(priority);
        this.pointsForEachFixedWarning = pointsForEachFixedWarning;
    }

    @Override
    protected RuleResult<Integer> evaluate(int previousAnnotations, int currentAnnotations) {

        if (currentAnnotations < previousAnnotations) {
            int fixedWarnings = previousAnnotations - currentAnnotations;
            return new RuleResult<Integer>(fixedWarnings * pointsForEachFixedWarning,
                    Messages.FindBugsRuleSet_FixedWarningsRule_Count(Math.abs(fixedWarnings), priority.name()),
                    fixedWarnings);
        }

        return EMPTY_RESULT;
    }

    @Override
    public RuleResult<?> aggregate(Collection<RuleResult<Integer>> results) {
        double score = 0.0;
        int fixedWarnings = 0;
        for (RuleResult<Integer> result : results) {
            if (result != null) {
                score += result.getPoints();
                fixedWarnings += result.getAdditionalData();
            }
        }

        if (score != 0.0) {
            return new RuleResult<Void>(score,
                    Messages.FindBugsRuleSet_FixedWarningsRule_Count(Math.abs(fixedWarnings), priority.name()));
        }
        return EMPTY_RESULT;
    }

    @Override
    public String getName() {
        return Messages.FindBugsRuleSet_FixedWarningsRule_Title(priority.name()); //$NON-NLS-1$
    }
}
