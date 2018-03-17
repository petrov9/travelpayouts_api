package api.to.excel;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static api.to.excel.City.*;

public class Start
{
    private static final Logger log = Logger.getLogger(Start.class);

    private static String TOKEN = "no token";
    private static DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static Calendar calendar = Calendar.getInstance();

    private static final List<ParsedItem> parsedItems = new ArrayList<>();

    public static void main(String[] args)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("conf/token.txt")));
            TOKEN = br.readLine().trim();

            Map<City, City> flows = new HashMap<>();
            flows.put(GDANSK, VILNUIS);
            flows.put(OSLO, VILNUIS);
            flows.put(GETEBORG, VILNUIS);
            flows.put(STOCKHOLM, VILNUIS);
            flows.put(LONDON, VILNUIS);

            calendar.add(Calendar.MONTH, -1);
            for (int i = 0; i < 3; i++)
            {
                calendar.add(Calendar.MONTH, 1);

                for (Map.Entry<City, City> flow : flows.entrySet())
                {
                    parse(flow.getKey(), flow.getValue());
                    parse(flow.getValue(), flow.getKey());
                }
            }

            writeToExcel();
        }
        catch (Exception e)
        {
            log.error("Can't parse", e);
        }
    }

    private static void parse(City cityFrom, City cityTo) throws Exception
    {
        String from = cityFrom.getCode();
        String to = cityTo.getCode();

        String url = String.format(getQueryPattern(), getStrMonth(), from, to);
        log.info("Api url: " + url);
        JSONObject jsonObject = readJsonFromUrl(url);
        if (!jsonObject.getBoolean("success"))
            return;

        JSONObject flights = jsonObject.getJSONObject("data");
        JSONArray keys = flights.names();
        if (keys == null)
            return;
        ParsedItem parsedItem;
        for (int i = 0; i < keys.length(); i++)
        {
            String key = keys.getString(i);
            JSONObject flight = flights.getJSONObject(key);
            int price = flight.getInt("price");

            String[] fromDateAndTime = convertDateFormat(flight.getString("departure_at")).split("\\s");
            String strFromDate = fromDateAndTime[0];
            String strFromTime = fromDateAndTime[1];

            String[] toDateAndTime = convertDateFormat(flight.getString("return_at")).split("\\s");
            String strToDate = toDateAndTime[0];
            String strToTime = toDateAndTime[1];

            parsedItem = new ParsedItem();
            parsedItem.setFrom(cityFrom.getName());
            parsedItem.setTo(cityTo.getName());
            parsedItem.setFromDate(strFromDate);
            parsedItem.setFromTime(strFromTime);
            parsedItem.setToDate(strToDate);
            parsedItem.setToTime(strToTime);
            parsedItem.setPrice(price);
            parsedItems.add(parsedItem);
        }
    }

    private static String convertDateFormat(String inputdate) throws Exception
    {
        String outputDate = null;
        Date date = inputFormat.parse(inputdate);
        outputDate = outputFormat.format(date);

        return outputDate;
    }

    private static String readAll(Reader rd) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1)
        {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException
    {
        try (InputStream is = new URL(url).openStream();
             BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))
        )
        {
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        }
    }

    private static void writeToExcel() throws Exception
    {
        String fileName = "Flights";
        Workbook workBook = new XSSFWorkbook();

        CellStyle cellStyle = workBook.createCellStyle();
        Font font = workBook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);

        Sheet sheet = workBook.createSheet(fileName);
        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellValue("From");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("To");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Departure date");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Departure time");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Return date");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Return time");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Price");
        cell.setCellStyle(cellStyle);

        int count = 1;
        for (ParsedItem parsedItem : parsedItems)
        {
            row = sheet.createRow(count++);

            row.createCell(0).setCellValue(parsedItem.getFrom());
            row.createCell(1).setCellValue(parsedItem.getTo());
            row.createCell(2).setCellValue(parsedItem.getFromDate());
            row.createCell(3).setCellValue(parsedItem.getFromTime());
            row.createCell(4).setCellValue(parsedItem.getToDate());
            row.createCell(5).setCellValue(parsedItem.getToTime());
            row.createCell(6).setCellValue(parsedItem.getPrice());
        }

        workBook.write(new FileOutputStream(fileName + ".xlsx"));
        workBook.close();
    }

    private static String getQueryPattern()
    {
        return "http://api.travelpayouts.com/v1/prices/calendar?" +
                "depart_date=%s" +
                "&origin=%s" +
                "&destination=%s" +
                "&currency=rub" +
                "&calendar_type=departure_date" +
                "&token=" + TOKEN;
    }

    private static String getStrMonth()
    {
        DateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(calendar.getTime());
    }
}
