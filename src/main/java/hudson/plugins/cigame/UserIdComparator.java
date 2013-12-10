package hudson.plugins.cigame;

import hudson.model.User;

import java.util.Comparator;

/**
 * Comparator that ignores casing on the User's ID
 */
class UserIdComparator implements Comparator<User> {
    private boolean caseSensitive;

    public UserIdComparator() {
        this(false);
    }

    public UserIdComparator(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public int compare(User u0, User u1) {
        if (caseSensitive) {
            return u0.getId().compareTo(u1.getId());
        }
        return u0.getId().compareToIgnoreCase(u1.getId());
    }
}