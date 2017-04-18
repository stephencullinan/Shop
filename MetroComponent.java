package MetroComponents;
import java.io.*;
import java.util.*;
import javax.faces.component.*;
import javax.faces.context.*;
@FacesComponent(value = "Components.MetroComponent", createTag = true)
public class MetroComponent extends UIComponentBase
{
    protected StringBuilder controlContent;
    public MetroComponent()
    {
        controlContent = new StringBuilder();
    }
    @Override
    public String getFamily()
    {
        return "StephenCullinan.MetroComponent.Base";
    }
    @Override
    public void encodeBegin(FacesContext context)
    {
        try
        {
            ResponseWriter writer = context.getResponseWriter();
            writer.write(controlContent.toString());
        }
        catch(IOException error)
        {
           System.out.println(error); 
        }
    }
    @Override
    public String toString()
    {
        return controlContent.toString();
    }
}