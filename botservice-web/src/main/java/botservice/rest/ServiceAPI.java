package botservice.rest;

import botservice.model.system.UserKeyEntity;
import botservice.model.system.UserLogEntity;
import botservice.queueprocessing.BotManagerService;
import botservice.rest.model.LogObject;
import botservice.rest.model.MsgObject;
import botservice.rest.model.UserObject;
import botservice.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
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

    @Inject
    BotManagerService botManagerService;

    /**
     * @return список ключей пользователей
     */
    @PermitAll
    @GET
    @Path("/userKeyList/{clientName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserKeyList(@PathParam("clientName") String clientName) throws JsonProcessingException {
        List<UserObject> userObjectList = new ArrayList<>();
        for (UserKeyEntity userKeyEntity: clientService.getUserKeyListByClientName(clientName)){
            UserObject userObject = new UserObject();
            userObject.setUserName(userKeyEntity.getUserName());
            userObject.setBotName(userKeyEntity.getBotEntity().getName());
            userObjectList.add(userObject);
        }
        return Response.ok(userObjectList).build();
    }

    /**
     * @return история сообщений по запросу всем пользователям клиента
     */
    @PermitAll
    @GET
    @Path("/clientLog/{clientName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientLog(@PathParam("clientName") String clientName) throws JsonProcessingException {
        return Response.ok(getLogObjectListByUserLogEntityList(
                clientService.getUserLogListByClientName(clientName))).build();
    }

    /**
     * @return история сообщений по запросу конкретному пользователю клиента
     */
    @PermitAll
    @POST
    @Path("/userKeyLog")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserKeyLog(UserObject userObject)  {
        return Response.ok(getLogObjectListByUserLogEntityList(
                clientService.getUserLogListByBotNameAndUserName(
                        userObject.getBotName(), userObject.getUserName()))).build();
    }

    /**
     * @return Прием сообщения от клиента
     */
    @PermitAll
    @POST
    @Path("/sendMsg")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMsg(MsgObject msgObject) throws JsonProcessingException {
        botManagerService.sendMessageToBot(msgObject);
        return Response.ok().build();
    }

    private List<LogObject> getLogObjectListByUserLogEntityList(List<UserLogEntity> userLogEntityList){
        List<LogObject> logObjectList = new ArrayList<>();
        for(UserLogEntity userLogEntity: userLogEntityList){
            LogObject logObject = new LogObject();
            logObject.setMsgTime(userLogEntity.getMsgTime());
            MsgObject msgObject = new MsgObject();
            msgObject.setDirectionType(userLogEntity.getDirectionType().name());
            msgObject.setMsgBody(userLogEntity.getMsgBody());
            logObject.setMsgObject(msgObject);
            UserObject userObject = new UserObject();
            userObject.setBotName(userLogEntity.getUserKeyEntity().getBotEntity().getName());
            userObject.setUserName(userLogEntity.getUserKeyEntity().getUserName());
            msgObject.setUserObject(userObject);
            logObjectList.add(logObject);
        }
        return logObjectList;
    }
}
