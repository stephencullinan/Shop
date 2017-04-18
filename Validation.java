package Utilities;
public class Validation 
{
    public Validation()
    {
        ;
    }
    public Integer validatePositiveInteger(String input)
    {
        Integer anInteger = 0;
        try
        {
            anInteger = Integer.parseInt(input);
        }
        catch(NumberFormatException error)
        {
            System.out.println(error);
            return -1;
        }
        if(anInteger < 0)
            return -1;
        return anInteger;
    }
}
