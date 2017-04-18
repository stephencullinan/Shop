package Services;
import Database.*;
import MetroComponents.*;
import Utilities.*;
import java.util.*;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
@Path("LogOn")
public class LogOn 
{
    public LogOn() 
    {
        
    }
    @GET
    @Path("/getLogOnPage")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLogOnPage()
    {               
        MetroLayout logOnPageLayout = new MetroLayout(2);
        MetroTextField username = new MetroTextField("username", "Please enter your username", "user", "text", "");
        MetroTextField password = new MetroTextField("password", "Please enter your password", "security", "password", "");
        MetroPanel usernamePanel = new MetroPanel("Please enter your username", "user", username);
        MetroPanel passwordPanel = new MetroPanel("Please enter your password", "security", password);
        MetroCommandButton logOnButton = new MetroCommandButton("Log On", "Log On To The Shop", "checkmark", "success", 
        new JavaScript().updateContent("LogOn/processLogOn", new ArrayList<>(Arrays.asList("username", "password"))));
        logOnPageLayout.addRow(usernamePanel);
        logOnPageLayout.addRow(passwordPanel);
        logOnPageLayout.addRow(logOnButton, new ArrayList<>(Arrays.asList(4, 4, 4)));
        MetroAccordion logOnAccordion = new MetroAccordion("logOnAccordion");
        logOnAccordion.addFrame("Log On To The Shop", "enter", logOnPageLayout, true);
        return new JSON().convertComponentToJSON(logOnAccordion);
    }
    @POST
    @Path("/processLogOn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String processLogOn(String logOnDetails)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> logOnParameters = json.convertJSONToHashMap(logOnDetails);
        if(logOnParameters.containsKey("username") && logOnParameters.containsKey("password"))
        {
            if(logOnParameters.get("username").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Username", "A valid username should contain at least 2 characters");
            if(logOnParameters.get("password").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Password", "A valid password should contain at least 2 characters");
            Database database = new Database();
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("username", logOnParameters.get("username"));
            List<List<String>> userDetails = database.getTableRows("users", selectedParameters);
            if(userDetails.isEmpty())
                return json.convertErrorNotificationToJSON("Invalid Username", logOnParameters.get("username") + " is not a valid username");
            if(!userDetails.get(0).get(2).equals(logOnParameters.get("password")))
                return json.convertErrorNotificationToJSON("Invalid Password", logOnParameters.get("password") + " is not a valid " + 
                                                                 "password for " + logOnParameters.get("username"));
            String tokenCode = new Token().generateToken(userDetails.get(0).get(0));
            if(userDetails.get(0).get(3).equals("user"))
            {
                MetroLayout shopLayout = new MetroLayout(0);
                MetroFluentMenu shopFluentMenu = new MetroFluentMenu("shopFluentMenu", "Main Menu", 
                javascript.updateContent("Shop/getMainMenu", "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") + 
                javascript.show("shopUpdatePanel"), new ArrayList(Arrays.asList("User Actions")));
                MetroFluentMenuButton viewAllProducts = new MetroFluentMenuButton("View All Products", "shopping-basket", 
                javascript.updateContent("Product/viewAllProducts", "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") + 
                javascript.show("shopUpdatePanel"));
                MetroFluentMenuButton viewAllUserProfiles = new MetroFluentMenuButton("View All User Profiles", "profile",
                javascript.updateContent("UserProfile/viewAllUserProfiles", "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") +
                javascript.show("shopUpdatePanel"));
                MetroFluentMenuButton search = new MetroFluentMenuButton("Search", "search", 
                javascript.updateContent("Search/getSearchPage", "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") + 
                javascript.show("shopUpdatePanel"));
                MetroFluentMenuButton editProfile = new MetroFluentMenuButton("Edit My Profile", "pencil",
                javascript.updateContent("UserProfile/editUserProfile", "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") + 
                javascript.show("shopUpdatePanel"));
                MetroFluentMenuButton shoppingCart = new MetroFluentMenuButton("My Shopping Cart", "shopping-basket",
                javascript.updateContent("ShoppingCart/getShoppingCart", "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") + 
                javascript.show("shopUpdatePanel"));
                MetroFluentMenuButton logOut = new MetroFluentMenuButton("Log Out", "exit",
                javascript.getContent("LogOn/getLogOnPage"));
                List<MetroFluentMenuButton> fluentMenuButtons = new ArrayList<>(Arrays.asList(viewAllProducts, viewAllUserProfiles, search, 
                editProfile, shoppingCart, logOut));
                List<MetroFluentMenuPanelGroup> panelGroups = new ArrayList<>(Collections.singletonList(
                new MetroFluentMenuPanelGroup(fluentMenuButtons, "User Actions"))); 
                shopFluentMenu.addTab(panelGroups);
                MetroUpdatePanel shopUpdatePanel = new MetroUpdatePanel("shopUpdatePanel", new MetroHiddenInput("token", tokenCode));
                MetroUpdatePanel selectedItemUpdatePanel = new MetroUpdatePanel("selectedItemUpdatePanel", new MetroHiddenInput("token",
                tokenCode));
                shopLayout.addRow(shopFluentMenu);
                shopLayout.addRow(shopUpdatePanel);
                shopLayout.addRow(selectedItemUpdatePanel);
                return json.convertComponentAndSuccessNotificationToJSON(shopLayout, "Logged In As A Customer", 
                "You have been logged in as " + userDetails.get(0).get(1));
            }
            else if(userDetails.get(0).get(3).equals("administrator"))
            {
                MetroLayout administrationLayout = new MetroLayout(0);
                MetroFluentMenu administrationMenu = new MetroFluentMenu("administrationMenu", "Main Menu", 
                                                                         javascript.updateContent("Administration/getMainMenu", 
                                                                         "administrationPanel"), 
                                                                         new ArrayList<>(Arrays.asList("User Actions")));
                List<MetroFluentMenuButton> productsButtons = new ArrayList<>();
                productsButtons.add(new MetroFluentMenuButton("View Products", "shopping-basket", 
                                                              javascript.updateContent("Administration/viewProductsTable", 
                                                              "administrationPanel")));
                productsButtons.add(new MetroFluentMenuButton("Add A Product", "plus", javascript.updateContent("Administration/addAProduct", 
                                                                                                                "administrationPanel")));
                productsButtons.add(new MetroFluentMenuButton("Delete A Product", "bin", 
                                                              javascript.updateContent("Administration/deleteProductsTable", "administrationPanel")));
                productsButtons.add(new MetroFluentMenuButton("Adjust Stock Quantity", "shopping-basket2", 
                                                              javascript.updateContent("Administration/adjustStockQuantityTable", 
                                                              "administrationPanel")));
                productsButtons.add(new MetroFluentMenuButton("Log Out", "exit", javascript.getContent("LogOn/getLogOnPage")));
                MetroFluentMenuPanelGroup productsGroup = new MetroFluentMenuPanelGroup(productsButtons, "User Actions");
                administrationMenu.addTab(new ArrayList<>(Collections.singletonList(productsGroup)));
                administrationLayout.addRow(administrationMenu);
                administrationLayout.addRow(new MetroUpdatePanel("administrationPanel", new MetroHiddenInput("token", tokenCode)));
                return json.convertComponentAndSuccessNotificationToJSON(administrationLayout, "Logged In As An Administrator", 
                                                                               "You have been logged in as " + userDetails.get(0).get(1));
            }
        }
        return json.convertErrorNotificationToJSON("Invalid Data", "Invalid data has been supplied to the server");
    }
}