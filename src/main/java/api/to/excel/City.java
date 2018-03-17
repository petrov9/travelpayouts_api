package api.to.excel;

public enum City
{
    VILNUIS("VNO", "Вильнюс"),
    GDANSK("GDN", "Гданьск"),
    OSLO("OSL", "Осло"),
    GETEBORG("GOT", "Гетеборг"),
    STOCKHOLM("STO", "Стокгольм"),
    LONDON("LON", "Лондон");

    private String code;
    private String name;

    City(String code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public String getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }
}
