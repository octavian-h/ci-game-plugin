package hudson.plugins.cigame.rules.build;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule that gives points on the result of the build.
 */
public class BuildResultRule implements Rule<Void> {

    private int failurePoints;
    private int successPoints;

    public BuildResultRule() {
        this(1, -10);
    }

    public BuildResultRule(int successPoints, int failurePoints) {
        this.successPoints = successPoints;
        this.failurePoints = failurePoints;
    }

    @Override
    public String getName() {
        return Messages.BuildRuleSet_BuildResult(); //$NON-NLS-1$
    }

    @Override
    public RuleResult<Void> evaluate(AbstractBuild<?, ?> previousBuild, AbstractBuild<?, ?> build) {
        Result result = build.getResult();
        Result lastResult = null;
        if (previousBuild != null) {
            lastResult = previousBuild.getResult();
        }
        return evaluate(result, lastResult);
    }

    RuleResult<Void> evaluate(Result result, Result lastResult) {
        if (result == Result.SUCCESS) {
            return new RuleResult<Void>(successPoints, Messages.BuildRuleSet_BuildSuccess()); //$NON-NLS-1$
        }
        if (result == Result.FAILURE) {
            if ((lastResult == null)
                    || (lastResult.isBetterThan(Result.FAILURE))) {
                return new RuleResult<Void>(failurePoints, Messages.BuildRuleSet_BuildFailed()); //$NON-NLS-1$
            }
        }
        return null;
    }
}
