package MetroComponents;
import java.util.*;
public class MetroDataTable extends MetroComponent
{
    public MetroDataTable(String id, List<String> tableTitles, List<List<String>> tableCells, List<String> tableRowClickEvents)
    {
        controlContent.append("<table id=\"").append(id).append("\" class=\"dataTable striped border bordered\" data-role=\"datatable\" ");
        controlContent.append("data-searching=\"true\">");
        controlContent.append("<thead>");
        controlContent.append("<tr>");
        for(String aTableTitle : tableTitles)
            controlContent.append("<th>").append(aTableTitle).append("</th>");
        controlContent.append("</tr>");
        controlContent.append("</thead>");
        controlContent.append("<tbody>");
        for(int counter = 0; counter < tableCells.size(); counter++)
        {
            String onClickEvent = "";
            if(tableRowClickEvents.size() > counter)
                onClickEvent = tableRowClickEvents.get(counter);
            controlContent.append("<tr onclick=\"").append(onClickEvent).append("\">");
            for(String aTableCell : tableCells.get(counter))
                controlContent.append("<td>").append(aTableCell).append("</td>");
            controlContent.append("</tr>");
        }
        controlContent.append("</tbody>");
        controlContent.append("</table>");
    }
}
