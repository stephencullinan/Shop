package Utilities;
import Database.*;
import java.text.*;
import java.util.*;
public class Log 
{
    public Log()
    {
        
    }
    public void addLog(String userID, String description)
    {
        Database database = new Database();
        Double maxValue = database.getMaxValueOfColumn("log", "code") + 1;
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        database.insertTableRow("log", new ArrayList<>(Arrays.asList(maxValue.intValue() + "", userID, description, formattedDate)));
    }
}
