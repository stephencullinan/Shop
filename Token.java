package Utilities;
import Database.*;
import java.text.*;
import java.util.*;
public class Token 
{
    private final Database database;
    private String token;
    public Token()
    {
        database = new Database();
        token = "";
    }
    public String generateToken(String userID)
    {
        token = UUID.randomUUID().toString();
        HashMap<String, String> selectedParameters = new HashMap();
        selectedParameters.put("users", userID);
        List<List<String>> userTokenDetails = database.getTableRows("tokens", selectedParameters, new ArrayList<>(), new ArrayList<>());
        Date currentDate = new Date();
        currentDate.setTime(currentDate.getTime() + 1000000000);
        String formattedCurrentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate);
        if(userTokenDetails.size() > 0)
        {
            HashMap<String, String> updatedParameters = new HashMap<>();
            updatedParameters.put("token", token);
            updatedParameters.put("expirydate", formattedCurrentDate);
            database.updateTableRow("tokens", updatedParameters, selectedParameters);
        }
        else
            database.insertTableRow("tokens", new ArrayList<>(Arrays.asList(userID, token, formattedCurrentDate)));
        return token;
    }
    public boolean isValidAdministratorToken(String token)
    {
        return isValidToken(token, "administrator");
    }
    public boolean isValidUserToken(String token)
    {
        return isValidToken(token, "user");
    }
    private boolean isValidToken(String token, String userType)
    {
        this.token = token;
        HashMap<String, String> selectedParameters = new HashMap();
        selectedParameters.put("tokens.token", token);
        selectedParameters.put("users.role", userType);
        List<List<String>> userDetails = database.getJoinedTableRows(new ArrayList<>(Arrays.asList("tokens", "users")), 
                                                                     new ArrayList<>(Arrays.asList("tokens.users", "users.code")), 
                                                                     selectedParameters, new ArrayList<>(), new ArrayList<>(), false);
        if(userDetails.size() > 0)
        {
            try
            {
                if(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(userDetails.get(0).get(2)).compareTo(new Date()) > 0)
                    return true;
            }
            catch(ParseException error)
            {
                System.out.println(error);
            }
        }
        return false;
    }
    public List<String> getUserDetailsFromValidAdministratorToken(String token)
    {
       return getUserDetailsFromValidToken(token, "administrator"); 
    }
    public List<String> getUserDetailsFromValidUserToken(String token)
    {
        return getUserDetailsFromValidToken(token, "user");
    }
    private List<String> getUserDetailsFromValidToken(String token, String userRole)
    {
        this.token = token;
        List<String> formattedUserDetails = new ArrayList<>();
        HashMap<String, String> selectedParameters = new HashMap<>();
        selectedParameters.put("tokens.token", token);
        selectedParameters.put("users.role", userRole);
        List<List<String>> userDetails = database.getJoinedTableRows(new ArrayList<>(Arrays.asList("users", "tokens")), 
                                         new ArrayList<>(Arrays.asList("users.code", "tokens.users")), selectedParameters, new ArrayList<>
                                         (Arrays.asList("users.code", "users.username", "users.password", "users.firstname", "users.lastname",
                                         "users.message", "tokens.expirydate")), new ArrayList<>(), false);
        if(userDetails.size() > 0)
        {
            try
            {
                if(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(userDetails.get(0).get(6)).compareTo(new Date()) > 0)
                {
                    userDetails.get(0).remove(userDetails.get(0).size() - 1);
                    userDetails.forEach(x -> x.forEach(y -> formattedUserDetails.add(y)));
                }
            }
            catch(ParseException error)
            {
                System.out.println(error);
            }
        }
        return formattedUserDetails;
    }
}
