package Stock;
import Database.*;
import java.util.*;
public class Stock 
{
    public Stock()
    {
        
    }
    public Integer getStockLevel(String productID, String userID)
    {
        Integer currentStockLevel = 0;
        Integer currentStockInShoppingCarts = 0;
        Database database = new Database();
        HashMap<String, String> selectedParameters = new HashMap<>();
        selectedParameters.put("code", productID);
        List<List<String>> productDetails = database.getTableRows("products", selectedParameters);
        if(productDetails.size() > 0)
            currentStockLevel = Integer.parseInt(productDetails.get(0).get(3));
        HashMap<String, String> filteredParameters = new HashMap();
        filteredParameters.put("products", productID);
        List<List<String>> productInCarts = database.getTableRows("shoppingcarts", filteredParameters, 
        new ArrayList<>(Collections.singletonList("quantity")));
        currentStockInShoppingCarts = productInCarts.stream().map((aProduct) -> 
        {
            return Integer.parseInt(aProduct.get(0));
        }).reduce(currentStockInShoppingCarts, Integer::sum);
        filteredParameters.put("users", userID);
        List<List<String>> ownProductInCarts = database.getTableRows("shoppingcarts", filteredParameters, 
        new ArrayList<>(Collections.singletonList("quantity")));
        currentStockInShoppingCarts = ownProductInCarts.stream().map((anOwnProduct) -> 
        Integer.parseInt(anOwnProduct.get(0))).reduce(currentStockInShoppingCarts, (accumulator, _item) -> accumulator - _item);
        return currentStockLevel - currentStockInShoppingCarts;
    }
}
