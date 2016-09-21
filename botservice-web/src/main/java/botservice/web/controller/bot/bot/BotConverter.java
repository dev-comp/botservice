package botservice.web.controller.bot.bot;

import botservice.model.bot.BotEntity;
import botservice.model.bot.BotEntity_;
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
 * Конвертер для BotEntity
 */

@Named
public class BotConverter implements Converter {

    @Inject
    BotService botService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty())
            return null;
        try {
            return botService.getEntityByCriteria(BotEntity.class, new BaseParam(BotEntity_.id, Long.parseLong(value)));
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(String.format("%s is not a valid Object ID", value)), e);
        }    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null)
            return "";
        if (value instanceof BotEntity) {
            return String.valueOf(((BotEntity)value).getId());
        } else {
            throw new ConverterException(new FacesMessage(String.format("%s is not a valid Object", value)));
        }    }

}
