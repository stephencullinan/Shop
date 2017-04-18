package MetroComponents;
public class MetroSlider extends MetroComponent
{
    public MetroSlider(String id, int maximumValue, int minimumValue, Boolean complete)
    {
        controlContent.append("<div class=\"slider\" data-role=\"slider\" data-max-value=\"").append(maximumValue).append("\" ");
        controlContent.append("data-min-value=\"").append(minimumValue).append("\"");
        if(complete)
            controlContent.append("data-position=\"").append(maximumValue).append("\"");
        else
            controlContent.append("data-position=\"").append(minimumValue).append("\"");
        controlContent.append(">");
        if(complete)
            controlContent.append(new MetroHiddenInput(id, "on").toString());
        else
            controlContent.append(new MetroHiddenInput(id, "off").toString());
    }
}
