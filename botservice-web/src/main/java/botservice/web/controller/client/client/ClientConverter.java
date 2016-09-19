package botservice.web.controller.client.client;

import botservice.model.client.ClientEntity;
import botservice.model.client.ClientEntity_;
import botservice.service.ClientService;
import botservice.service.common.BaseParam;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Конвертер для ClientEntity
 */

@Named
public class ClientConverter implements Converter {

    @Inject
    ClientService clientService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty())
            return null;
        try {
            return clientService.getEntityByCriteria(ClientEntity.class, new BaseParam(ClientEntity_.id, Long.parseLong(value)));
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(String.format("%s is not a valid Client ID", value)), e);
        }    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null)
            return "";
        if (value instanceof ClientEntity) {
            return String.valueOf(((ClientEntity)value).getId());
        } else {
            throw new ConverterException(new FacesMessage(String.format("%s is not a valid Client", value)));
        }    }
}
