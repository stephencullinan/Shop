package Utilities;
import MetroComponents.*;
import java.util.*;
public class JSON 
{
    public JSON()
    {
        ;
    }
    public String convertComponentToJSON(MetroComponent component)
    {
        if(component != null)
            return "{\"html\" : \"" + component.toString().replace("\"", "\\\"") + "\"}";
        else
            return "{}";
    }
    public String convertComponentAndParametersToJSON(MetroComponent component, HashMap<String, String> selectedParameters)
    {
        StringBuilder formattedJSON = new StringBuilder("{");
        selectedParameters.forEach((x, y) -> formattedJSON.append("\"").append(x).append("\" : \"").append(y).append("\", "));
        if(component != null)
            return formattedJSON.toString() + convertComponentToJSON(component).substring(1);
        else
            return formattedJSON.toString().substring(0, formattedJSON.length() - 2) + "}";
    }
    public String convertComponentAndSuccessNotificationToJSON(MetroComponent component, String title, String description)
    {
        return convertComponentAndNotificationToJSON(component, title, description, "success");
    }
    public String convertComponentAndErrorNotificationToJSON(MetroComponent component, String title, String description)
    {
        return convertComponentAndNotificationToJSON(component, title, description, "error");
    }
    public String convertSuccessNotificationToJSON(String title, String description)
    {
        return convertComponentAndNotificationToJSON(null, title, description, "success");
    }
    public String convertErrorNotificationToJSON(String title, String description)
    {
        return convertComponentAndNotificationToJSON(null, title, description, "error");
    }
    public HashMap<String, String> convertJSONToHashMap(String json)
    {
        HashMap<String, String> formattedParameters = new HashMap();
        json = json.substring(1, json.length() - 2);
        String[] parameters = json.split(",");
        for(int counter = 0; counter < parameters.length; counter++)
        {
            String[] parameterPair = parameters[counter].split(":");
            if(parameterPair.length > 1)
                formattedParameters.put(parameterPair[0].trim().replace("\"", ""), parameterPair[1].trim().replace("\"", ""));
        }
        return formattedParameters;
    }
    private String convertComponentAndNotificationToJSON(MetroComponent component, String title, String description, String notificationType)
    {
        HashMap<String, String> selectedParameters = new HashMap();
        selectedParameters.put("notification", notificationType);
        selectedParameters.put("title", title);
        selectedParameters.put("description", description);
        return convertComponentAndParametersToJSON(component, selectedParameters);
    }
}
