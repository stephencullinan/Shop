package MetroComponents;
import java.util.*;
public class MetroFluentMenu extends MetroComponent 
{
    private final String id;
    private int counter;
    public MetroFluentMenu(String id, String title, String onClickEvent, List<String> titles)
    {
        this.id = id;
        this.counter = 0;
        controlContent.append("<div class=\"fluent-menu\" data-role=\"fluentmenu\" id=\"").append(id).append("\">");
        controlContent.append("<ul class=\"tabs-holder\">");
        controlContent.append("<li class=\"special\" onclick=\"").append(onClickEvent).append("\"><a href=\"#\">").append(title).append("</a></li>");
        for(int index = 0; index < titles.size(); index++)
        {
            String classTitle = "";
            if(index == 0)
                classTitle = "active";
            controlContent.append("<li class=\"").append(classTitle).append("\"><a href=\"#").append(id).append("_").append(index).append("\">").append(titles.get(index)).append("</a></li>");
        }
        controlContent.append("</ul>");
        controlContent.append("<div class=\"tabs-content\">");
        controlContent.append("</div>");
        controlContent.append("</div>");
    }
    public void addTab(List<MetroFluentMenuPanelGroup> panelGroups)
    {
        StringBuilder tabContent = new StringBuilder();
        String style = "display:none;";
        if(counter == 0)
            style = "display:block;";
        tabContent.append("<div class=\"tab-panel\" id=\"").append(id).append("_").append(counter++).append("\" style=\"").append(style).append("\">");
        panelGroups.forEach(x -> tabContent.append(x.toString()));
        tabContent.append("</div>");
        controlContent = new StringBuilder(controlContent.substring(0, controlContent.length() - 12) + tabContent.toString() + "</div></div>");
    }
}