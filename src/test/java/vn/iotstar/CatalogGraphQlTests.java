package vn.iotstar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import vn.iotstar.repository.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @AutoConfigureGraphQlTester
class CatalogGraphQlTests {
    @Autowired GraphQlTester graphQl;
    @Autowired ProductRepository products; @Autowired CategoryRepository categories; @Autowired UserRepository users;
    @BeforeEach void reset() { products.deleteAll(); categories.deleteAll(); users.deleteAll(); }
    private Long createUser() { return graphQl.document("mutation { createUser(input:{fullname:\"Nguyen Van A\",email:\"a@example.com\",password:\"secret12\",phone:\"0901234567\"}) { id email } }")
            .execute().path("createUser.id").entity(Long.class).get(); }
    private Long createCategory() { return graphQl.document("mutation { createCategory(input:{name:\"Điện tử\",images:\"cat.png\"}) { id name } }")
            .execute().path("createCategory.id").entity(Long.class).get(); }
    private Long createProduct(Long userId, Long categoryId, String title, int quantity, String price) { return graphQl.document("mutation { createProduct(input:{title:\"" + title + "\",quantity:" + quantity + ",desc:\"Mô tả\",price:" + price + ",userId:\"" + userId + "\",categoryId:\"" + categoryId + "\"}) { id } }")
            .execute().path("createProduct.id").entity(Long.class).get(); }
    @Test void createsUserWithoutExposingPasswordAndRejectsDuplicateEmail() {
        graphQl.document("mutation { createUser(input:{fullname:\"Nguyen Van A\",email:\"a@example.com\",password:\"secret12\"}) { id fullname email phone } }").execute()
                .path("createUser.email").entity(String.class).isEqualTo("a@example.com");
        graphQl.document("mutation { createUser(input:{fullname:\"B\",email:\"A@example.com\",password:\"secret12\"}) { id } }").execute()
                .errors().satisfy(errors -> assertThat(errors).anyMatch(e -> e.getExtensions().get("code").equals("CONFLICT")));
    }
    @Test void categoryCrudAssignmentAndProductProtectionWork() {
        Long user = createUser(); Long category = createCategory();
        graphQl.document("mutation { assignUsersToCategory(categoryId:\"" + category + "\",userIds:[\"" + user + "\"]) { users { email } } }").execute()
                .path("assignUsersToCategory.users[0].email").entity(String.class).isEqualTo("a@example.com");
        Long product = createProduct(user, category, "Tai nghe", 2, "300");
        graphQl.document("mutation { deleteCategory(id:\"" + category + "\") }").execute().errors()
                .satisfy(errors -> assertThat(errors).anyMatch(e -> e.getExtensions().get("code").equals("VALIDATION_ERROR")));
        graphQl.document("mutation { deleteProduct(id:\"" + product + "\") }").execute().path("deleteProduct").entity(Boolean.class).isEqualTo(true);
        graphQl.document("mutation { updateCategory(id:\"" + category + "\",input:{name:\"Điện tử mới\"}) { name } }").execute().path("updateCategory.name").entity(String.class).isEqualTo("Điện tử mới");
        graphQl.document("mutation { deleteCategory(id:\"" + category + "\") }").execute().path("deleteCategory").entity(Boolean.class).isEqualTo(true);
    }
    @Test void categoryValidationRejectsBlankAndDuplicateNames() {
        graphQl.document("mutation { createCategory(input:{name:\" \"}) { id } }").execute().errors()
                .satisfy(errors -> assertThat(errors).anyMatch(e -> e.getExtensions().get("code").equals("VALIDATION_ERROR")));
        createCategory();
        graphQl.document("mutation { createCategory(input:{name:\"đIệN Tử\"}) { id } }").execute().errors()
                .satisfy(errors -> assertThat(errors).anyMatch(e -> e.getExtensions().get("code").equals("CONFLICT")));
    }
    @Test void productQueriesAreSortedFilteredAndIncludeRelations() {
        Long user = createUser(); Long category = createCategory();
        createProduct(user, category, "Đắt", 1, "900"); Long cheap = createProduct(user, category, "Rẻ", 3, "100");
        graphQl.document("{ productsByPriceAsc { title price } productsByCategory(categoryId:\"" + category + "\") { title } productById(id:\"" + cheap + "\") { title desc user { email } category { name } } }").execute()
                .path("productsByPriceAsc[0].title").entity(String.class).isEqualTo("Rẻ")
                .path("productsByCategory").entityList(Object.class).hasSize(2)
                .path("productById.user.email").entity(String.class).isEqualTo("a@example.com")
                .path("productById.category.name").entity(String.class).isEqualTo("Điện tử");
    }
    @Test void productValidationUpdateDeleteAndMissingIdWork() {
        Long user = createUser(); Long category = createCategory();
        graphQl.document("mutation { createProduct(input:{title:\" \",quantity:1,price:1,userId:\"" + user + "\",categoryId:\"" + category + "\"}) { id } }").execute().errors()
                .satisfy(errors -> assertThat(errors).anyMatch(e -> e.getExtensions().get("code").equals("VALIDATION_ERROR")));
        graphQl.document("mutation { createProduct(input:{title:\"X\",quantity:-1,price:1,userId:\"" + user + "\",categoryId:\"" + category + "\"}) { id } }").execute().errors().satisfy(errors -> assertThat(errors).isNotEmpty());
        graphQl.document("mutation { createProduct(input:{title:\"X\",quantity:1,price:-1,userId:\"" + user + "\",categoryId:\"" + category + "\"}) { id } }").execute().errors().satisfy(errors -> assertThat(errors).isNotEmpty());
        Long product = createProduct(user, category, "Cũ", 1, "10");
        graphQl.document("mutation { updateProduct(id:\"" + product + "\",input:{title:\"Mới\",price:20}) { title price } }").execute().path("updateProduct.title").entity(String.class).isEqualTo("Mới");
        graphQl.document("mutation { deleteProduct(id:\"999999\") }").execute().errors().satisfy(errors -> assertThat(errors).anyMatch(e -> e.getExtensions().get("code").equals("NOT_FOUND")));
        graphQl.document("{ productById(id:\"999999\") { id } }").execute().path("productById").valueIsNull();
        graphQl.document("mutation { deleteProduct(id:\"" + product + "\") }").execute().path("deleteProduct").entity(Boolean.class).isEqualTo(true);
    }
}
