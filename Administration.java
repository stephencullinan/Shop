package Services;
import MetroComponents.*;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import Utilities.*;
import java.util.*;
import Database.Database;
@Path("Administration")
public class Administration 
{
    private final Database database;
    public Administration() 
    {
        database = new Database();
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
        if(new Token().isValidAdministratorToken(receivedParameters.get("token")))
        {
            MetroLayout administrationMainMenuLayout = new MetroLayout(0);
            MetroTile viewProductsTile = new MetroTile("View Products", "shopping-basket", 
            javascript.updateContent("Administration/viewProductsTable", "administrationPanel"), "viewProductsTile", false);
            MetroTile addAProductTile = new MetroTile("Add A Product", "plus", 
            javascript.updateContent("Administration/addAProduct", "administrationPanel"), "addAProductTile", false);
            MetroTile deleteAProductTile = new MetroTile("Delete A Product", "bin",
            javascript.updateContent("Administration/deleteProductsTable", "administrationPanel"), "deleteAProductTile", false);
            MetroTile adjustStockQuantityTile = new MetroTile("Adjust Stock Quantity", "shopping-basket2",
            javascript.updateContent("Administration/adjustStockQuantityTable", "administrationPanel"), "adjustStockQuantityTile", false);
            MetroTile logOutTile = new MetroTile("Log Out", "exit", new JavaScript().getContent("LogOn/getLogOnPage"), "logOutTile", false);
            administrationMainMenuLayout.addMultipleRows(new ArrayList<>(Arrays.asList(viewProductsTile, addAProductTile, deleteAProductTile,
            adjustStockQuantityTile, logOutTile)), new ArrayList<>(Arrays.asList(1, 4, 1)));
            administrationMainMenuLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion administrationMainMenuAccordion = new MetroAccordion("administrationMainMenuAccordion");
            administrationMainMenuAccordion.addFrame("Main Menu", "menu", administrationMainMenuLayout, true);
            return json.convertComponentToJSON(administrationMainMenuAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
    @POST
    @Path("/viewProductsTable")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String viewProductsTable(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidAdministratorToken(receivedParameters.get("token")))
        {
            MetroLayout productsTableLayout = new MetroLayout(0);
            List<String> productTableHeadings = new ArrayList<>(Arrays.asList("Code", "Title", "Description", "Quantity", "Price"));
            List<List<String>> productTableContents = database.getTableRows("products");
            MetroDataTable productsTable = new MetroDataTable("productsTable", productTableHeadings, productTableContents, new ArrayList<>());
            productsTableLayout.addRow(productsTable);
            productsTableLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion productsAccordion = new MetroAccordion("productsAccordion");
            productsAccordion.addFrame("Products Table", "shopping-basket", productsTableLayout, true);
            return json.convertComponentToJSON(productsAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
    @POST
    @Path("/addAProduct")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addAProduct(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidAdministratorToken(receivedParameters.get("token")))
        {
            MetroLayout addProductLayout = new MetroLayout(2);
            MetroTextField productTitle = new MetroTextField("productTitle", "Please enter the title of the product", "shopping-basket", 
                                                             "text", "");
            MetroPanel productTitlePanel = new MetroPanel("Please enter the title of the product", "shopping-basket", productTitle);
            addProductLayout.addRow(productTitlePanel);
            MetroTextField productDescription = new MetroTextField("productDescription", "Please enter the description of the product", 
                                                                   "shopping-basket", "text", "");
            MetroPanel productDescriptionPanel = new MetroPanel("Please enter the description of the product", "shopping-basket", 
                                                                productDescription);
            addProductLayout.addRow(productDescriptionPanel);
            MetroTextField productQuantity = new MetroTextField("productQuantity", "Please enter the quantity of the product",
                                                                "list-numbered", "text", "");
            MetroPanel productQuantityPanel = new MetroPanel("Please enter the quantity of the product", "list-numbered", productQuantity);
            addProductLayout.addRow(productQuantityPanel);
            MetroTextField productPrice = new MetroTextField("productPrice", "Please enter the price of the product", "eur", "text", "");
            MetroPanel productPricePanel = new MetroPanel("Please enter the price of the product", "eur", productPrice);
            addProductLayout.addRow(productPricePanel);
            MetroCommandButton addNewProductButton = new MetroCommandButton("Add", "Add A Product", "checkmark", "success", 
                                                                            javascript.updateContent("Administration/processNewProduct", 
                                                                            "administrationPanel", new ArrayList<>(Arrays.asList("token", 
                                                                            "productTitle", "productDescription", "productQuantity", 
                                                                            "productPrice"))));
            MetroCommandButton cancelButton = new MetroCommandButton("Cancel", "Return To Products", "exit", "danger", 
                                                                     javascript.updateContent("Administration/viewProductsTable", 
                                                                     "administrationPanel"));
            addProductLayout.addRow(new ArrayList<>(Arrays.asList(addNewProductButton, cancelButton)), 
                                    new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
            addProductLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion addProductAccordion = new MetroAccordion("addProductAccordion");
            addProductAccordion.addFrame("Add A Product", "shopping-basket", addProductLayout, true);
            return json.convertComponentToJSON(addProductAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
    @POST
    @Path("/processNewProduct")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String processNewProduct(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        Integer productQuantity = 0;
        Double productPrice = 0.0;
        List<String> userDetails = new Token().getUserDetailsFromValidAdministratorToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            if(receivedParameters.get("productTitle").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Product Title", 
                                                           "A valid product title should contain at least 2 characters");
            if(receivedParameters.get("productDescription").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Product Description", 
                                                           "A valid product description should contain at least 2 characters");
            try
            {
                productQuantity = Integer.parseInt(receivedParameters.get("productQuantity"));
            }
            catch(NumberFormatException error)
            {
                System.out.println(error);
                return json.convertErrorNotificationToJSON("Invalid Product Quantity", 
                                                           "A valid product quantity should be a positive integer value");
            }
            if(productQuantity < 0)
                return json.convertErrorNotificationToJSON("Invalid Product Quantity", 
                                                           "A valid product quantity should be a positive integer value");
            try
            {
                productPrice = Double.parseDouble(receivedParameters.get("productPrice"));
            }
            catch(NumberFormatException error)
            {
                System.out.println(error);
                return json.convertErrorNotificationToJSON("Invalid Product Price", 
                                                           "A valid product price should be a positive integer value");
            }
            if(productPrice < 0)
                return json.convertErrorNotificationToJSON("Invalid Product Price", "A valid product price should be a positive integer value");
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("title", receivedParameters.get("productTitle"));
            List<List<String>> existingProductDetails = database.getTableRows("products", selectedParameters);
            if(existingProductDetails.size() > 0)
                return json.convertErrorNotificationToJSON("Invalid Product Title", "A product already exists with this title");
            Double productID = database.getMaxValueOfColumn("products", "code") + 1;
            database.insertTableRow("products", new ArrayList<>(Arrays.asList(productID + "", receivedParameters.get("productTitle"), 
                                    receivedParameters.get("productDescription"), productQuantity + "", productPrice + "")));
            new Log().addLog(userDetails.get(0), "Product was added successfully");
            return viewProductsTable(content);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
    @POST
    @Path("/deleteProductsTable")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteProductsTable(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidAdministratorToken(receivedParameters.get("token")))
        {
            MetroLayout deleteProductsLayout = new MetroLayout(0);
            List<String> deleteProductTableClickEvents = new ArrayList<>();
            List<String> deleteProductTableHeadings = new ArrayList<>(Arrays.asList("Code", "Title", "Description", "Quantity", "Price"));
            List<List<String>> deleteProductTableContents = database.getTableRows("products");
            deleteProductTableContents.forEach(x -> deleteProductTableClickEvents.add(
            new JavaScript().updateContent("Administration/deleteProductConfirmation", "administrationPanel", 
            new ArrayList<>(Arrays.asList("token")), new ArrayList<>(Arrays.asList("productID", x.get(0))))));
            MetroDataTable deleteProductsTable = new MetroDataTable("deleteProductsTable", deleteProductTableHeadings, 
                                                 deleteProductTableContents, deleteProductTableClickEvents);
            deleteProductsLayout.addEmptyRows(2);
            deleteProductsLayout.addRow(new MetroPopover("Delete A Product By Selecting A Product From The Table", "bottom"), 
                                        new ArrayList<>(Arrays.asList(3, 6, 3)));
            deleteProductsLayout.addEmptyRows(2);
            deleteProductsLayout.addRow(deleteProductsTable);
            deleteProductsLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion deleteProductsAccordion = new MetroAccordion("deleteProductsAccordion");
            deleteProductsAccordion.addFrame("Delete Products Table", "bin", deleteProductsLayout, true);
            return json.convertComponentToJSON(deleteProductsAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
    @POST
    @Path("/deleteProductConfirmation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteProductConfirmation(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidAdministratorToken(receivedParameters.get("token")))
        {
            MetroLayout deleteProductConfirmationLayout = new MetroLayout(2);
            List<String> deleteProductTableHeadings = new ArrayList<>(Arrays.asList("Code", "Title", "Description", "Quantity", "Price"));
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("code", receivedParameters.get("productID"));
            List<List<String>> deleteProductTableContents = database.getTableRows("products", selectedParameters);
            MetroHeading deleteProductConfirmationHeading = new MetroHeading("Are you sure you wish to delete the selected product?", "");
            MetroDataTable deleteProductConfirmationTable = new MetroDataTable("deleteSelectedProductConfirmationTable", 
            deleteProductTableHeadings, deleteProductTableContents, new ArrayList<>());
            MetroCommandButton deleteProductConfirmationButton = new MetroCommandButton("Delete", "Delete Selected Product", "checkmark", 
                                                                                        "success", 
                                                                                        javascript.updateContent("Administration/deleteProduct", 
                                                                                        "administrationPanel", 
                                                                                        new ArrayList<>(Arrays.asList("token")), 
                                                                                        new ArrayList<>(Arrays.asList("productID", 
                                                                                        receivedParameters.get("productID")))));
            MetroCommandButton cancelProductConfirmationButton = new MetroCommandButton("Cancel", "Return To Table", "exit", "danger",
                                                                                        javascript.updateContent(
                                                                                        "Administration/deleteProductsTable", "administrationPanel"));
            deleteProductConfirmationLayout.addRow(deleteProductConfirmationHeading);
            deleteProductConfirmationLayout.addRow(deleteProductConfirmationTable);
            deleteProductConfirmationLayout.addRow(new ArrayList<>(Arrays.asList(deleteProductConfirmationButton, cancelProductConfirmationButton)),
                                                   new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
            deleteProductConfirmationLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion deleteProductConfirmationAccordion = new MetroAccordion("deleteProductConfirmationAccordion");
            deleteProductConfirmationAccordion.addFrame("Delete Selected Product", "bin", deleteProductConfirmationLayout, true);
            return json.convertComponentToJSON(deleteProductConfirmationAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
    @POST
    @Path("/deleteProduct")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteProduct(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidAdministratorToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("code", receivedParameters.get("productID"));
            database.removeTableRow("products", selectedParameters);
            new Log().addLog(userDetails.get(0), "Product was deleted successfully");
            return deleteProductsTable(content);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
    @POST
    @Path("/adjustStockQuantityTable")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String adjustStockQuantityTable(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidAdministratorToken(receivedParameters.get("token")))
        {
            MetroLayout adjustStockQuantityLayout = new MetroLayout(0);
            List<String> adjustStockQuantityTableClickEvents = new ArrayList<>();
            List<String> adjustStockQuantityTableHeadings = new ArrayList<>(Arrays.asList("Code", "Title", "Description", "Quantity"));
            List<List<String>> adjustStockQuantityTableContents = database.getTableRows("products");
            adjustStockQuantityTableContents.forEach(x -> adjustStockQuantityTableClickEvents.add(
            new JavaScript().updateContent("Administration/updateStockQuantity", "administrationPanel", new ArrayList<>(Arrays.asList("token")), 
            new ArrayList<>(Arrays.asList("productID", x.get(0), "productQuantity", x.get(3), "productTitle", x.get(1))))));
            MetroDataTable adjustStockQuantityTable = new MetroDataTable("adjustStockQuantityTable", adjustStockQuantityTableHeadings,
                                                                         adjustStockQuantityTableContents, adjustStockQuantityTableClickEvents);
            adjustStockQuantityLayout.addEmptyRows(2);
            adjustStockQuantityLayout.addRow(new MetroPopover("Adjust The Stock Quantity By Selecting A Product From The Table", "bottom"),
                                             new ArrayList<>(Arrays.asList(3, 6, 3)));
            adjustStockQuantityLayout.addEmptyRows(2);
            adjustStockQuantityLayout.addRow(adjustStockQuantityTable);
            adjustStockQuantityLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion adjustStockQuantityAccordion = new MetroAccordion("adjustStockQuantityAccordion");
            adjustStockQuantityAccordion.addFrame("Adjust The Stock Quantity", "shopping-basket", adjustStockQuantityLayout, true);
            return json.convertComponentToJSON(adjustStockQuantityAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
    @POST
    @Path("/updateStockQuantity")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateStockQuantity(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidAdministratorToken(receivedParameters.get("token")))
        {
            MetroLayout updatedStockQuantityLayout = new MetroLayout(2);
            MetroTextField updatedStockQuantity = new MetroTextField("updatedStockQuantity", "Please enter the updated quantity for " + 
                                                  receivedParameters.get("productTitle"), "shopping-basket", "text", 
                                                  receivedParameters.get("productQuantity"));
            MetroPanel updatedStockQuantityPanel = new MetroPanel("Please enter the updated quantity for " + 
                                                   receivedParameters.get("productTitle"), "shopping-basket", updatedStockQuantity);
            MetroCommandButton updatedStockQuantityButton = new MetroCommandButton("Update", "Update The Quantity", "checkmark", 
                                                                   "success", javascript.updateContent("Administration/saveUpdatedStockQuantity", 
                                                                   "administrationPanel",
                                                                   new ArrayList<>(Arrays.asList("token", "updatedStockQuantity")),
                                                                   new ArrayList<>(Arrays.asList("productID", 
                                                                   receivedParameters.get("productID")))));
            MetroCommandButton cancelStockQuantityButton = new MetroCommandButton("Cancel", "Return To Table", "exit", "danger",
                                                                  javascript.updateContent("Administration/adjustStockQuantityTable", 
                                                                  "administrationPanel"));
            updatedStockQuantityLayout.addRow(updatedStockQuantityPanel);
            updatedStockQuantityLayout.addRow(new ArrayList<>(Arrays.asList(updatedStockQuantityButton, cancelStockQuantityButton)), 
                                              new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
            updatedStockQuantityLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion updatedStockQuantityAccordion = new MetroAccordion("updatedStockQuantityAccordion");
            updatedStockQuantityAccordion.addFrame("Updated Quantity Of " + receivedParameters.get("productTitle"), "shopping-basket", 
                                                   updatedStockQuantityLayout, true);
            return json.convertComponentToJSON(updatedStockQuantityAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
    @POST
    @Path("/saveUpdatedStockQuantity")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String saveUpdatedStockQuantity(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidAdministratorToken(receivedParameters.get("token")))
        {
            int productQuantity = 0;
            try
            {
                productQuantity = Integer.parseInt(receivedParameters.get("updatedStockQuantity"));
            }
            catch(NumberFormatException error)
            {
                return json.convertErrorNotificationToJSON("Invalid Stock Quantity", "The stock quantity must be a valid positive integer");
            }
            if(productQuantity < 0)
                return json.convertErrorNotificationToJSON("Invalid Stock Quantity", "The stock quantity must be a valid positive integer");
            HashMap<String, String> updatedParameters = new HashMap<>();
            updatedParameters.put("quantity", productQuantity + "");
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("code", receivedParameters.get("productID"));
            database.updateTableRow("products", updatedParameters, selectedParameters);
            return adjustStockQuantityTable(content);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid administrator token");
    }
}
