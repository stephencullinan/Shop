package MetroComponents;
public class MetroUpdatePanel extends MetroComponent
{
    public MetroUpdatePanel(String id, MetroComponent aControl)
    {
        controlContent.append("<div id=\"").append(id).append("\">");
        if(aControl != null)
            controlContent.append(aControl.toString());
        controlContent.append("</div>");
    }
}
