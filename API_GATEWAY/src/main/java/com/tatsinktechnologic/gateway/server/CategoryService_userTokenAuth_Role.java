package com.tatsinktechnologic.gateway.server;

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.tatsinktechnologic.resfull.server;
//
//import com.olivier.resfull.server.beans.Category;
//import com.olivier.resfull.server.beans.Role;
//import com.olivier.resfull.server.dao.CategoryDAO;
//import java.security.Principal;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.SecurityContext;
//import org.apache.log4j.Logger;
//
///**
// *
// * @author olivier.tatsinkou
// */
//@Path("/categoryservice")
//@Produces(MediaType.APPLICATION_XML)
//public class CategoryService_userTokenAuth_Role {
//    private CategoryDAO categoryDAO = new CategoryDAO() ;
//    private static Logger logger = Logger.getLogger(CategoryService_userTokenAuth_Role.class);
//    
//    @Context
//    SecurityContext securityContext;
//    
//    public CategoryDAO getCategoryDAO() {
//        return categoryDAO;
//    }
//    
////Wired using Spring
//    
//    public void setCategoryDAO(CategoryDAO categoryDAO) {
//        this.categoryDAO = categoryDAO;
//    }
//
//    @GET
//    @Path("/category/{id}")
//    @Secured_tokenAuth_Role({Role.ROLE_1})
//    public Category getCategory(@PathParam("id") String id,@Context SecurityContext securityContext) {
//        Principal principal = securityContext.getUserPrincipal();
//        String username = principal.getName();
//        logger.info("current user is : "+ username);
//        logger.info("getCategory called with category id: "+ id);
//        Category cat = (Category) getCategoryDAO().getCategory(id);
//        return cat;
//    }
//    
//    @POST
//    @Path("/category")
//    @Consumes("application/xml")
//    public Category addCategory(Category category,@Context SecurityContext securityContext) {
//        Principal principal = securityContext.getUserPrincipal();
//        String username = principal.getName();
//        logger.info("current user is : "+ username);
//        
//        System.out.println("addCategory called");
//        Category cat = (Category) getCategoryDAO().getCategory(category.getCategoryId());
//        return cat;
//    }
//    
//    @DELETE
//    @Path("/category/{id}")
//    public void deleteCategory(@PathParam("id") String id,@Context SecurityContext securityContext) {
//        Principal principal = securityContext.getUserPrincipal();
//        String username = principal.getName();
//        logger.info("current user is : "+ username);
//        System.out.println("deleteCategory with category id : " +id);
//        getCategoryDAO().deleteCategory(id);
//    }
//    
//    @PUT
//    @Path("/category")
//    public void updateCategory(Category category,@Context SecurityContext securityContext) {
//         Principal principal = securityContext.getUserPrincipal();
//        String username = principal.getName();
//        logger.info("current user is : "+ username);
//        System.out.println("updateCategory with category id : "+ category.getCategoryId());
//        getCategoryDAO().updateCategory(category);
//    }
//    
//    @POST
//    @Path("/category/book")
//    @Consumes("application/xml")
//    public void addBooks(Category category,@Context SecurityContext securityContext) {
//         Principal principal = securityContext.getUserPrincipal();
//        String username = principal.getName();
//        logger.info("current user is : "+ username);
//        System.out.println("addBooks with category id : "+ category.getCategoryId());
//        Category cat = (Category) getCategoryDAO().getCategory(category.getCategoryId());
//        getCategoryDAO().addBook(category);
//    }
//    
//    @Secured_tokenAuth_Role({Role.ROLE_1, Role.ROLE_2})
//    @GET
//    @Path("/category/{id}/books/{userName}")
//    @Consumes("application/xml")
//    public Category getBooks(@PathParam("id") String id,@PathParam("userName") String userName,@Context SecurityContext securityContext) {
//
//        System.out.println("getBooks called with category id : "+id);
//        Category cat = (Category) getCategoryDAO().getCategory(id);
//        cat.setBooks(getCategoryDAO().getBooks(id));
//        return cat;
//    }
//}
