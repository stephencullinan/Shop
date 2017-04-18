package MetroComponents;
import java.util.*;
public class MetroTabs extends MetroComponent
{
    public MetroTabs(String id, List<String> tabTitles, List<MetroComponent> tabControls)
    {
        controlContent.append("<div class=\"tabcontrol2\" data-role=\"tabcontrol\" id=\"").append(id).append("\">");
        controlContent.append("<ul class=\"tabs\">");
        for(int counter = 0; counter < tabTitles.size(); counter++)
        {
            controlContent.append("<li>");
            controlContent.append("<a href=\"#").append(id).append("_").append(counter).append("\">").append(tabTitles.get(counter));
            controlContent.append("</a>");
            controlContent.append("</li>");
        }
        controlContent.append("</ul>");
        controlContent.append("<div class=\"frames\">");
        for(int counter = 0; counter < tabControls.size(); counter++)
        {
            controlContent.append("<div class=\"frame\" id=\"").append(id).append("_").append(counter).append("\">");
            if(tabControls.get(counter) != null)
                controlContent.append(tabControls.get(counter).toString());
            controlContent.append("</div>");
        }
        controlContent.append("</div>");
        controlContent.append("</div>");
    }
}
