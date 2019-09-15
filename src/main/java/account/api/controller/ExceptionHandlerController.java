package account.api.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
@Produces
@Singleton
@Requires(classes = {ExceptionHandler.class})
public class ExceptionHandlerController implements ExceptionHandler<RuntimeException, HttpResponse> {
    @Override
    public HttpResponse<String> handle(HttpRequest request, RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return HttpResponse.notFound(exception.getMessage());
    }
}
