package MetroComponents;
import java.util.*;
import Utilities.*;
public class MetroSwitcher extends MetroComponent
{
    public MetroSwitcher(String id, boolean state, List<String> items)
    {
        controlContent.append("<label class=\"switch\" onclick=\"toggleSwitch('").append(id).append("', ");
        controlContent.append(new JavaScript().convertListToJavaScriptArray(items)).append(");\">");
        controlContent.append("<input type=\"checkbox\" id=\"").append(id).append("\"");
        if(state)
            controlContent.append("value=\"on\" checked=\"\">");
        else
            controlContent.append("value=\"off\">");
        
        controlContent.append("<span class=\"check\"></span>");
        controlContent.append("</label>");
        controlContent.append("<input type=\"hidden\" id=\"").append(id).append("_HiddenInput\" value=\"true\"></input>");
    }
}
