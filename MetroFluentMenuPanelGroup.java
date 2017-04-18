package MetroComponents;
import java.util.*;
public class MetroFluentMenuPanelGroup extends MetroComponent
{
    public MetroFluentMenuPanelGroup(List<MetroFluentMenuButton> buttons, String caption)
    {
        controlContent.append("<div class=\"tab-panel-group\">");
        controlContent.append("<div class=\"tab-group-content\">");
        for(MetroFluentMenuButton aButton : buttons)
            controlContent.append(aButton.toString());
        controlContent.append("</div>");
        controlContent.append("<div class=\"tab-group-caption\">").append(caption).append("</div>");
        controlContent.append("</div>");
    }
}
