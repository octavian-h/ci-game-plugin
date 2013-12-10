package hudson.plugins.cigame.model;

import java.util.Collection;

/**
 * A {@link Rule} which is able to aggregate the scores for sub parts of a job.
 * This is e.g. used to calculate the scores for incremental maven multi module builds.
 *
 * @author kutzi
 */
public interface AggregatableRule<T> extends Rule<T> {

    /**
     * Aggregates several rule results calculated by the same rule into one.
     * This is currently (only) used to aggregate build results for maven multi module
     * builds (especially with the 'incremental build' option).
     */
    RuleResult<?> aggregate(Collection<RuleResult<T>> results);
}
