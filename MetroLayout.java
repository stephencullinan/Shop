package MetroComponents;
import java.util.*;
public class MetroLayout extends MetroComponent
{
    private final Integer numberOfEmptyRows;
    public MetroLayout(Integer numberOfEmptyRows)
    {
        this.numberOfEmptyRows = numberOfEmptyRows;
        controlContent.append("<div class=\"grid condensed\">");
        controlContent.append("</div>");
    }
    public void addRow(MetroComponent aComponent, List<Integer> positions)
    {
        addRow(new ArrayList<>(Collections.singletonList(aComponent)), positions);
    }
    public void addRow(MetroComponent aComponent)
    {
        addRow(aComponent, new ArrayList<>(Arrays.asList(0, 12, 0)));
    }
    public void addRow(List<MetroComponent> components, List<Integer> positions)
    {
        StringBuilder newRow = new StringBuilder();
        newRow.append("<div class=\"row cells12\">");
        for(int counter = 0, index = 0; counter < components.size() && (index + 2) < positions.size(); counter++, index = index + 3)
        {
            newRow.append("<div class=\"cell colspan").append(positions.get(index)).append("\"></div>");
            newRow.append("<div class=\"cell colspan").append(positions.get(index + 1)).append("\">");
            newRow.append(components.get(counter).toString());
            newRow.append("</div>");
            newRow.append("<div class=\"cell colspan").append(positions.get(index + 2)).append("\"></div>");
        }
        newRow.append("</div>");
        controlContent = new StringBuilder(controlContent.substring(0, controlContent.length() - 6) + newRow.toString() + "</div>");
        addEmptyRows(numberOfEmptyRows);
    }
    public void addMultipleRows(List<MetroComponent> components, List<Integer> positions)
    {
        if(positions.size() > 2)
        {
            int numberOfControlsPerRow = 12 / (positions.get(0) + positions.get(1) + positions.get(2));
            for(int counter = 0; counter < components.size(); counter = counter + numberOfControlsPerRow)
            {
                List<MetroComponent> componentsForCurrentRow = new ArrayList<>();
                List<Integer> positionsForCurrentRow = new ArrayList<>();
                for(int index = counter; index < (counter + numberOfControlsPerRow) && index < components.size(); index++)
                {
                    componentsForCurrentRow.add(components.get(index));
                    positionsForCurrentRow.addAll(new ArrayList<>(Arrays.asList(positions.get(0), positions.get(1), positions.get(2))));
                }
                addRow(componentsForCurrentRow, positionsForCurrentRow);
            }
        }
    }
    public void addEmptyRows(Integer numberOfEmptyRows)
    {
        StringBuilder emptyRows = new StringBuilder();
        for(int counter = 0; counter < numberOfEmptyRows; counter++)
        {
            emptyRows.append("<div class=\"row cells12\">");
            emptyRows.append("<div class=\"cell colspan12\"></div>");
            emptyRows.append("</div>");
        }
        controlContent = new StringBuilder(controlContent.substring(0, controlContent.length() - 6) + emptyRows.toString() + "</div>");
    }
}
