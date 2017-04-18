package MetroComponents;
public class MetroSideBar extends MetroComponent
{
    public MetroSideBar(String onClickEvent)
    {
        controlContent.append("<ul class=\"sidebar navy\" onclick=\"").append(onClickEvent).append("\"></ul>");
    }
    public void addItem(String title, String subTitle, String icon)
    {
        StringBuilder itemContent = new StringBuilder();
        itemContent.append("<li><a href=\"#\">");
        itemContent.append("<span class=\"mif-").append(icon).append(" icon\"></span>");
        itemContent.append("<span class=\"title\">").append(title).append("</span>");
        itemContent.append("<span class=\"counter\">").append(subTitle).append("</span>");
        itemContent.append("</a></li>");
        controlContent = new StringBuilder(controlContent.substring(0, controlContent.length() - 5) + itemContent.toString() + "</ul>");
    }
}
