package hudson.plugins.cigame;

import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for the {@link UserScoreProperty}.
 *
 * @author Erik Ramfelt
 */
@Extension
public class UserScorePropertyDescriptor extends UserPropertyDescriptor {

    public UserScorePropertyDescriptor() {
        super(UserScoreProperty.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.User_Property_Title();
    }

    @Override
    public UserScoreProperty newInstance(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
        if (formData.has("score")) { //$NON-NLS-1$
            return req.bindJSON(UserScoreProperty.class, formData);
        }
        return new UserScoreProperty();
    }

    @Override
    public UserProperty newInstance(User arg0) {
        return null;
    }
}
