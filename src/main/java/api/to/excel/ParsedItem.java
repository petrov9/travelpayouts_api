package api.to.excel;

public class ParsedItem
{
    private String from;
    private String to;
    private String fromDate;
    private String fromTime;
    private String toDate;
    private String toTime;
    private int price;

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public String getFromDate()
    {
        return fromDate;
    }

    public void setFromDate(String fromDate)
    {
        this.fromDate = fromDate;
    }

    public String getFromTime()
    {
        return fromTime;
    }

    public void setFromTime(String fromTime)
    {
        this.fromTime = fromTime;
    }

    public String getToDate()
    {
        return toDate;
    }

    public void setToDate(String toDate)
    {
        this.toDate = toDate;
    }

    public String getToTime()
    {
        return toTime;
    }

    public void setToTime(String toTime)
    {
        this.toTime = toTime;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    @Override
    public String toString()
    {
        return "ParsedItem{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", fromTime='" + fromTime + '\'' +
                ", toDate='" + toDate + '\'' +
                ", toTime='" + toTime + '\'' +
                ", price=" + price +
                '}';
    }

}
