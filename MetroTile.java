package MetroComponents;
public class MetroTile extends MetroComponent
{
    public MetroTile(String label, String icon, String onClickEvent, String id, Boolean selected)
    {
        String className = "tile-wide bg-cyan fg-white";
        String value = "off";
        if(selected)
        {
            className += " element-selected";
            value = "on";
        }
        controlContent.append("<div class=\"").append(className).append("\" data-role=\"tile\" style=\"width:100%;\" id=\"").append(id); 
        controlContent.append("_Tile\" onclick=\"").append(onClickEvent).append("\">");
        controlContent.append("<div class=\"tile-content iconic\">");
        controlContent.append("<span class=\"icon mif-").append(icon).append("\"></span>");
        controlContent.append("<span class=\"tile-label\">").append(label).append("</span>");
        controlContent.append("</div>");
        controlContent.append("</div>");
        controlContent.append("<input type=\"hidden\" value=\"").append(value).append("\" id=\"").append(id).append("\"></input>");
    }
}
