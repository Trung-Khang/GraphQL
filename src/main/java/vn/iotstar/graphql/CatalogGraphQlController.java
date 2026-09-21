package vn.iotstar.graphql;

import vn.iotstar.dto.input.*;
import vn.iotstar.entity.*;
import vn.iotstar.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.*;

@Controller @RequiredArgsConstructor
public class CatalogGraphQlController {
    private final CatalogService service;
    @QueryMapping List<Product> products() { return service.products(); }
    @QueryMapping Product productById(@Argument Long id) { return service.product(id); }
    @QueryMapping List<Product> productsByPriceAsc() { return service.productsByPrice(); }
    @QueryMapping List<Product> productsByCategory(@Argument Long categoryId) { return service.productsByCategory(categoryId); }
    @QueryMapping List<Category> categories() { return service.categories(); }
    @QueryMapping Category categoryById(@Argument Long id) { return service.category(id); }
    @QueryMapping List<User> users() { return service.users(); }
    @QueryMapping User userById(@Argument Long id) { return service.user(id); }
    @MutationMapping Category createCategory(@Argument CreateCategoryInput input) { return service.createCategory(input); }
    @MutationMapping Category updateCategory(@Argument Long id, @Argument UpdateCategoryInput input) { return service.updateCategory(id, input); }
    @MutationMapping boolean deleteCategory(@Argument Long id) { return service.deleteCategory(id); }
    @MutationMapping Product createProduct(@Argument CreateProductInput input) { return service.createProduct(input); }
    @MutationMapping Product updateProduct(@Argument Long id, @Argument UpdateProductInput input) { return service.updateProduct(id, input); }
    @MutationMapping boolean deleteProduct(@Argument Long id) { return service.deleteProduct(id); }
    @MutationMapping User createUser(@Argument CreateUserInput input) { return service.createUser(input); }
    @MutationMapping Category assignUsersToCategory(@Argument Long categoryId, @Argument List<Long> userIds) { return service.assignUsers(categoryId, userIds); }
    @SchemaMapping String desc(Product product) { return product.getDescription(); }
    @SchemaMapping List<User> users(Category category) { return new ArrayList<>(category.getUsers()); }
    @SchemaMapping List<Product> products(Category category) { return service.productsByCategory(category.getId()); }
    @SchemaMapping List<Category> categories(User user) { return new ArrayList<>(user.getCategories()); }
    @SchemaMapping List<Product> products(User user) { return service.products().stream().filter(p -> p.getUser().getId().equals(user.getId())).toList(); }
}
