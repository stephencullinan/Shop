package MetroComponents;
public class MetroHiddenInput extends MetroComponent
{
    public MetroHiddenInput(String title, String value)
    {
        controlContent.append("<input type=\"hidden\" id=\"").append(title).append("\" value=\"").append(value).append("\"></input>");
    }
}
