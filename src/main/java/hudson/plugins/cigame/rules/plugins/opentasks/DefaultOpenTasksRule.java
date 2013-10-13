package hudson.plugins.cigame.rules.plugins.opentasks;

import hudson.maven.MavenBuild;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.AggregatableRule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionRetriever;
import hudson.plugins.tasks.TasksResultAction;

import java.util.Collection;
import java.util.List;

/**
 * Default rule for the Open tasks plugin.
 */
public class DefaultOpenTasksRule implements AggregatableRule<Integer> {

    private int pointsForAddingAnAnnotation;
    private int pointsForRemovingAnAnnotation;
    private Priority tasksPriority;

    public DefaultOpenTasksRule(Priority tasksPriority,
                                int pointsForAddingAnAnnotation, int pointsForRemovingAnAnnotation) {
        this.tasksPriority = tasksPriority;
        this.pointsForAddingAnAnnotation = pointsForAddingAnAnnotation;
        this.pointsForRemovingAnAnnotation = pointsForRemovingAnAnnotation;
    }

    @Override
    public RuleResult<?> aggregate(Collection<RuleResult<Integer>> results) {
        double score = 0.0;
        int newTasks = 0;
        for (RuleResult<Integer> result : results) {
            if (result != null) {
                score += result.getPoints();
                newTasks += result.getAdditionalData();
            }
        }

        if (newTasks > 0) {
            return new RuleResult<Void>(score,
                    Messages.OpenTasksRule_DefaultRule_NewTasksCount(newTasks, tasksPriority.name()));
        } else if (newTasks < 0) {
            return new RuleResult<Integer>(score,
                    Messages.OpenTasksRule_DefaultRule_FixedTasksCount(newTasks * -1, tasksPriority.name()));
        }
        return RuleResult.EMPTY_INT_RESULT;
    }

    @Override
    public RuleResult<Integer> evaluate(AbstractBuild<?, ?> previousBuild,
                                        AbstractBuild<?, ?> build) {
        if (build != null && build.getResult() != null && build.getResult().isWorseOrEqualTo(Result.FAILURE)) {
            return RuleResult.EMPTY_INT_RESULT;
        }

        if (previousBuild == null) {
            if (!(build instanceof MavenBuild)) {
                // backward compatibility
                return RuleResult.EMPTY_INT_RESULT;
            }
        } else if (previousBuild.getResult().isWorseOrEqualTo(Result.FAILURE)) {
            return RuleResult.EMPTY_INT_RESULT;
        }

        List<TasksResultAction> currentActions = ActionRetriever.getResult(build, Result.UNSTABLE, TasksResultAction.class);
        if (!hasNoErrors(currentActions)) {
            return RuleResult.EMPTY_INT_RESULT;
        }
        int currentAnnotations = getNumberOfAnnotations(currentActions);

        List<TasksResultAction> previousActions = ActionRetriever.getResult(previousBuild, Result.UNSTABLE, TasksResultAction.class);
        if (!hasNoErrors(previousActions)) {
            return RuleResult.EMPTY_INT_RESULT;
        }
        int previousAnnotations = getNumberOfAnnotations(previousActions);

        int numberOfNewAnnotations = currentAnnotations - previousAnnotations;

        if (numberOfNewAnnotations > 0) {
            return new RuleResult<Integer>(numberOfNewAnnotations * pointsForAddingAnAnnotation,
                    Messages.OpenTasksRule_DefaultRule_NewTasksCount(numberOfNewAnnotations, tasksPriority.name()),
                    numberOfNewAnnotations);
        }
        if (numberOfNewAnnotations < 0) {
            return new RuleResult<Integer>((numberOfNewAnnotations * -1) * pointsForRemovingAnAnnotation,
                    Messages.OpenTasksRule_DefaultRule_FixedTasksCount(numberOfNewAnnotations * -1, tasksPriority.name()),
                    numberOfNewAnnotations);
        }

        return RuleResult.EMPTY_INT_RESULT;
    }

    private boolean hasNoErrors(List<TasksResultAction> actions) {
        for (TasksResultAction action : actions) {
            if (action.getResult().hasError()) {
                return false;
            }
        }
        return true;
    }

    private int getNumberOfAnnotations(List<TasksResultAction> actions) {
        int numberOfAnnotations = 0;
        for (TasksResultAction action : actions) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations(tasksPriority);
        }
        return numberOfAnnotations;
    }

    @Override
    public String getName() {
        return Messages.OpenTasksRule_DefaultRule_Name(tasksPriority.name()); //$NON-NLS-1$
    }
}
