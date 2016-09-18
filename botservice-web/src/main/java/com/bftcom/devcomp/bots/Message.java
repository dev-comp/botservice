package com.bftcom.devcomp.bots;

import java.util.Map;

/**
 * Класс, представляющий собой сообщение для бомена между сервисом и адаптерами/экземплярами ботов
 * date: 18.09.2016
 *
 * @author p.shapoval
 */
public class Message {

  private String command;
  private Map<String, String> userProperties;
  private Map<String, String> serviceProperties;

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public Map<String, String> getUserProperties() {
    return userProperties;
  }

  public void setUserProperties(Map<String, String> userProperties) {
    this.userProperties = userProperties;
  }

  public Map<String, String> getServiceProperties() {
    return serviceProperties;
  }

  public void setServiceProperties(Map<String, String> serviceProperties) {
    this.serviceProperties = serviceProperties;
  }
}
