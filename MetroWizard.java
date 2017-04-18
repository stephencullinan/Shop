package MetroComponents;
import java.util.*;
public class MetroWizard extends MetroComponent
{
    public MetroWizard(List<MetroComponent> stepControls)
    {
        controlContent.append("<div class=\"wizard2\" data-role=\"wizard2\">");
        stepControls.forEach(x -> 
        {
            controlContent.append("<div class=\"step\">");
            controlContent.append("<div class=\"step-content\">");
            if(x != null)
                controlContent.append(x.toString());
            controlContent.append("</div>");
            controlContent.append("</div>");
        });
    }
}
