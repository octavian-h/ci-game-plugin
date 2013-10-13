package hudson.plugins.cigame;

import hudson.model.Action;

/**
 * Score card for a job.
 * 
 * @author Erik Ramfelt
 */
public class ScoreBoardAction implements Action {

    public String getDisplayName() {
        return Messages.Scorecard_Title();
    }

    public String getIconFileName() {
        return "Scorecard.gif"; //$NON-NLS-1$
    }

    public String getUrlName() {
        return "ci-game"; //$NON-NLS-1$
    }

}
