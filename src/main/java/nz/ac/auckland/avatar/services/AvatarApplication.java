package nz.ac.auckland.avatar.services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import nz.ac.auckland.avatar.services.AvatarResource;

import java.util.HashSet;
import java.util.Set;

/**
 * Application subclass for the Avatar Web service.
 * 
 * @author Ian Warren
 *
 */
@ApplicationPath("/services")
public class AvatarApplication extends Application
{
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> classes = new HashSet<Class<?>>();

   public AvatarApplication()
   {
	  // Register the ParoleeResource singleton to handle HTTP requests.
	   AvatarResource resource = new AvatarResource();
      singletons.add(resource);
      
      // Register the ContextResolver class for JAXB.
      classes.add(AvatarResolver.class);
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
   
   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }
}
