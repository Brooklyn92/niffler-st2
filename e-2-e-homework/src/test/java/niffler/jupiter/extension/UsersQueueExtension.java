package niffler.jupiter.extension;

import io.qameta.allure.AllureId;
import niffler.jupiter.annotation.User;
import niffler.jupiter.annotation.User.UserType;
import niffler.model.UserJson;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UsersQueueExtension implements
    BeforeEachCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static Namespace USER_EXTENSION_NAMESPACE = Namespace.create(UsersQueueExtension.class);


  private static Queue<UserJson> USERS_WITH_FRIENDS_QUEUE = new ConcurrentLinkedQueue<>();
  private static Queue<UserJson> USERS_INVITATION_SENT_QUEUE = new ConcurrentLinkedQueue<>();
  private static Queue<UserJson> USERS_INVITATION_RECEIVED_QUEUE = new ConcurrentLinkedQueue<>();

  static {
    USERS_WITH_FRIENDS_QUEUE.addAll(
        List.of(userJson("Aleksandr", "12345"), userJson("Miami", "12345"))
    );
    USERS_INVITATION_SENT_QUEUE.addAll(
        List.of(userJson("Arizona", "12345"), userJson("Florida", "12345"))
    );
    USERS_INVITATION_RECEIVED_QUEUE.addAll(
        List.of(userJson("Ford", "12345"), userJson("Texas", "12345"))
    );
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    final String testId = getTestId(context);
    Parameter[] testParameters = context.getRequiredTestMethod().getParameters(); // получаем массив параметров, которые указаны в тесте
    for (Parameter parameter : testParameters) {
      User desiredUser = parameter.getAnnotation(User.class);
      if (desiredUser != null) {
        UserType userType = desiredUser.userType();

        UserJson user = null;
        while (user == null) {
          switch (userType) {
            case WITH_FRIENDS -> user = USERS_WITH_FRIENDS_QUEUE.poll();
            case INVITATION_SENT -> user = USERS_INVITATION_SENT_QUEUE.poll();
            case INVITATION_RECEIVED -> user = USERS_INVITATION_RECEIVED_QUEUE.poll();
          }
        }

        context.getStore(USER_EXTENSION_NAMESPACE).put(testId, Map.of(userType, user));
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    final String testId = getTestId(context);
    Map<UserType, UserJson> user = (Map<UserType, UserJson>) context.getStore(USER_EXTENSION_NAMESPACE)
        .get(testId);

    UserType userType = user.keySet().iterator().next();
    switch (userType) {
      case WITH_FRIENDS -> USERS_WITH_FRIENDS_QUEUE.add(user.get(userType));
      case INVITATION_SENT -> USERS_INVITATION_SENT_QUEUE.add(user.get(userType));
      case INVITATION_RECEIVED -> USERS_INVITATION_RECEIVED_QUEUE.add(user.get(userType));
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().isAnnotationPresent(User.class) &&
        parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public UserJson resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    final String testId = getTestId(extensionContext);
    Map<UserType, UserJson> user = (Map<UserType, UserJson>) extensionContext.getStore(USER_EXTENSION_NAMESPACE)
        .get(testId);

    return user.values().iterator().next();
  }

  //Получение id теста, прописанный в аннотации AllureId
  private String getTestId(ExtensionContext context) {
    return Objects
        .requireNonNull(context.getRequiredTestMethod().getAnnotation(AllureId.class))
        .value();
  }

  private static UserJson userJson(String userName, String password) {
    UserJson user = new UserJson();
    user.setUsername(userName);
    user.setPassword(password);
    return user;
  }
}
