package MetroComponents;
public class MetroPopover extends MetroComponent
{
    public MetroPopover(String text, String markerPosition)
    {
        controlContent.append("<div class=\"popover marker-on-").append(markerPosition).append(" bg-cyan\">");
        controlContent.append("<div class=\"fg-white\" align=\"center\">");
        controlContent.append(text);
        controlContent.append("</div>");
        controlContent.append("</div>");
    }
}
