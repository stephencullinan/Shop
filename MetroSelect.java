package MetroComponents;
import java.util.*;
public class MetroSelect extends MetroComponent
{
    public MetroSelect(HashMap<String, String> selectedParameters, String id, String placeHolder)
    {
        controlContent.append("<div class=\"input-control full-size\" data-role=\"select\" id=\"").append(id).append("\" ");
        controlContent.append("data-placeholder=\"").append(placeHolder).append("\">");
        controlContent.append("<select class=\"full-size\">");
        selectedParameters.entrySet().forEach((aSelectedParameter) -> 
        {
            controlContent.append("<option value=\"").append(aSelectedParameter.getKey()).append("\">");
            controlContent.append(aSelectedParameter.getValue()).append("</option>");
        });
        controlContent.append("</select>");
        controlContent.append("</div>");
    }
}
