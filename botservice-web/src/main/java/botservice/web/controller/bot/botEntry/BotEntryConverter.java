package botservice.web.controller.bot.botEntry;

import botservice.model.bot.BotEntryEntity;
import botservice.model.bot.BotEntryEntity_;
import botservice.service.ClientAppService;
import botservice.service.common.BaseParam;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Конвертер для BotEntryEntity
 */

@Named
public class BotEntryConverter implements Converter {

    @Inject
    ClientAppService clientAppService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty())
            return null;
        try {
            return clientAppService.getEntityByCriteria(BotEntryEntity.class, new BaseParam(BotEntryEntity_.id, Long.parseLong(value)));
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(String.format("%s is not a valid Entry ID", value)), e);
        }    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null)
            return "";
        if (value instanceof BotEntryEntity) {
            return String.valueOf(((BotEntryEntity)value).getId());
        } else {
            throw new ConverterException(new FacesMessage(String.format("%s is not a valid Entry", value)));
        }    }

}
