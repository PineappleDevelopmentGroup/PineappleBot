package sh.miles.pineapplebot.data;

public class PluginComissionTicket extends Ticket {

    private final String function;
    private final String otherInfo;

    public PluginComissionTicket(long userId, String function, String budget, String otherInfo) {
        super(budget, userId, TicketType.PLUGIN_COMMISSION);
        this.function = function;
        this.otherInfo = otherInfo;
    }

    public String getFunction() {
        return function;
    }

    public String getOtherInfo() {
        return otherInfo;
    }
}
