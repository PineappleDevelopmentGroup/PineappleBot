package sh.miles.pineapplebot.data;

public abstract class Ticket {

    private int id;
    private final long userId;
    private long channelId;
    private final TicketType type;
    private final String budget;

    public Ticket(String budget, long userId, TicketType type) {
        this.userId = userId;
        this.type = type;
        this.budget = budget;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public int getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getChannelId() {
        return channelId;
    }

    public TicketType getType() {
        return type;
    }

    public String getBudget() {
        return budget;
    }
}
