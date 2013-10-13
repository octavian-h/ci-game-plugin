package hudson.plugins.cigame.rules.plugins.findbugs;

import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.RuleResult;

import java.util.Collection;

public class NewFindBugsWarningsRule extends AbstractFindBugsWarningsRule {

    private int pointsForEachNewWarning;

    public NewFindBugsWarningsRule(Priority priority, int pointsForEachNewWarning) {
        super(priority);
        this.pointsForEachNewWarning = pointsForEachNewWarning;
    }

    @Override
    public RuleResult<?> aggregate(Collection<RuleResult<Integer>> results) {
        double score = 0.0;
        int newWarnings = 0;
        for (RuleResult<Integer> result : results) {
            if (result != null) {
                score += result.getPoints();
                newWarnings += result.getAdditionalData();
            }
        }

        if (score != 0.0) {
            return new RuleResult<Void>(score,
                    Messages.FindBugsRuleSet_NewWarningsRule_Count(Math.abs(newWarnings), priority.name()));
        }
        return EMPTY_RESULT;
    }

    @Override
    protected RuleResult<Integer> evaluate(int previousAnnotations, int currentAnnotations) {

        if (currentAnnotations > previousAnnotations) {
            int newWarnings = currentAnnotations - previousAnnotations;
            return new RuleResult<Integer>(newWarnings * pointsForEachNewWarning,
                    Messages.FindBugsRuleSet_NewWarningsRule_Count(Math.abs(newWarnings), priority.name()),
                    newWarnings);
        }

        return EMPTY_RESULT;
    }

    @Override
    public String getName() {
        return Messages.FindBugsRuleSet_NewWarningsRule_Title(priority.name()); //$NON-NLS-1$
    }
}
