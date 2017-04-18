package Services;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import Utilities.*;
import java.util.*;
import MetroComponents.*;
@Path("Shop")
public class Shop 
{
    public Shop() 
    {
        
    }
    @POST
    @Path("/getMainMenu")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getMainMenu(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<MetroComponent> mainMenuTiles = new ArrayList();
        if(new Token().isValidUserToken(receivedParameters.get("token")))
        {
            mainMenuTiles.add(new MetroTile("View All Products", "shopping-basket", javascript.updateContent("Product/viewAllProducts", 
            "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") + javascript.show("shopUpdatePanel"), "viewAllProductsTile", false));
            mainMenuTiles.add(new MetroTile("View All User Profiles", "profile", javascript.updateContent("UserProfile/viewAllUserProfiles", 
            "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") + javascript.show("shopUpdatePanel"), "viewAllUserProfilesTile", false));
            mainMenuTiles.add(new MetroTile("Search", "search", javascript.updateContent("Search/getSearchPage", "shopUpdatePanel") + 
            javascript.hide("selectedItemUpdatePanel") + javascript.show("shopUpdatePanel"), "searchTile", false));
            mainMenuTiles.add(new MetroTile("Edit My Profile", "pencil", javascript.updateContent("UserProfile/editUserProfile", 
            "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") + javascript.show("shopUpdatePanel"), "editMyProfile", false));
            mainMenuTiles.add(new MetroTile("My Shopping Cart", "shopping-basket", javascript.updateContent("ShoppingCart/getShoppingCart", 
            "shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel") + javascript.show("shopUpdatePanel"), "shoppingCart", false));
            mainMenuTiles.add(new MetroTile("Log Out", "exit", javascript.getContent("LogOn/getLogOnPage"), "logOut", false));
            MetroLayout mainMenuLayout = new MetroLayout(2);
            mainMenuLayout.addMultipleRows(mainMenuTiles, new ArrayList(Arrays.asList(1, 4, 1)));
            mainMenuLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion mainMenuAccordion = new MetroAccordion("mainMenuAccordion");
            mainMenuAccordion.addFrame("Main Menu", "menu", mainMenuLayout, true);
            return json.convertComponentToJSON(mainMenuAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Data", "Invalid data has been supplied to the server");
    }
}
