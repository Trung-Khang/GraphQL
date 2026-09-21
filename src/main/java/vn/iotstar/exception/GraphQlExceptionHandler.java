package vn.iotstar.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import java.util.Map;

@ControllerAdvice
public class GraphQlExceptionHandler {
    @org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler(NotFoundException.class) GraphQLError notFound(NotFoundException e) { return error(e, "NOT_FOUND"); }
    @org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler(ConflictException.class) GraphQLError conflict(ConflictException e) { return error(e, "CONFLICT"); }
    @org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler(BusinessValidationException.class) GraphQLError validation(BusinessValidationException e) { return error(e, "VALIDATION_ERROR"); }
    private GraphQLError error(RuntimeException e, String code) { return GraphqlErrorBuilder.newError().message(e.getMessage()).extensions(Map.of("code", code)).build(); }
}
