package MetroComponents;
public class MetroTextBlock extends MetroComponent 
{
    public MetroTextBlock(String title, String textualContent, Boolean includeHeading)
    {
        controlContent.append("<div class=\"example\" data-text=\"").append(title).append("\">");
        if(includeHeading)
            controlContent.append("<h1 align=\"center\">").append(title).append("</h1>");
        controlContent.append(textualContent);
        controlContent.append("</div>");
    }
}
