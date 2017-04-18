package MetroComponents;
public class MetroPanel extends MetroComponent
{
    public MetroPanel(String title, String icon, MetroComponent aComponent)
    {
        controlContent.append("<div class=\"panel collapsible\" data-role=\"panel\">");
        controlContent.append("<div class=\"heading\">");
        controlContent.append("<span class=\"icon mif-").append(icon).append("\"></span>");
        controlContent.append("<span class=\"title\">").append(title).append("</span>");
        controlContent.append("</div>");
        controlContent.append("<div class=\"content bg-white\">");
        if(aComponent != null)
            controlContent.append(aComponent.toString());
        controlContent.append("</div>");
        controlContent.append("</div>");
    }
}
