package MetroComponents;
public class MetroListView extends MetroComponent
{
    private final String id;
    public MetroListView(String id, String title)
    {
        this.id = id;
        controlContent.append("<input type=\"hidden\" id=\"").append(id).append("\"></input>");
        controlContent.append("<div class=\"listview-outlook\" data-role=\"listview\">");
        controlContent.append("<div class=\"list-group\">");
        controlContent.append("<span class=\"list-group-toggle\">").append(title).append("</span>");
        controlContent.append("<div class=\"list-group-content\">");
        controlContent.append("</div>");
        controlContent.append("</div>");
        controlContent.append("</div>");
    }
    public void addListItem(String onClickEvent, String title, String subTitle, String description, String value)
    {
        StringBuilder listItem = new StringBuilder();
        listItem.append("<a class=\"list marked\" onclick=\"selectItemInListView('").append(id).append("', '").append(value).append("');");
        listItem.append(onClickEvent).append("\">");
        listItem.append("<div class=\"list-content\">");
        listItem.append("<span class=\"list-title\">").append(title).append("</span>");
        listItem.append("<span class=\"list-subtitle\">").append(subTitle).append("</span>");
        listItem.append("<span class=\"list-remark\">").append(description).append("</span>");
        listItem.append("</div>");
        listItem.append("</a>");
        controlContent = new StringBuilder(controlContent.substring(0, controlContent.length() - 18) + listItem.toString() + 
        "</div></div></div>");
    }
}