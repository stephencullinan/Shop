package MetroComponents;
public class MetroTextField extends MetroComponent
{
    public MetroTextField(String id, String placeHolderText, String icon, String textFieldType, String value)
    {
        controlContent.append("<div class=\"input-control modern ").append(textFieldType);
        controlContent.append(" iconic full-size\" data-role=\"input\">");
        controlContent.append("<input type=\"").append(textFieldType).append("\" value=\"").append(value);
        controlContent.append("\" id=\"").append(id).append("\">");
        controlContent.append("<span class=\"label\">").append(placeHolderText).append("</span>");
        controlContent.append("<span class=\"informer\">").append(placeHolderText).append("</span>");
        controlContent.append("<span class=\"placeholder\">").append(placeHolderText).append("</span>");
        controlContent.append("<span class=\"icon mif-").append(icon).append("\"></span>");
        controlContent.append("</div>");
    }
}
