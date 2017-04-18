package MetroComponents;
public class MetroFluentMenuButton extends MetroComponent 
{
    public MetroFluentMenuButton(String text, String icon, String onClickEvent)
    {
        controlContent.append("<button class=\"fluent-big-button\" onclick=\"").append(onClickEvent).append("\">");
        controlContent.append("<span class=\"icon mif-").append(icon).append("\"></span>");
        controlContent.append(text);
        controlContent.append("</button>");
    }
}
