package MetroComponents;
public class MetroHeading extends MetroComponent
{
    public MetroHeading(String title, String subTitle)
    {
        controlContent.append("<h1 align=\"center\">").append(title).append("<small>").append(subTitle).append("</small></h1>");
    }
}
