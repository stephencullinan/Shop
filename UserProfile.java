package Services;
import Database.*;
import MetroComponents.*;
import Utilities.*;
import java.text.*;
import java.util.*;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
@Path("UserProfile")
public class UserProfile 
{
    public UserProfile() 
    {
        
    }
    @POST
    @Path("/viewAllUserProfiles")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String viewAllUserProfiles(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidUserToken(receivedParameters.get("token")))
        {
            List<MetroComponent> searchResultsTiles = new ArrayList<>();
            Database database = new Database();
            List<List<String>> userDetails = database.getTableRows("users", new HashMap(), new ArrayList(),
            new ArrayList(Arrays.asList("lastname", "firstname")));
            userDetails.forEach(x ->
            {
                MetroSideBar aSideBar = new MetroSideBar("");
                aSideBar.addItem("User ID", x.get(0), "user");
                aSideBar.addItem("User Name", x.get(1), "user");
                aSideBar.addItem("User Title", x.get(4) + " " + x.get(5), "user");
                searchResultsTiles.add(new MetroTileWithControl(aSideBar, javascript.updateContent("UserProfile/viewUserProfile",
                "selectedItemUpdatePanel", new ArrayList(Collections.singletonList("token")), new ArrayList(Arrays.asList("userID", x.get(0))))
                + javascript.show("selectedItemUpdatePanel") + javascript.hide("shopUpdatePanel")));
            });
            MetroLayout viewAllUserProfilesLayout = new MetroLayout(2);
            viewAllUserProfilesLayout.addMultipleRows(searchResultsTiles, new ArrayList(Arrays.asList(1, 4, 1)));
            viewAllUserProfilesLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion viewAllUserProfilesAccordion = new MetroAccordion("viewAllUserProfilesAccordion");
            viewAllUserProfilesAccordion.addFrame("View All Users", "profile", viewAllUserProfilesLayout, true);
            return json.convertComponentToJSON(viewAllUserProfilesAccordion);
        }    
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/viewUserProfile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String viewUserProfile(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidUserToken(receivedParameters.get("token")))
        {
            MetroLayout userProfileLayout = new MetroLayout(2);
            Database database = new Database();
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("code", receivedParameters.get("userID"));
            List<List<String>> userDetails = database.getTableRows("users", selectedParameters);
            if(userDetails.size() > 0)
            {
                List<String> anUserDetails = userDetails.get(0);
                MetroCommandButton backButton = new MetroCommandButton("Return", "Return To Search Results", "exit", "danger", 
                javascript.hide("selectedItemUpdatePanel") + javascript.show("shopUpdatePanel"));
                userProfileLayout.addRow(backButton, new ArrayList<>(Arrays.asList(1, 3, 8)));
                MetroTile usernameTile = new MetroTile(anUserDetails.get(1), "user", "", "usernameTile", false);
                MetroTile nameTile = new MetroTile(anUserDetails.get(4) + " " + anUserDetails.get(5), "user", "", "nameTile", false);
                userProfileLayout.addRow(new ArrayList<>(Arrays.asList(usernameTile, nameTile)), new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
                MetroPanel messagePanel = new MetroPanel("Message", "mail-read", 
                new MetroTextBlock("Message", anUserDetails.get(6), true));
                userProfileLayout.addRow(messagePanel);
                return json.convertComponentToJSON(userProfileLayout);
            }
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/editUserProfile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String editUserProfile(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            MetroLayout editUserProfileLayout = new MetroLayout(0);
            if(receivedParameters.containsKey("confirmationMessage"))
            {
                editUserProfileLayout.addEmptyRows(2);
                editUserProfileLayout.addRow(new MetroPopover(receivedParameters.get("confirmationMessage"), "bottom"), 
                                             new ArrayList<>(Arrays.asList(2, 8, 2)));
                editUserProfileLayout.addEmptyRows(2);
            }
            MetroTextField username = new MetroTextField("username", "Please enter your username", "user", "text", userDetails.get(1));
            MetroPanel usernamePanel = new MetroPanel("Please enter your username", "user", username);
            MetroTextField password = new MetroTextField("password", "Please enter your password", "security", "password", userDetails.get(2));
            MetroPanel passwordPanel = new MetroPanel("Please enter your password", "security", password);
            MetroTextField firstName = new MetroTextField("firstName", "Please enter your first name", "user", "text", userDetails.get(3));
            MetroPanel firstNamePanel = new MetroPanel("Please enter your first name", "user", firstName);
            MetroTextField lastName = new MetroTextField("lastName", "Please enter your last name", "user", "text", userDetails.get(4));
            MetroPanel lastNamePanel = new MetroPanel("Please enter your last name", "user", lastName);
            MetroTextField message = new MetroTextField("message", "Please enter your message", "mail-read", "text", userDetails.get(5));
            MetroPanel messagePanel = new MetroPanel("Please enter your message", "mail-read", message);
            MetroCommandButton submitButton = new MetroCommandButton("Update", "Upodate Your Details", "checkmark", "success", 
            javascript.updateContent("UserProfile/saveUserProfile", "shopUpdatePanel", new ArrayList<>(Arrays.asList("username", "password", 
            "firstName", "lastName", "message", "token")), 
            new ArrayList<>(Arrays.asList("confirmationMessage", "Your user profile was updated at " + 
            new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()), "userID", userDetails.get(0)))));
            editUserProfileLayout.addRow(usernamePanel);
            editUserProfileLayout.addRow(passwordPanel);
            editUserProfileLayout.addRow(firstNamePanel);
            editUserProfileLayout.addRow(lastNamePanel);
            editUserProfileLayout.addRow(messagePanel);
            if(receivedParameters.containsKey("confirmationMessage"))
            {
                editUserProfileLayout.addEmptyRows(2);
                editUserProfileLayout.addRow(new MetroPopover(receivedParameters.get("confirmationMessage"), "top"), 
                                             new ArrayList<>(Arrays.asList(2, 8, 2)));
                editUserProfileLayout.addEmptyRows(2);
            }
            editUserProfileLayout.addRow(submitButton, new ArrayList<>(Arrays.asList(4, 4, 4)));
            editUserProfileLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion editUserProfileAccordion = new MetroAccordion("editUserProfileAccordion");
            editUserProfileAccordion.addFrame("Edit Your Profile", "profile", editUserProfileLayout, true);
            return json.convertComponentToJSON(editUserProfileAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/saveUserProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String saveUserProfile(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidUserToken(receivedParameters.get("token")))
        {
            HashMap<String, String> updatedParameters = new HashMap<>();
            if(receivedParameters.get("username").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Username", "A valid username should be at least 2 characters long");
            if(receivedParameters.get("password").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Password", "A valid password should be at least 2 characters long");
            if(receivedParameters.get("firstName").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid First Name", "A valid first name should be at least 2 characters long");
            if(receivedParameters.get("lastName").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Last Name", "A valid last name should be at least 2 character long");
            if(receivedParameters.get("message").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Message", "A valid message should be at least 2 characters long");
            HashMap<String, String> existingUsernameParameters = new HashMap<>();
            existingUsernameParameters.put("username", receivedParameters.get("username"));
            Database database = new Database();
            List<List<String>> existingUsernames = database.getTableRows("users", existingUsernameParameters);
            for(List<String> anExistingUsername: existingUsernames)
                if(!anExistingUsername.get(0).equals(receivedParameters.get("userID")))
                    return json.convertErrorNotificationToJSON("Invalid Username", "The username " + receivedParameters.get("username") + 
                    " is already in use");
            updatedParameters.put("username", receivedParameters.get("username"));
            updatedParameters.put("password", receivedParameters.get("password"));
            updatedParameters.put("firstname", receivedParameters.get("firstName"));
            updatedParameters.put("lastname", receivedParameters.get("lastName"));
            updatedParameters.put("message", receivedParameters.get("message"));
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("code", receivedParameters.get("userID"));
            database.updateTableRow("users", updatedParameters, selectedParameters);
            return editUserProfile(content);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
}
