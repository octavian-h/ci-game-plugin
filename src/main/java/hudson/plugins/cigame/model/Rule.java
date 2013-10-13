package hudson.plugins.cigame.model;

import hudson.model.AbstractBuild;

/**
 * Rule interface.
 *
 * @author Erik Ramfelt
 */
public interface Rule<T> {
    /**
     * Returns the name of the rule
     *
     * @return name of the rule
     */
    String getName();

    /**
     * Evaluate the build and return the points for it
     *
     * @param build build to calculate points for
     * @return the result of the rule; null if the rule should be ignored.
     */
//    RuleResult evaluate(AbstractBuild<?, ?> build);

    /**
     * Evaluates the rule for the current build compared to a previous build.
     * <p/>
     * Please note that 'previousBuild' may not necessarily be the immediate previous build
     * (i.e. with build number n-1), as builds which have been aborted (or otherwise not build)
     * are skipped. This is especially true for not-build maven modules in a Maven project
     * which the 'incremental build' option enabled.
     *
     * @param previousBuild the previous build with usable results (may be null!)
     * @param build         the current build (may be null!)
     * @return the rule result or null, if no points should be awarded
     */
    RuleResult<T> evaluate(AbstractBuild<?, ?> previousBuild, AbstractBuild<?, ?> build);
}
