package MetroComponents;
public class MetroStepper extends MetroComponent
{
    public MetroStepper(String id, Integer totalNumberOfSteps, Integer currentStep)
    {
        controlContent.append("<div class=\"stepper\" data-role=\"stepper\" data-type=\"diamond\" data-clickable=\"true\" id=\"").append(id);
        controlContent.append("\" data-steps=\"").append(totalNumberOfSteps).append("\" data-start=\"").append(currentStep).append("\">");
        controlContent.append("</div>");
    }
}
