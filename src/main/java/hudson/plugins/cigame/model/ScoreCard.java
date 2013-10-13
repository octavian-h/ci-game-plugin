package hudson.plugins.cigame.model;

import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.plugins.cigame.util.BuildUtil;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.*;

/**
 * Score card containing the results of evaluating the rules against a build.
 */
@ExportedBean(defaultVisibility = 999)
public class ScoreCard {

    private List<Score> scores;

    /**
     * Record points for the rules in the rule set
     *
     * @param build    build to evaluate
     * @param ruleset  rule set to use for evaluation
     * @param listener
     */
    public void record(AbstractBuild<?, ?> build, RuleSet ruleset, BuildListener listener) {

        List<Score> scoresForBuild = new LinkedList<Score>();
        for (Rule rule : ruleset.getRules()) {
            if (null != rule) {
                if (listener != null) {
                    listener.getLogger().append("[ci-game] evaluating rule: ").append(rule.getName()).append("\n");
                }
                RuleResult<?> result = evaluate(build, rule);
                if ((result != null) && (result.getPoints() != 0)) {
                    Score score = new Score(ruleset.getName(), rule.getName(), result.getPoints(), result.getDescription());
                    scoresForBuild.add(score);
                    if (listener != null) {
                        listener.getLogger().append("[ci-game] scored: ").append(Double.toString(score.getValue())).append("\n");
                    }
                }
            } else {
                if (listener != null) {
                    listener.getLogger().append("[ci-game] null rule encountered\n");
                }
            }
        }

        // prevent ConcurrentModificationExceptions for e.g. matrix builds (see JENKINS-11498):
        synchronized (this) {
            if (scores == null) {
                scores = new LinkedList<Score>();
            }
            scores.addAll(scoresForBuild);
            Collections.sort(scores);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    RuleResult<?> evaluate(AbstractBuild<?, ?> build, Rule rule) {
        if (rule instanceof AggregatableRule<?> && build instanceof MavenModuleSetBuild) {
            AggregatableRule aRule = (AggregatableRule<?>) rule;
            MavenModuleSetBuild mavenModuleSetBuild = (MavenModuleSetBuild) build;

            List<RuleResult> results = new ArrayList<RuleResult>();

            for (Map.Entry<MavenModule, MavenBuild> e : mavenModuleSetBuild.getModuleLastBuilds().entrySet()) {
                MavenBuild moduleBuild = e.getValue();
                if (moduleBuild != null) {
                    AbstractBuild<?, ?> previousBuild = BuildUtil.getPreviousBuiltBuild(moduleBuild);
                    results.add(aRule.evaluate(previousBuild, moduleBuild));
                } else {
                    // module was probably removed from multimodule
                    if (mavenModuleSetBuild.getPreviousBuild() != null) {
                        MavenModuleSetBuild prevBuild = mavenModuleSetBuild.getPreviousBuild();
                        AbstractBuild<?, ?> prevModuleBuild = prevBuild.getModuleLastBuilds().get(e.getKey());
                        if (prevModuleBuild.getResult() == null) {
                            prevModuleBuild = BuildUtil.getPreviousBuiltBuild(prevModuleBuild);
                        }
                        results.add(aRule.evaluate(prevModuleBuild, null));
                    } else {
                        //results.add(aRule.evaluate(null, null));
                        return RuleResult.EMPTY_RESULT;
                    }
                }
            }
            return aRule.aggregate(results);
        } else {
            return rule.evaluate(build.getPreviousBuild(), build);
        }
    }

    /**
     * Record points for the rules in the rule book
     *
     * @param build    build to evaluate
     * @param ruleBook rule book to use for evaluation
     * @param listener
     */
    public void record(AbstractBuild<?, ?> build, RuleBook ruleBook, BuildListener listener) {
        if (scores == null) {
            scores = new LinkedList<Score>();
        }
        for (RuleSet set : ruleBook.getRuleSets()) {
            record(build, set, listener);
        }
    }

    /**
     * Returns a collection of scores. May not be called before the score has
     * been recorded.
     *
     * @return a collection of scores.
     * @throws IllegalStateException thrown if the method is called before the scores has been recorded.
     */
    @Exported
    public Collection<Score> getScores() throws IllegalStateException {
        if (scores == null) {
            throw new IllegalStateException("No scores are available"); //$NON-NLS-1$
        }
        return scores;
    }

    /**
     * Returns the total points for this score card
     *
     * @return the total points for this score card
     * @throws IllegalStateException thrown if the method is called before scores has been calculated
     */
    @Exported
    public double getTotalPoints() throws IllegalStateException {
        if (scores == null) {
            throw new IllegalStateException("No scores are available"); //$NON-NLS-1$
        }
        double value = 0;
        for (Score score : scores) {
            value += score.getValue();
        }
        return value;
    }
}
