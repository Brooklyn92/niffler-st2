package niffler.jupiter.extension;

import niffler.api.CategoryService;
import niffler.jupiter.annotation.GenerateCategory;
import niffler.model.CategoryJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.*;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GenerateCategoryExtension implements ParameterResolver, BeforeEachCallback {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
            .create(GenerateCategoryExtension.class);

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .build();

    private static final Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl("http://127.0.0.1:8093")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final CategoryService categoryService = retrofit.create(CategoryService.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        GenerateCategory generateCategoryAnnotation = context.getRequiredTestMethod()
                .getAnnotation(GenerateCategory.class);
        if (generateCategoryAnnotation != null) {
            CategoryJson category = new CategoryJson();
            category.setCategory(generateCategoryAnnotation.category());
            category.setUsername(generateCategoryAnnotation.username());

            CategoryJson createdCategory = categoryService.addCategory(category)
                    .execute()
                    .body();

            context.getStore(NAMESPACE).put("category", createdCategory);

        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get("category", CategoryJson.class);
    }
}
