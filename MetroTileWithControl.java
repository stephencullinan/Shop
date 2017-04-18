package MetroComponents;
public class MetroTileWithControl extends MetroComponent
{
    public MetroTileWithControl(MetroComponent aControl, String onClickEvent)
    {
        controlContent.append("<div class=\"tile-wide bg-cyan fg-white\" data-role=\"tile\" style=\"width:100%;\""); 
        controlContent.append("onclick=\"").append(onClickEvent).append("\">");
        controlContent.append("<div class=\"tile-content\">");
        if(aControl != null)
            controlContent.append(aControl.toString());
        controlContent.append("</div>");
        controlContent.append("</div>");
    }
}
