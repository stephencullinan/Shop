package Services;
import java.util.Set;
import javax.ws.rs.core.Application;
@javax.ws.rs.ApplicationPath("services")
public class ApplicationConfig extends Application 
{
    @Override
    public Set<Class<?>> getClasses() 
    {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }
    private void addRestResourceClasses(Set<Class<?>> resources) 
    {
        resources.add(Services.Administration.class);
        resources.add(Services.LogOn.class);
        resources.add(Services.Product.class);
        resources.add(Services.Search.class);
        resources.add(Services.Shop.class);
        resources.add(Services.ShoppingCart.class);
        resources.add(Services.UserProfile.class);
    }
}
