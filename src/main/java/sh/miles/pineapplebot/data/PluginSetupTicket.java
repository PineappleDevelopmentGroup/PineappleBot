package sh.miles.pineapplebot.data;

public class PluginSetupTicket extends Ticket {

    private final String setupNeeded;
    private final String setupInfo;
    private final String otherInfo;

    public PluginSetupTicket(long userId, String budget, String setupNeeded, String setupInfo, String otherInfo) {
        super(budget, userId, TicketType.PLUGIN_SETUP);
        this.setupNeeded = setupNeeded;
        this.setupInfo = setupInfo;
        this.otherInfo = otherInfo;
    }

    public String getSetupNeeded() {
        return setupNeeded;
    }

    public String getSetupInfo() {
        return setupInfo;
    }

    public String getOtherInfo() {
        return otherInfo;
    }
}
