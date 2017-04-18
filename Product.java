package Services;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import Utilities.*;
import java.util.*;
import MetroComponents.*;
import Database.*;
import Stock.*;
@Path("Product")
public class Product 
{
    public Product() 
    {
        
    }
    @POST
    @Path("/viewAllProducts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String viewAllProducts(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<MetroComponent> searchResultsTiles = new ArrayList();
        if(new Token().isValidUserToken(receivedParameters.get("token")))
        {
            Database database = new Database();
            List<List<String>> productDetails = database.getTableRows("products", new HashMap(), new ArrayList(),
            new ArrayList(Arrays.asList("title", "code")));
            productDetails.forEach(x -> 
            {
                MetroSideBar aSideBar = new MetroSideBar("");
                aSideBar.addItem("Product ID", x.get(0), "shopping-basket");
                aSideBar.addItem("Product Title", x.get(1), "shopping-basket");
                aSideBar.addItem("Product Price", x.get(4), "eur");
                searchResultsTiles.add(new MetroTileWithControl(aSideBar, javascript.updateContent("Product/viewProduct", 
                "selectedItemUpdatePanel", new ArrayList<>(Collections.singletonList("token")), 
                new ArrayList<>(Arrays.asList("productID", x.get(0)))) + javascript.show("selectedItemUpdatePanel") + 
                javascript.hide("shopUpdatePanel")));
            });
            MetroLayout viewAllProductsLayout = new MetroLayout(2);
            viewAllProductsLayout.addMultipleRows(searchResultsTiles, new ArrayList<>(Arrays.asList(1, 4, 1)));
            viewAllProductsLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion viewAllProductsAccordion = new MetroAccordion("viewAllProductsAccordion");
            viewAllProductsAccordion.addFrame("View Available Products", "shopping-basket", viewAllProductsLayout, true);
            return json.convertComponentToJSON(viewAllProductsAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Data", "Invalid data has been supplied to the server");
    }
    @POST
    @Path("/viewProduct")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String viewProduct(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            Database database = new Database();
            MetroCommandButton backButton = new MetroCommandButton("Return", "Return To Previous Page", "exit", "danger", 
            javascript.show("shopUpdatePanel") + javascript.hide("selectedItemUpdatePanel"));
            HashMap<String, String> filteredParameters = new HashMap<>();
            filteredParameters.put("users", userDetails.get(0));
            filteredParameters.put("products", receivedParameters.get("productID"));
            MetroAccordion addToCartAccordion = new MetroAccordion("addToCartAccordion");
            HashMap<String, String> selectedParameters = new HashMap();
            selectedParameters.put("code", receivedParameters.get("productID"));
            List<List<String>> existingProductDetails = database.getTableRows("products", selectedParameters);
            List<List<String>> productAddedToCart = database.getTableRows("shoppingcarts", filteredParameters, new ArrayList<>(), 
            new ArrayList<>(), false);
            if( existingProductDetails.size() > 0 && productAddedToCart.isEmpty())
            {
                MetroLayout addToCartLayout = new MetroLayout(0);
                MetroTile addToCart = new MetroTile("Add To Cart", "shopping-basket", javascript.updateContent("Product/addProductToCart", 
                "addToCartUpdatePanel", new ArrayList<>(Arrays.asList("token")), new ArrayList<>(Arrays.asList("productID", 
                existingProductDetails.get(0).get(0)))), "addToCart", false);
                addToCartLayout.addRow(addToCart, new ArrayList<>(Arrays.asList(4, 4, 4)));
                addToCartAccordion.addFrame("Add To Cart", "shopping-basket", addToCartLayout, true);
            }
            else if(productAddedToCart.size() > 0 && existingProductDetails.size() > 0)
            {
                MetroLayout addToCartLayout = new MetroLayout(0);
                MetroTile addToCart = new MetroTile("Remove From Cart", "shopping-basket", 
                javascript.updateContent("Product/removeProductFromCart", 
                "addToCartUpdatePanel", new ArrayList<>(Arrays.asList("token")), new ArrayList<>(Arrays.asList("productID",
                existingProductDetails.get(0).get(0)))), "addToCart", false);
                addToCartLayout.addRow(addToCart, new ArrayList<>(Arrays.asList(4, 4, 4)));
                addToCartAccordion.addFrame("Remove From Cart", "shopping-basket", addToCartLayout, true);
            }
            MetroUpdatePanel addToCartUpdatePanel = new MetroUpdatePanel("addToCartUpdatePanel", addToCartAccordion);
            MetroLayout viewProductLayout = new MetroLayout(2);
            viewProductLayout.addRow(backButton, new ArrayList<>(Arrays.asList(1, 3, 8)));
            viewProductLayout.addRow(addToCartUpdatePanel, new ArrayList<>(Arrays.asList(0, 12, 0)));
            if(existingProductDetails.size() > 0)
            {
                List<String> existingProduct = existingProductDetails.get(0);
                MetroTile productTitle = new MetroTile(existingProduct.get(1), "shopping-basket", "", "productTitle", false);
                MetroTile productPrice = new MetroTile(existingProduct.get(4), "eur", "", "productPrice", false);
                viewProductLayout.addRow(new ArrayList<>(Arrays.asList(productTitle, productPrice)), 
                new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
                MetroTextBlock productDescription = new MetroTextBlock("Description", existingProduct.get(2), true);
                viewProductLayout.addRow(productDescription);
                return json.convertComponentToJSON(viewProductLayout);
            }
        }
        return json.convertErrorNotificationToJSON("Invalid Data", "Invalid data has been supplied to the server");
    }
    @POST
    @Path("/addProductToCart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addProductToCart(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            MetroAccordion addProductToCartAccordion = new MetroAccordion("addProductToCartAccordion");
            MetroLayout addProductToCartLayout = new MetroLayout(2);
            MetroTextField productQuantity = new MetroTextField("productQuantity", "Please enter your desired quantity for the product" ,
            "shopping-basket", "text", "");
            MetroPanel productQuantityPanel = new MetroPanel("Please enter your desired quantity for the product", "shopping-basket", 
            productQuantity);
            MetroCommandButton updateProductQuantity = new MetroCommandButton("Add", "Add Your Quantity", "checkmark", "success", 
            javascript.updateContent("Product/addProductToCartConfirmation", "addToCartUpdatePanel", 
            new ArrayList<>(Arrays.asList("token", "productQuantity")), 
            new ArrayList<>(Arrays.asList("productID", receivedParameters.get("productID")))));
            addProductToCartLayout.addRow(productQuantityPanel);
            addProductToCartLayout.addRow(updateProductQuantity, new ArrayList<>(Arrays.asList(4, 4, 4)));
            addProductToCartAccordion.addFrame("Add Product To Cart", "shopping-basket", addProductToCartLayout, true);
            return json.convertComponentToJSON(addProductToCartAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Data", "Invalid data has been supplied to the server");
    }
    @POST
    @Path("/addProductToCartConfirmation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addProductToCartConfirmation(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            Integer productQuantity = new Validation().validatePositiveInteger(receivedParameters.get("productQuantity"));
            if(productQuantity == -1 || productQuantity == 0)
                return json.convertErrorNotificationToJSON("Invalid Product Quantity", "A valid product quantity should consist of a valid " + 
                "positive integer");
            Stock aStock = new Stock();
            Integer currentStockLevel = aStock.getStockLevel(receivedParameters.get("productID"), userDetails.get(0));
            if(productQuantity > currentStockLevel)
                return json.convertErrorNotificationToJSON("Invalid Product Quantity", "There are only " + currentStockLevel + 
                " units available");
            Database database = new Database();
            database.insertTableRow("shoppingcarts", new ArrayList<>(Arrays.asList(userDetails.get(0), receivedParameters.get("productID"),
            productQuantity + "")));
            MetroAccordion addProductToCartConfirmationAccordion = new MetroAccordion("addProductToCartConfirmationAccordion");
            MetroHeading addProductToCartConfirmationHeading = new MetroHeading("Your product has been added successfully to the cart", "");
            MetroPanel addProductToCartConfirmationPanel = new MetroPanel("Product Added Successfully To Cart", "shopping-basket", 
            addProductToCartConfirmationHeading);
            addProductToCartConfirmationAccordion.addFrame("Product Added Successfully To Cart", "shopping-basket", 
            addProductToCartConfirmationPanel, true);
            return json.convertComponentToJSON(addProductToCartConfirmationAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Data", "Invalid data has been supplied to the server");
    }
    @POST
    @Path("/removeProductFromCart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String removeProductFromCart(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            MetroAccordion removeProductFromCartAccordion = new MetroAccordion("removeProductFromCartAccordion");
            MetroLayout removeProductFromCartLayout = new MetroLayout(2);
            MetroHeading removeProductFromCartHeading = new MetroHeading("Are you sure you wish to remove this product", "");
            removeProductFromCartLayout.addRow(removeProductFromCartHeading);
            MetroCommandButton removeProductFromCartButton = new MetroCommandButton("Remove", "Remove This Product", "bin", "danger", 
            javascript.updateContent("Product/removeProductFromCartConfirmation", "addToCartUpdatePanel", 
            new ArrayList<>(Arrays.asList("token")), new ArrayList<>(Arrays.asList("productID", receivedParameters.get("productID")))));
            removeProductFromCartLayout.addRow(removeProductFromCartButton, new ArrayList<>(Arrays.asList(4, 4, 4)));
            MetroPanel removeProductFromCartPanel = new MetroPanel("Confirm Removal Of Product From Cart", "warning", 
            removeProductFromCartLayout);
            removeProductFromCartAccordion.addFrame("Confirm Removal Of Product From Cart", "warning", removeProductFromCartPanel, true);
            return json.convertComponentToJSON(removeProductFromCartAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Data", "Invalid data has been supplied to the server");
    }
    @POST
    @Path("/removeProductFromCartConfirmation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String removeProductFromCartConfirmation(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            MetroAccordion removeProductFromCartConfirmationAccordion = new MetroAccordion("removeProductFromCartConfirmationAccordion");
            MetroLayout removeProductFromCartConfirmationLayout = new MetroLayout(0);
            removeProductFromCartConfirmationLayout.addRow(new MetroHeading("The product was successfully removed from your shopping cart", ""));
            removeProductFromCartConfirmationAccordion.addFrame("Successfully Removed Product", "checkmark", 
            removeProductFromCartConfirmationLayout, true);
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("users", userDetails.get(0));
            selectedParameters.put("products", receivedParameters.get("productID"));
            Database database = new Database();
            database.removeTableRow("shoppingcarts", selectedParameters, false);
            return json.convertComponentToJSON(removeProductFromCartConfirmationAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Data", "Invalid data has been supplied to the server");
    }
}
