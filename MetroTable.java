package MetroComponents;
import java.util.*;
public class MetroTable extends MetroComponent
{
    public MetroTable(String id, List<String> tableHeadings, List<List<String>> tableContents)
    {
        controlContent.append("<table class=\"table striped hovered cell-hovered border bordered\" id=\"").append(id).append("\">");
        controlContent.append("<thead>");
        controlContent.append("<tr>");
        tableHeadings.forEach(x -> controlContent.append("<th>").append(x).append("</th>"));
        controlContent.append("</tr>");
        controlContent.append("</thead>");
        controlContent.append("<tbody>");
        for(List<String> aTableRow : tableContents)
        {
            controlContent.append("<tr>");
            aTableRow.forEach(x -> controlContent.append("<td>").append(x).append("</td>"));
            controlContent.append("</tr>");
        }
        controlContent.append("</tbody>");
        controlContent.append("</table>");
    }
}
