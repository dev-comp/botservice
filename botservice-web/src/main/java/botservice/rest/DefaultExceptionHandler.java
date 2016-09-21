package botservice.rest;

import botservice.serviceException.ServiceExceptionObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Дефолтный хендлер ошибок
 */

@Provider
public class DefaultExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        return Response.serverError().entity(new ServiceExceptionObject("Ошибка при обращении к севрису", e))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
