package hudson.plugins.cigame.util;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Result;

import java.util.Collections;
import java.util.List;

public class ActionRetriever {

    public static <T extends Action> List<T> getResult(AbstractBuild<?, ?> build,
                                                       Result resultThreshold, Class<T> actionClass) {
        if (build != null && build.getResult() != null
                && build.getResult().isBetterOrEqualTo(resultThreshold)) {
            return build.getActions(actionClass);
        }
        return Collections.emptyList();
    }
}
