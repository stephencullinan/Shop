package MetroComponents;
public class MetroAppBar extends MetroComponent
{
    public MetroAppBar()
    {
        controlContent.append("<div class=\"app-bar\" data-role=\"appbar\">");
        controlContent.append("<ul class=\"app-bar-menu\">");
        controlContent.append("</ul>");
        controlContent.append("</div>");
    }
    public void addMenuItem(String title, String icon, String onClickEvent)
    {
        StringBuilder aMenuItem = new StringBuilder();
        aMenuItem.append("<li onclick=\"").append(onClickEvent).append("\">");
        aMenuItem.append("<a><span class=\"mif-").append(icon).append(" mif-2x icon\"></span>").append(title).append("</a>");
        aMenuItem.append("</li>");
        controlContent = new StringBuilder(controlContent.substring(0, controlContent.length() - 11) + aMenuItem.toString() + "</ul></div>");
    }
}