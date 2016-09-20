package botservice.rest;

import botservice.model.system.UserKeyEntity;
import botservice.rest.model.UserObject;
import botservice.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * API сервиса
 */

@RequestScoped
@Path("api")
public class ServiceAPI {

    @Inject
    ClientService clientService;

    /**
     * @return список ключей пользователей
     */
    @PermitAll
    @GET
    @Path("/userKeyList/{clientName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHotelsList(@PathParam("clientName") String clientName) throws JsonProcessingException {
        List<UserKeyEntity> userKeyEntitiesList = clientService.getUserKeyListByClientName(clientName);
        List<UserObject> userObjectList = new ArrayList<>();
        for (UserKeyEntity userKeyEntity: userKeyEntitiesList){
            UserObject userObject = new UserObject();
            userObject.setUserName(userKeyEntity.getUserName());
            userObject.setBotEntryName(userKeyEntity.getBotEntryEntity().getName());
            userObjectList.add(userObject);
        }
        return Response.ok(userObjectList).build();
    }

}
