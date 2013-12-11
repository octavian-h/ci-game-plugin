package hudson.plugins.cigame;

import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.RootAction;
import hudson.model.User;
import hudson.plugins.cigame.model.ScoreLevel;
import hudson.security.ACL;
import hudson.security.AccessControlled;
import hudson.security.Permission;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.*;

/**
 * Leader board for users participating in the game.
 *
 * @author Erik Ramfelt
 */
@ExportedBean(defaultVisibility = 999)
@Extension
public class LeaderBoardAction implements RootAction, AccessControlled {

    public String getDisplayName() {
        return Messages.Leaderboard_Title();
    }

    public String getIconFileName() {
        return GameDescriptor.ACTION_LOGO_MEDIUM;
    }

    public String getUrlName() {
        return "/ci-game"; //$NON-NLS-1$
    }

    /**
     * Returns the user that are participants in the ci game
     *
     * @return list containing users.
     */
    @Exported
    public List<UserScore> getUserScores() {
        return getUserScores(User.getAll(), Hudson.getInstance().getDescriptorByType(GameDescriptor.class).getNamesAreCaseSensitive());
    }

    List<UserScore> getUserScores(Collection<User> users, boolean usernameIsCaseSensitive) {
        List<UserScore> list = new ArrayList<UserScore>();

        Collection<User> players;
        if (usernameIsCaseSensitive) {
            players = users;
        } else {
            List<User> playerList = new ArrayList<User>();
            UserIdComparator userIdComparator = new UserIdComparator();
            for (User user : users) {
                if (Collections.binarySearch(playerList, user, userIdComparator) < 0) {
                    playerList.add(user);
                }
            }
            players = playerList;
        }

        for (User user : players) {
            UserScoreProperty property = user.getProperty(UserScoreProperty.class);
            if ((property != null) && property.isParticipatingInGame()) {
                list.add(new UserScore(user, property.getScore(), user.getDescription()));
            }
        }

        Collections.sort(list, new Comparator<UserScore>() {
            public int compare(UserScore o1, UserScore o2) {
                if (o1.score < o2.score)
                    return 1;
                if (o1.score > o2.score)
                    return -1;
                return 0;
            }
        });

        return list;
    }

    public void doResetScores(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (Hudson.getInstance().getACL().hasPermission(Hudson.ADMINISTER)) {
            doResetScores(User.getAll());
        }
        rsp.sendRedirect2(req.getContextPath());
    }

    void doResetScores(Collection<User> users) throws IOException {
        for (User user : users) {
            UserScoreProperty property = user.getProperty(UserScoreProperty.class);
            if (property != null) {
                property.setScore(0);
                user.save();
            }
        }
    }

    public ACL getACL() {
        return Hudson.getInstance().getACL();
    }

    public void checkPermission(Permission p) {
        getACL().checkPermission(p);
    }

    public boolean hasPermission(Permission p) {
        return getACL().hasPermission(p);
    }

    @Exported
    public Map<ScoreLevel, List<UserScore>> getUserGroups() {
        Map<ScoreLevel, List<UserScore>> result = new TreeMap<ScoreLevel, List<UserScore>>(new Comparator<ScoreLevel>() {
            public int compare(ScoreLevel o1, ScoreLevel o2) {
                return o2.getLevel() - o1.getLevel(); //descending
            }
        });

        GameDescriptor gameDescriptor = Hudson.getInstance().getDescriptorByType(GameDescriptor.class);
        List<UserScore> userScores = getUserScores(User.getAll(), gameDescriptor.getNamesAreCaseSensitive());
        Map<Integer, ScoreLevel> levels = gameDescriptor.getScoreLevels();
        for (UserScore userScore : userScores) {
            if (userScore.getScore() < 10) {
                addUserScoreToLevel(userScore, levels.get(1), result);
            } else if (userScore.getScore() < 20) {
                addUserScoreToLevel(userScore, levels.get(2), result);
            } else if (userScore.getScore() < 30) {
                addUserScoreToLevel(userScore, levels.get(3), result);
            } else {
                addUserScoreToLevel(userScore, levels.get(4), result);
            }
        }

        return result;
    }

    private void addUserScoreToLevel(UserScore userScore, ScoreLevel level, Map<ScoreLevel, List<UserScore>> userScores) {
        List<UserScore> scores = userScores.get(level);
        if (scores == null) {
            scores = new ArrayList<UserScore>();
            userScores.put(level, scores);
        }
        scores.add(userScore);
    }

    @ExportedBean(defaultVisibility = 999)
    public class UserScore {
        private User user;
        private double score;
        private String description;

        public UserScore(User user, double score, String description) {
            super();
            this.user = user;
            this.score = score;
            this.description = description;
        }

        @Exported
        public User getUser() {
            return user;
        }

        @Exported
        public double getScore() {
            return score;
        }

        @Exported
        public String getDescription() {
            return description;
        }
    }
}
