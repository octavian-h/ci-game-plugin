package hudson.plugins.cigame;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.cigame.model.ScoreCard;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.*;

/**
 * Score card for a certain build
 * 
 * @author Erik Ramfelt
 */
@ExportedBean(defaultVisibility = 999)
public class ScoreCardAction implements Action {

    private AbstractBuild<?, ?> build;

    private ScoreCard scorecard;

    public ScoreCardAction(ScoreCard scorecard, AbstractBuild<?, ?> b) {
        build = b;
        this.scorecard = scorecard;
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public String getDisplayName() {
        return Messages.Scorecard_Title(); //$NON-NLS-1$
    }

    public String getIconFileName() {
        return GameDescriptor.ACTION_LOGO_MEDIUM;
    }

    public String getUrlName() {
        return "ci-game"; //$NON-NLS-1$
    }

    @Exported
    public ScoreCard getScorecard() {
        return scorecard;
    }

    @Exported
    public Collection<User> getParticipants() {
        return getParticipants(Hudson.getInstance().getDescriptorByType(GameDescriptor.class).getNamesAreCaseSensitive());
    }
    
    Collection<User> getParticipants(boolean usernameIsCasesensitive) {
        Comparator<User> userIdComparator = new CaseInsensitiveUserIdComparator();
        List<User> players = new ArrayList<User>();
        ChangeLogSet<? extends Entry> changeSet = build.getChangeSet();
        for (Entry entry : changeSet) {
            User user = entry.getAuthor();
            UserScoreProperty property = user.getProperty(UserScoreProperty.class);
            if ((property != null) 
                    && property.isParticipatingInGame() 
                    && (usernameIsCasesensitive || Collections.binarySearch(players, user, userIdComparator) < 0)) {
                players.add(user);
            }
        }
        Collections.sort(players, new UserDisplayNameComparator());
        return players;
    }
    
    private static class UserDisplayNameComparator implements Comparator<User> {
        public int compare(User arg0, User arg1) {
            return arg0.getDisplayName().compareToIgnoreCase(arg1.getDisplayName());
        }            
    }
}
