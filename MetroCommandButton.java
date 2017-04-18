package MetroComponents;
public class MetroCommandButton extends MetroComponent 
{
    public MetroCommandButton(String title, String description, String icon, String buttonType, String onClickEvent)
    {
        controlContent.append("<button class=\"command-button ").append(buttonType).append("\" onclick=\"").append(onClickEvent).append("\" style=\"width:100%;\">");
        controlContent.append("<span class=\"icon mif-").append(icon).append("\"></span>");
        controlContent.append(title);
        controlContent.append("<small>").append(description).append("</small>");
        controlContent.append("</button>");
    }
}
