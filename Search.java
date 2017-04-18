package Services;
import Database.*;
import MetroComponents.*;
import Utilities.*;
import java.util.*;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
@Path("Search")
public class Search 
{
    private final Database database;
    public Search()
    {
        database = new Database();
    }
    @POST
    @Path("/getSearchPage")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getSearchPage(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidUserToken(receivedParameters.get("token")))
        {                    
            MetroLayout userFiltersLayout = new MetroLayout(0);
            MetroPanel productIDPanel = createFilterPanel("productID", new ArrayList<>(Arrays.asList("Include Product ID", 
            "Exclude Product ID")));
            MetroPanel productTitlePanel = createFilterPanel("productTitle", new ArrayList<>(Arrays.asList("Include Product Title", 
            "Exclude Product Title")));
            MetroPanel userIDPanel = createFilterPanel("userID", new ArrayList<>(Arrays.asList("Include User ID", "Exclude User ID")));
            MetroPanel userTitlePanel = createFilterPanel("userTitle", new ArrayList<>(Arrays.asList("Include User Title", 
            "Exclude User Title")));
            userFiltersLayout.addRow(new ArrayList<>(Arrays.asList(productIDPanel, productTitlePanel)), 
            new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
            userFiltersLayout.addRow(new ArrayList<>(Arrays.asList(userIDPanel, userTitlePanel)), 
            new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
            MetroAccordion userFiltersAccordion = new MetroAccordion("userFiltersAccordion");
            userFiltersAccordion.addFrame("Apply Filters To Search Results", "search", userFiltersLayout, false);
            MetroLayout searchPageLayout = new MetroLayout(0);
            MetroTextField searchTerm = new MetroTextField("searchTerm", "Please enter the search term", "search", "text", "");
            MetroCommandButton searchButton = new MetroCommandButton("Search", "Search With Your Specified Search Term", "search", "success", 
                                              javascript.updateContent("Search/getSearchResults", "searchResultsUpdatePanel", 
                                              new ArrayList<>(Arrays.asList("token", "searchTerm", "productID", "productTitle", 
                                              "userID", "userTitle"))));
            searchPageLayout.addRow(userFiltersAccordion);
            searchPageLayout.addRow(searchTerm);
            searchPageLayout.addRow(searchButton, new ArrayList<>(Arrays.asList(4, 4, 4)));
            MetroHiddenInput token = new MetroHiddenInput("token", receivedParameters.get("token"));
            MetroUpdatePanel searchResultsUpdatePanel = new MetroUpdatePanel("searchResultsUpdatePanel", token);
            searchPageLayout.addRow(searchResultsUpdatePanel);
            return json.convertComponentToJSON(searchPageLayout);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/getSearchResults")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getSearchResults(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        List<MetroComponent> searchResultsTiles = new ArrayList<>();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidUserToken(receivedParameters.get("token")))
        {
            if((receivedParameters.get("productID").equals("on") && isNumeric(receivedParameters.get("searchTerm"))) 
            || (receivedParameters.get("productTitle").equals("on") && !isNumeric(receivedParameters.get("searchTerm"))))
            {
                HashMap<String, String> selectedParameters = new HashMap<>();
                if(receivedParameters.get("productID").equals("on") && isNumeric(receivedParameters.get("searchTerm")))
                    selectedParameters.put("code", receivedParameters.get("searchTerm"));
                if(receivedParameters.get("productTitle").equals("on") && !isNumeric(receivedParameters.get("searchTerm")))
                    selectedParameters.put("title", receivedParameters.get("searchTerm"));
                List<List<String>> productDetails = database.getTableRows("products", selectedParameters);
                productDetails.forEach(x ->
                {
                    MetroSideBar aSideBar = new MetroSideBar("");
                    aSideBar.addItem("Product ID", x.get(0), "shopping-basket");
                    aSideBar.addItem("Product Title", x.get(1), "shopping-basket");
                    aSideBar.addItem("Product Price", x.get(4), "eur");
                    searchResultsTiles.add(new MetroTileWithControl(aSideBar, javascript.updateContent("Product/viewProduct",
                    "selectedItemUpdatePanel", new ArrayList<>(Collections.singletonList("token")), 
                    new ArrayList<>(Arrays.asList("productID", x.get(0)))) +  javascript.show("selectedItemUpdatePanel") + 
                    javascript.hide("shopUpdatePanel")));
                });
            }
            if((receivedParameters.get("userID").equals("on") && isNumeric(receivedParameters.get("searchTerm"))) 
            || (receivedParameters.get("userTitle").equals("on") && !isNumeric(receivedParameters.get("searchTerm"))))
            {
                HashMap<String, String> selectedParameters = new HashMap<>();
                if(receivedParameters.get("userID").equals("on") && isNumeric(receivedParameters.get("searchTerm")))
                    selectedParameters.put("code", receivedParameters.get("searchTerm"));
                if(receivedParameters.get("userTitle").equals("on") && !isNumeric(receivedParameters.get("searchTerm")))
                    selectedParameters.put("username", receivedParameters.get("searchTerm"));
                List<List<String>> userDetails = database.getTableRows("users", selectedParameters);
                userDetails.forEach(x ->
                {
                    MetroSideBar aSideBar = new MetroSideBar("");
                    aSideBar.addItem("User ID", x.get(0), "user");
                    aSideBar.addItem("User Name", x.get(1), "user");
                    aSideBar.addItem("User Title", x.get(4) + " "  + x.get(5), "user");
                    searchResultsTiles.add(new MetroTileWithControl(aSideBar, javascript.updateContent("UserProfile/viewUserProfile", 
                    "selectedItemUpdatePanel", new ArrayList<>(Collections.singletonList("token")), 
                    new ArrayList<>(Arrays.asList("userID", x.get(0)))) + javascript.show("selectedItemUpdatePanel") + 
                    javascript.hide("shopUpdatePanel")));
                });
            }
            MetroLayout searchResultsLayout = new MetroLayout(0);
            if(searchResultsTiles.isEmpty())
                searchResultsLayout.addRow(new MetroPanel("No Search Results Have Been Found", "warning", 
                                           new MetroHeading("No Search Results Have Been Found", "")));
            else
                searchResultsLayout.addMultipleRows(searchResultsTiles, new ArrayList<>(Arrays.asList(1, 4, 1)));
            searchResultsLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion searchResultsAccordion = new MetroAccordion("searchResultsAccordion");
            searchResultsAccordion.addFrame("Search Results For " + receivedParameters.get("searchTerm"), "search", searchResultsLayout, true);
            return json.convertComponentToJSON(searchResultsAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    private MetroPanel createFilterPanel(String controlID, List<String> titles)
    {
        MetroSwitcher aSwitcher = new MetroSwitcher(controlID, true, titles);
        MetroLayout switcherLayout = new MetroLayout(0);
        switcherLayout.addRow(aSwitcher, new ArrayList<>(Arrays.asList(5, 2, 5)));
        return new MetroPanel(titles.get(0), "search", switcherLayout);
    }
    private boolean isNumeric(String value)
    {
        try
        {
            Double.parseDouble(value);
            return true;
        }
        catch(NumberFormatException error)
        {
            System.out.println(error);
            return false;
        }
    }
}
