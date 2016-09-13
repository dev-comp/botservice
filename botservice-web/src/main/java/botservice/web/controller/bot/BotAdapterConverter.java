package botservice.web.controller.bot;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.service.BotService;
import botservice.service.common.BaseParam;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Конвертер для BotAdapterEntity
 */

@Named
public class BotAdapterConverter implements Converter {

    @Inject
    BotService botService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty())
            return null;
        try {
            return botService.getEntityByCriteria(BotAdapterEntity.class, new BaseParam(BotAdapterEntity_.id, Long.parseLong(value)));
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(String.format("%s is not a valid Adapter ID", value)), e);
        }    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null)
            return "";
        if (value instanceof BotAdapterEntity) {
            return String.valueOf(((BotAdapterEntity)value).getId());
        } else {
            throw new ConverterException(new FacesMessage(String.format("%s is not a valid Adapter", value)));
        }    }
}
