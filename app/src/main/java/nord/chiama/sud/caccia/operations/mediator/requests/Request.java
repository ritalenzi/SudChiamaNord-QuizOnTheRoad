package nord.chiama.sud.caccia.operations.mediator.requests;

public class Request
{
    private String action;
    private String sessionKey;
    private Details details;

    /**
     *
     * @return
     * The action
     */
    public String getAction() {
        return action;
    }

    /**
     *
     * @param action
     * The action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     *
     * @return
     * The sessionKey
     */
    public String getSessionKey() {
        return sessionKey;
    }

    /**
     *
     * @param sessionKey
     * The sessionKey
     */
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    /**
     *
     * @return
     * The details
     */
    public Details getDetails() {
        return details;
    }

    /**
     *
     * @param details
     * The details
     */
    public void setDetails(Details details) {
        this.details = details;
    }
}
