package com.bftcom.devcomp.bots;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Класс, представляющий собой сообщение для бомена между сервисом и адаптерами/экземплярами ботов
 * date: 18.09.2016
 *
 * @author p.shapoval
 */
public class Message {

  private String command;
  private Map<String, String> properties;

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }
}
