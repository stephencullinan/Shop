package MetroComponents;
public class MetroProgressBar extends MetroComponent
{
    public MetroProgressBar(Integer initialValue)
    {
        controlContent.append("<div class=\"progress large\" data-value=\"").append(initialValue).append("\"");
        controlContent.append(" data-color=\"ribbed-cyan\" data-role=\"progress\"></div>");
    }
}
