package hudson.plugins.cigame;

import hudson.model.User;

import java.util.Comparator;

/**
 * Comparator that ignores casing on the User's ID
 */
class UserNameComparator implements Comparator<User> {
    public int compare(User u0, User u1) {
        return u0.getDisplayName().compareToIgnoreCase(u1.getDisplayName());
    }
}