package de.muenchen.ehrenamtjustiz.eai.personeninfo.callbacks;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import org.apache.wss4j.common.ext.WSPasswordCallback;

public class PasswordCallback implements CallbackHandler {

    /**
     * Required username for calling the web service with the given password.
     */
    protected String userName;

    /**
     * Password for calling the web service with the given user.
     */
    protected String userPassword;

    @Override
    public void handle(final Callback[] callbacks) {
        final WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        if (pc.getIdentifier().equals(userName)) {
            pc.setPassword(userPassword);
        }
    }

    /**
     * @param userName The username
     */
    public void setUserName(final String userName) {
        this.userName = userName;
    }

    /**
     * @param userPassword The password
     */
    public void setUserPassword(final String userPassword) {
        this.userPassword = userPassword;
    }
}
