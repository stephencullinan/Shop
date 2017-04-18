package MetroComponents;
public class MetroAccordion extends MetroComponent
{
    public MetroAccordion(String id)
    {
        controlContent.append("<div class=\"accordion large-heading\" data-role=\"accordion\" id=\"").append(id).append("\"></div>");
    }
    public void addFrame(String headingText, String icon, MetroComponent component, Boolean collapsed)
    {
        StringBuilder frameContent = new StringBuilder();
        frameContent.append("<div class=\"frame");
        if(collapsed)
            frameContent.append(" active");
        frameContent.append("\">");
        frameContent.append("<div class=\"heading\">").append(headingText).append("<span class=\"mif-").append(icon).append(" icon\"></span></div>");
        frameContent.append("<div class=\"content\">");
        if(component != null)
            frameContent.append(component.toString());
        frameContent.append("</div>");
        frameContent.append("</div>");
        controlContent = new StringBuilder(controlContent.substring(0, controlContent.length() - 6) + frameContent.toString() + "</div>");
    }
}