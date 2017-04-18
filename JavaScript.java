package Utilities;
import java.util.*;
public class JavaScript 
{
    public JavaScript()
    {
        
    }
    public String convertListToJavaScriptArray(List<String> aList)
    {
        if(aList.isEmpty())
            return "[]";
        StringBuilder javaScriptArray = new StringBuilder("[");
        aList.forEach(x -> javaScriptArray.append("'").append(x).append("', "));
        return javaScriptArray.toString().substring(0, javaScriptArray.length() - 2) + "]";
    }
    public String getContent(String url)
    {
        return getContent(url, "bodyPanel");
    }
    public String getContent(String url, String updatePanel)
    {
        return "getContent('" + updatePanel + "', '" + url + "');";
    }
    public String updateContent(String url, String updatePanel, List<String> parameters)
    {
        return "updateContent('" + updatePanel + "', '" + url + "', " + convertListToJavaScriptArray(parameters) + ");";
    }
    public String updateContent(String url, String updatePanel, List<String> parameters, List<String> additionalParameters)
    {
        return "updateContent('" + updatePanel + "', '" + url + "', " + convertListToJavaScriptArray(parameters) + ", " + 
        convertListToJavaScriptArray(additionalParameters) + ");";
    }
    public String updateContent(String url, List<String> parameters)
    {
        return updateContent(url, "bodyPanel", parameters);
    }
    public String updateContent(String url)
    {
        return updateContent(url, "bodyPanel");
    }
    public String updateContent(String url, String updatePanel)
    {
        List<String> parameters = new ArrayList<>(Arrays.asList("token"));
        return updateContent(url, updatePanel, parameters);
    }
    public String toggleTile(String id)
    {
        return "toggleTile('" + id + "');";
    }
    public String selectTileInAccordion(String id, String titleText, String titleIcon, Integer selectedTileNumber)
    {
        return "selectTileInAccordion('" + id + "', '" + titleText + "', '" + titleIcon + "', " + selectedTileNumber + ");";
    }
    public String hide(String id)
    {
        return "hide('" + id + "');";
    }
    public String show(String id)
    {
        return "show('" + id + "');";
    }
}
