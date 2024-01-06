package sh.miles.pineappleticketbot.data;

public class ServerSetupTicket extends Ticket {

    private final String serverType;
    private final String setupNeeded;
    private final String serverInfo;
    private final String otherInfo;

    public ServerSetupTicket(long userId, String serverType, String setupNeeded, String serverInfo, String otherInfo, String budget) {
        super(budget, userId, TicketType.SERVER_SETUP);
        this.serverType = serverType;
        this.setupNeeded = setupNeeded;
        this.serverInfo = serverInfo;
        this.otherInfo = otherInfo;
    }

    public String getServerType() {
        return serverType;
    }

    public String getSetupNeeded() {
        return setupNeeded;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public String getOtherInfo() {
        return otherInfo;
    }
}
