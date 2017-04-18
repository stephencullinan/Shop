package Services;
import MetroComponents.*;
import Utilities.*;
import java.util.*;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import Database.Database;
import Stock.Stock;
@Path("ShoppingCart")
public class ShoppingCart 
{
    private final Database database;
    public ShoppingCart() 
    {
        database = new Database();
    }
    @POST
    @Path("/getShoppingCart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getShoppingCart(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            MetroLayout shoppingCartLayout = new MetroLayout(0);
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("shoppingcarts.users", userDetails.get(0));
            List<List<String>> shoppingCart = database.getJoinedTableRows(new ArrayList<>(Arrays.asList("shoppingcarts", "products")), 
            new ArrayList<>(Arrays.asList("shoppingcarts.products", "products.code")), selectedParameters, 
            new ArrayList<>(Arrays.asList("products.code", "products.title", "products.description", "products.quantity", "products.price", 
            "shoppingcarts.quantity")));
            List<List<String>> formattedItems = new ArrayList<>();
            List<String> onClickEvents = new ArrayList<>();
            shoppingCart.stream().map((aShoppingCart) -> 
            {
                List<String> formattedListItem = new ArrayList<>();
                formattedListItem.add(aShoppingCart.get(1));
                formattedListItem.add(aShoppingCart.get(5));
                formattedListItem.add(aShoppingCart.get(4));
                onClickEvents.add(javascript.updateContent("ShoppingCart/adjustStockQuantity", "shopUpdatePanel", 
                new ArrayList<>(Collections.singletonList("token")), new ArrayList<>(Arrays.asList("productID", aShoppingCart.get(0), 
                "productTitle", aShoppingCart.get(1), "productQuantity", aShoppingCart.get(5)))));
                return formattedListItem;
            }).forEachOrdered((formattedListItem) -> 
            {
                formattedItems.add(formattedListItem);
            });
            MetroDataTable shoppingCartTable = new MetroDataTable("shoppingCartTable", new ArrayList<>(Arrays.asList("Product Title", 
            "Product Quantity", "Product Price")), formattedItems, onClickEvents);
            MetroCommandButton checkOutButton = new MetroCommandButton("Check Out", "Proceed To Step 2", "checkmark", "success", 
                                                                       javascript.updateContent("ShoppingCart/getShoppingCartAddress", 
                                                                       "shopUpdatePanel"));
            MetroCommandButton cancelButton = new MetroCommandButton("Empty", "Empty The Basket", "bin", "danger", 
            javascript.updateContent("ShoppingCart/emptyCart", "shopUpdatePanel"));
            shoppingCartLayout.addRow(new MetroProgressBar(25));
            shoppingCartLayout.addRow(new MetroHeading("Shopping Cart", "Step 1"));
            shoppingCartLayout.addEmptyRows(2);
            shoppingCartLayout.addRow(new MetroPopover("Adjust the quantity of an item by selecting the item", "bottom"), 
            new ArrayList<>(Arrays.asList(2, 8, 2)));
            shoppingCartLayout.addEmptyRows(2);
            shoppingCartLayout.addRow(shoppingCartTable);
            shoppingCartLayout.addRow(new ArrayList<>(Arrays.asList(checkOutButton, cancelButton)), 
            new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
            shoppingCartLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion shoppingCartAccordion = new MetroAccordion("shoppingCartAccordion");
            shoppingCartAccordion.addFrame("Your Shopping Cart", "shopping-basket", shoppingCartLayout, true);
            return json.convertComponentToJSON(shoppingCartAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/emptyCart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String emptyCart(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            HashMap<String, String> selectedParameters = new HashMap();
            selectedParameters.put("users", userDetails.get(0));
            database.removeTableRow("shoppingcarts", selectedParameters);
            new Log().addLog(userDetails.get(0), "Order Has Been Cancelled");
            return getShoppingCart(content);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/getShoppingCartAddress")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getShoppingCartAddress(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            HashMap<String, String> filteredParameters = new HashMap();
            filteredParameters.put("users", userDetails.get(0));
            List<List<String>> tableContents = database.getTableRows("shoppingcarts", filteredParameters);
            if(tableContents.isEmpty())
                return json.convertErrorNotificationToJSON("Cart Is Empty", "You do not have any products in the basket");
            MetroCommandButton backButton = new MetroCommandButton("Return", "Return To Step 1", "exit", "danger", 
                                            javascript.updateContent("ShoppingCart/getShoppingCart", "shopUpdatePanel"));
            MetroListView selectedAddress = new MetroListView("selectedAddress", "Available Addresses");
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("users", userDetails.get(0));
            List<List<String>> existingAddresses = database.getTableRows("addresses", selectedParameters);
            existingAddresses.forEach(x -> selectedAddress.addListItem("", x.get(6), x.get(4) + " " + x.get(5), x.get(2) + " " + x.get(3), 
            x.get(0)));
            MetroPanel availableAddressesPanel = new MetroPanel("Available Addresses", "map2", selectedAddress);
            MetroCommandButton addNewAddressButton = new MetroCommandButton("Add", "Add Your Address", "checkmark", "success",
                                                     javascript.updateContent("ShoppingCart/addNewAddress", "shopUpdatePanel"));
            MetroCommandButton saveNewAddressButton = new MetroCommandButton("Proceed", "Proceed To Step 3", "checkmark", "success",
                                                      javascript.updateContent("ShoppingCart/getShoppingCartPayment", "shopUpdatePanel",
                                                      new ArrayList<>(Arrays.asList("token", "selectedAddress"))));
            MetroLayout shoppingCartAddressLayout = new MetroLayout(2);
            shoppingCartAddressLayout.addRow(backButton, new ArrayList<>(Arrays.asList(1, 3, 8)));
            shoppingCartAddressLayout.addRow(new MetroProgressBar(50));
            shoppingCartAddressLayout.addRow(new MetroHeading("Shopping Cart", "Step 2"));
            shoppingCartAddressLayout.addRow(addNewAddressButton, new ArrayList<>(Arrays.asList(4, 4, 4)));
            if(existingAddresses.size() > 0)
            {
                shoppingCartAddressLayout.addRow(new MetroPopover("Click on an address in the list to select that address", "bottom"),
                new ArrayList<>(Arrays.asList(3, 6, 3)));
                shoppingCartAddressLayout.addRow(availableAddressesPanel);
            }
            else
            {
                MetroHeading noAddressesHeading = new MetroHeading("There are no addresses available", "");
                MetroPanel noAddressesPanel = new MetroPanel("There are no addresses available", "warning", noAddressesHeading);
                shoppingCartAddressLayout.addRow(noAddressesPanel);
            }
            shoppingCartAddressLayout.addRow(saveNewAddressButton, new ArrayList<>(Arrays.asList(4, 4, 4)));
            shoppingCartAddressLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion availableAddressesAccordion = new MetroAccordion("availableAddressesAccordion");
            availableAddressesAccordion.addFrame("Available Addresses", "map2", shoppingCartAddressLayout, true);
            return json.convertComponentToJSON(availableAddressesAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/getShoppingCartPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getShoppingCartPayment(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            Integer selectedAddressIdentifier = 0;
            if(receivedParameters.get("selectedAddress").length() == 0)
                return json.convertErrorNotificationToJSON("Invalid Address", "Please select an address from the list");
            try
            {
                selectedAddressIdentifier = Integer.parseInt(receivedParameters.get("selectedAddress"));
            }
            catch(NumberFormatException error)
            {
                System.out.println(error);
                return json.convertErrorNotificationToJSON("Invalid Address", "Please select an address from the list");
            }
            if(selectedAddressIdentifier < 1)
                return json.convertErrorNotificationToJSON("Invalid Address", "Please select an address from the list");
            MetroLayout availablePaymentMethodsLayout = new MetroLayout(2);
            MetroListView availablePaymentMethods = new MetroListView("availablePaymentMethods", "Available Payment Methods");
            MetroCommandButton backButton = new MetroCommandButton("Return", "Return To Step 2", "exit", "danger", 
            javascript.updateContent("ShoppingCart/getShoppingCartAddress", "shopUpdatePanel"));
            MetroCommandButton addAvailablePaymentButton = new MetroCommandButton("Add", "Add A Payment Method", "eur", "success",
            javascript.updateContent("ShoppingCart/addNewPaymentMethod", "shopUpdatePanel", 
            new ArrayList<>(Collections.singletonList("token")), new ArrayList<>(Arrays.asList("selectedAddress", 
            receivedParameters.get("selectedAddress")))));
            MetroCommandButton confirmOrderButton = new MetroCommandButton("Confirm", "Confirm Your Order", "checkmark", "success",
            javascript.updateContent("ShoppingCart/confirmOrder", "shopUpdatePanel", 
            new ArrayList<>(Arrays.asList("token", "availablePaymentMethods")), new ArrayList<>(Arrays.asList("selectedAddress", 
            receivedParameters.get("selectedAddress")))));
            HashMap<String, String> selectedParameters = new HashMap<>();
            selectedParameters.put("users", userDetails.get(0));
            List<List<String>> availablePayments = database.getTableRows("paymentmethods", selectedParameters);
            for(List<String> anAvailablePayment: availablePayments)
            {
                availablePaymentMethods.addListItem("", anAvailablePayment.get(2), anAvailablePayment.get(3), anAvailablePayment.get(4), 
                anAvailablePayment.get(0));
            }
            availablePaymentMethodsLayout.addRow(backButton, new ArrayList<>(Arrays.asList(1, 3, 8)));
            availablePaymentMethodsLayout.addRow(new MetroProgressBar(75));
            availablePaymentMethodsLayout.addRow(new MetroHeading("Shopping Cart", "Step 3"));
            availablePaymentMethodsLayout.addRow(addAvailablePaymentButton, new ArrayList<>(Arrays.asList(4, 4, 4)));
            if(availablePayments.size() > 0)
                availablePaymentMethodsLayout.addRow(availablePaymentMethods);
            else
            {
                MetroHeading noPaymentMethodsHeading = new MetroHeading("There are no payment methods available", "");
                MetroPanel noPaymentMethodsPanel = new MetroPanel("There are no payment methods available", "warning", noPaymentMethodsHeading);
                availablePaymentMethodsLayout.addRow(noPaymentMethodsPanel);
            }
            availablePaymentMethodsLayout.addRow(confirmOrderButton, new ArrayList<>(Arrays.asList(4, 4, 4)));
            availablePaymentMethodsLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion availablePaymentMethodsAccordion = new MetroAccordion("availablePaymentMethodsAccordion");
            availablePaymentMethodsAccordion.addFrame("Available Payment Methods", "eur", availablePaymentMethodsLayout, true);
            return json.convertComponentToJSON(availablePaymentMethodsAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/addNewPaymentMethod")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addNewPaymentMethod(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            MetroTextField creditCardNumber = new MetroTextField("creditCardNumber", "Please enter your credit card number", "credit-card", 
            "text", "");
            MetroTextField creditCardExpiryDate = new MetroTextField("creditCardExpiryDate", "Please enter your credit card expiry date as " + 
            "yyyy-MM-dd", "credit-card", "text", "");
            MetroTextField creditCardSecurityCode = new MetroTextField("creditCardSecurityCode", "Please enter your credit card security code",
            "credit-card", "text", "");
            MetroPanel creditCardNumberPanel = new MetroPanel("Please enter your credit card number", "credit-card", creditCardNumber);
            MetroPanel creditCardExpiryDatePanel = new MetroPanel("Please enter your credit card expiry date", "credit-card", 
                                                   creditCardExpiryDate);
            MetroPanel creditCardSecurityCodePanel = new MetroPanel("Please enter your credit card security code", "credit-card", 
                                                     creditCardSecurityCode);
            MetroCommandButton addPaymentMethodButton = new MetroCommandButton("Add", "Add Your Payment Method", "checkmark", "success",
            javascript.updateContent("ShoppingCart/saveNewPaymentMethod", "shopUpdatePanel", 
            new ArrayList<>(Arrays.asList("creditCardNumber", "creditCardExpiryDate",
            "creditCardSecurityCode", "token")), new ArrayList<>(Arrays.asList("selectedAddress", receivedParameters.get("selectedAddress")))));
            MetroCommandButton cancelButton = new MetroCommandButton("Return", "Return To Shopping Cart", "exit", "danger", 
            new JavaScript().updateContent("ShoppingCart/getShoppingCartPayment", "shopUpdatePanel", new ArrayList<>(Arrays.asList("token")), 
            new ArrayList<>(Arrays.asList("selectedAddress", receivedParameters.get("selectedAddress")))));
            MetroLayout addNewPaymentMethodLayout = new MetroLayout(2);
            addNewPaymentMethodLayout.addRow(creditCardNumberPanel);
            addNewPaymentMethodLayout.addRow(creditCardExpiryDatePanel);
            addNewPaymentMethodLayout.addRow(creditCardSecurityCodePanel);
            addNewPaymentMethodLayout.addRow(new ArrayList<>(Arrays.asList(addPaymentMethodButton, cancelButton)), 
            new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
            addNewPaymentMethodLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion addNewPaymentMethodAccordion = new MetroAccordion("addNewPaymentMethodAccordion");
            addNewPaymentMethodAccordion.addFrame("Add Your Payment Method", "credit-card", addNewPaymentMethodLayout, true);
            return json.convertComponentToJSON(addNewPaymentMethodAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/saveNewPaymentMethod")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String saveNewPaymentMethod(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            if(receivedParameters.get("creditCardNumber").length() < 16)
                return json.convertErrorNotificationToJSON("Invalid Credit Card Number", "A valid credit card number should consist of 16 digits");
            if(receivedParameters.get("creditCardExpiryDate").length() < 8)
                return json.convertErrorNotificationToJSON("Invalid Credit Card Expiry Date", "A valid credit card expiry date should consist of "
                + "8 digits");
            if(receivedParameters.get("creditCardSecurityCode").length() < 3)
                return json.convertErrorNotificationToJSON("Invalid Credit Card Security Code", "A valid credit card security code should consist " 
                + "of 3 digits");
            Double maxValue = database.getMaxValueOfColumn("paymentmethods", "code") + 1;
            database.insertTableRow("paymentmethods", new ArrayList<>(Arrays.asList(maxValue + "", userDetails.get(0), 
            receivedParameters.get("creditCardNumber"), receivedParameters.get("creditCardExpiryDate"), 
            receivedParameters.get("creditCardSecurityCode"))));
            return getShoppingCartPayment(content);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/addNewAddress")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addNewAddress(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidUserToken(receivedParameters.get("token")))
        {
            MetroLayout addNewAddressLayout = new MetroLayout(2);
            MetroTextField firstLine = new MetroTextField("firstLine", "Please enter the first line of the address", "map2", "text", "");
            MetroPanel firstLinePanel = new MetroPanel("Please enter the first line of the address", "map2", firstLine);
            MetroTextField secondLine = new MetroTextField("secondLine", "Please enter the second line of the address", "map2", "text", "");
            MetroPanel secondLinePanel = new MetroPanel("Please enter the second line of the address", "map2", secondLine);
            MetroTextField town = new MetroTextField("town", "Please enter the town of the address", "map2", "text", "");
            MetroPanel townPanel = new MetroPanel("Please enter the town of the address", "map2", town);
            MetroTextField county = new MetroTextField("county", "Please enter the county of the address", "map2", "text", "");
            MetroPanel countyPanel = new MetroPanel("Please enter the county of the address", "map2", county);
            MetroTextField country = new MetroTextField("country", "Please enter the country of the address", "map2", "text", "");
            MetroPanel countryPanel = new MetroPanel("Please enter the country of the address", "map2", country);
            MetroCommandButton addNewAddressSubmitButton = new MetroCommandButton("Add", "Add Your Address", "checkmark", "success", 
                                                           javascript.updateContent("ShoppingCart/saveNewAddress", "shopUpdatePanel", 
                                                           new ArrayList<>(Arrays.asList("firstLine", "secondLine", "town", "county", "country",
                                                           "token"))));
            MetroCommandButton cancelButton = new MetroCommandButton("Return", "Return To Shopping Cart", "exit", "danger",
            javascript.updateContent("ShoppingCart/getShoppingCartAddress", "shopUpdatePanel"));
            addNewAddressLayout.addRow(new MetroProgressBar(50));
            addNewAddressLayout.addRow(new MetroHeading("Add New Address", ""));
            addNewAddressLayout.addRow(firstLinePanel);
            addNewAddressLayout.addRow(secondLinePanel);
            addNewAddressLayout.addRow(townPanel);
            addNewAddressLayout.addRow(countyPanel);
            addNewAddressLayout.addRow(countryPanel);
            addNewAddressLayout.addRow(new ArrayList<>(Arrays.asList(addNewAddressSubmitButton, cancelButton)), 
            new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
            addNewAddressLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            return json.convertComponentToJSON(addNewAddressLayout);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/saveNewAddress")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String saveNewAddress(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            if(receivedParameters.get("firstLine").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid First Line", "A valid first line should contain at least 2 characters");
            else if(receivedParameters.get("secondLine").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Second Line", "A valid second line should contain at least 2 characters");
            else if(receivedParameters.get("town").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Town", "A valid town should contain at least 2 characters");
            else if(receivedParameters.get("county").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid County", "A valid county should contain at least 2 characters");
            else if(receivedParameters.get("country").length() < 2)
                return json.convertErrorNotificationToJSON("Invalid Country", "A valid country should contain at least 2 characters");
            Double maxValue = database.getMaxValueOfColumn("addresses", "code") + 1;
            database.insertTableRow("addresses", new ArrayList<>(Arrays.asList(maxValue + "", userDetails.get(0), 
            receivedParameters.get("firstLine"), receivedParameters.get("secondLine"), receivedParameters.get("town"), 
            receivedParameters.get("county"), receivedParameters.get("country"))));
            return getShoppingCartAddress(content);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/adjustStockQuantity")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String adjustStockQuantity(String content)
    {
        JSON json = new JSON();
        JavaScript javascript = new JavaScript();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        if(new Token().isValidUserToken(receivedParameters.get("token")))
        {
            MetroLayout adjustStockQuantityLayout = new MetroLayout(2);
            MetroTextField adjustStockQuantity = new MetroTextField("adjustStockQuantity", "Please enter the quantity of the product titled " +
            receivedParameters.get("productTitle"), "shopping-basket", "text", receivedParameters.get("productQuantity"));
            MetroPanel adjustStockQuantityPanel = new MetroPanel("Please enter the quantity of the product titled " + 
            receivedParameters.get("productTitle"), "shopping-basket", adjustStockQuantity);
            MetroCommandButton adjustStockQuantitySubmitButton = new MetroCommandButton("Adjust", "Adjust Stock Quantity", "checkmark", "success",
            javascript.updateContent("ShoppingCart/saveStockQuantity", "shopUpdatePanel", 
            new ArrayList<>(Arrays.asList("adjustStockQuantity", "token")), 
            new ArrayList<>(Arrays.asList("productID", receivedParameters.get("productID")))));
            MetroCommandButton cancelButton = new MetroCommandButton("Return", "Return To Shopping Cart", "exit", "danger", 
            javascript.updateContent("ShoppingCart/getShoppingCart", "shopUpdatePanel"));
            adjustStockQuantityLayout.addRow(new MetroProgressBar(25));
            adjustStockQuantityLayout.addRow(adjustStockQuantityPanel);
            adjustStockQuantityLayout.addRow(new ArrayList<>(Arrays.asList(adjustStockQuantitySubmitButton, cancelButton)), 
            new ArrayList<>(Arrays.asList(1, 4, 1, 1, 4, 1)));
            adjustStockQuantityLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            MetroAccordion adjustStockQuantityAccordion = new MetroAccordion("adjustStockQuantityAccordion");
            adjustStockQuantityAccordion.addFrame("Adjust Stock Quantity", "shopping-basket", adjustStockQuantityLayout, true);
            return json.convertComponentToJSON(adjustStockQuantityAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/saveStockQuantity")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String saveStockQuantity(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            Integer adjustStockQuantity = new Validation().validatePositiveInteger(receivedParameters.get("adjustStockQuantity"));
            if(adjustStockQuantity == -1)
                return json.convertErrorNotificationToJSON("Invalid Quantity", "A valid quantity should be a positive integer value");
            Integer currentStockLevel = new Stock().getStockLevel(receivedParameters.get("productID"), userDetails.get(0));
            if(adjustStockQuantity > currentStockLevel)
                return json.convertErrorNotificationToJSON("Invalid Quantity", "There are currently only " + currentStockLevel + 
                        " items left in stock");
            if(adjustStockQuantity == 0)
            {
                HashMap<String, String> deletionParameters = new HashMap();
                deletionParameters.put("users", userDetails.get(0));
                deletionParameters.put("products", receivedParameters.get("productID"));
                database.removeTableRow("shoppingcarts", deletionParameters);
            }
            HashMap<String, String> updatedParameters = new HashMap<>();
            updatedParameters.put("quantity", adjustStockQuantity.toString());
            HashMap<String, String> filteredParameters = new HashMap<>();
            filteredParameters.put("users", userDetails.get(0));
            filteredParameters.put("products", receivedParameters.get("productID"));
            database.updateTableRow("shoppingcarts", updatedParameters, filteredParameters, false);
            return getShoppingCart(content);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
    @POST
    @Path("/confirmOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String confirmOrder(String content)
    {
        JSON json = new JSON();
        HashMap<String, String> receivedParameters = json.convertJSONToHashMap(content);
        List<String> userDetails = new Token().getUserDetailsFromValidUserToken(receivedParameters.get("token"));
        if(userDetails.size() > 0)
        {
            if(receivedParameters.get("availablePaymentMethods").length() == 0)
                return json.convertErrorNotificationToJSON("Invalid Payment Method", "You have not selected a valid payment method");
            Integer possiblePaymentMethod = new Validation().validatePositiveInteger(receivedParameters.get("availablePaymentMethods"));
            if(possiblePaymentMethod == -1)
                return json.convertErrorNotificationToJSON("Invalid Payment Method", "You have not selected a valid payment method");
            Double purchasesOrdersID = database.getMaxValueOfColumn("purchasesorders", "code") + 1;
            database.insertTableRow("purchasesorders", new ArrayList(Arrays.asList(purchasesOrdersID + "", userDetails.get(0), 
            receivedParameters.get("selectedAddress"), receivedParameters.get("availablePaymentMethods"))));
            HashMap<String, String> selectedParameters = new HashMap();
            selectedParameters.put("users", userDetails.get(0));
            List<List<String>> selectedItems = database.getTableRows("shoppingcarts", selectedParameters);
            List<String> orderTableHeadings = new ArrayList(Arrays.asList("Product ID", "Product Title", "Product Quantity"));
            List<List<String>> orderTableContents = new ArrayList();
            for(List<String> aSelectedItem: selectedItems)
            {
                database.insertTableRow("purchasesordersproducts", new ArrayList<>(Arrays.asList(purchasesOrdersID + "", aSelectedItem.get(1),
                aSelectedItem.get(2))));
                HashMap<String, String> filteredParameters = new HashMap();
                filteredParameters.put("code", aSelectedItem.get(1));
                List<List<String>> productDetails = database.getTableRows("products", filteredParameters);
                int updatedQuantity = Integer.parseInt(productDetails.get(0).get(3)) - Integer.parseInt(aSelectedItem.get(2));
                if(updatedQuantity < 0)
                    return json.convertErrorNotificationToJSON("Invalid Stock Quantity", "Your order exceeds the available quantity");
                HashMap<String, String> updatedParameters = new HashMap();
                updatedParameters.put("quantity", updatedQuantity + "");
                HashMap<String, String> restrictiveParameters = new HashMap();
                restrictiveParameters.put("code", aSelectedItem.get(1));
                database.updateTableRow("products", updatedParameters, restrictiveParameters);
                orderTableContents.add(new ArrayList(Arrays.asList(aSelectedItem.get(1), productDetails.get(0).get(1), aSelectedItem.get(2))));
                HashMap<String, String> deletionParameters = new HashMap();
                deletionParameters.put("users", userDetails.get(0));
                deletionParameters.put("products", aSelectedItem.get(1));
                database.removeTableRow("shoppingcarts", deletionParameters);
            }
            MetroDataTable orderConfirmationTable = new MetroDataTable("orderConfirmationTable", orderTableHeadings, orderTableContents, 
            new ArrayList());
            MetroAccordion orderConfirmationAccordion = new MetroAccordion("orderConfirmationAccordion");
            MetroLayout orderConfirmationLayout = new MetroLayout(0);
            orderConfirmationLayout.addRow(new MetroHeading("Your order has been processed successfully" ,"Step 4"));
            orderConfirmationLayout.addRow(orderConfirmationTable);
            orderConfirmationLayout.addRow(new MetroHiddenInput("token", receivedParameters.get("token")));
            orderConfirmationAccordion.addFrame("Your Order " + purchasesOrdersID.intValue(), "shopping-basket", 
            orderConfirmationLayout, true);
            new Log().addLog(userDetails.get(0), "Confirmed Order");
            return json.convertComponentToJSON(orderConfirmationAccordion);
        }
        return json.convertErrorNotificationToJSON("Invalid Token", "The supplied token is not a valid user token");
    }
}