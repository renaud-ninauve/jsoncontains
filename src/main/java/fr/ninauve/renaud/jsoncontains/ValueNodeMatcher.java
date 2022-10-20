package fr.ninauve.renaud.jsoncontains;

import com.fasterxml.jackson.databind.JsonNode;

public class ValueNodeMatcher {
  private final JsonNode patternNode;

  public ValueNodeMatcher(JsonNode patternNode) {
    this.patternNode = patternNode;
  }

  public boolean matches(JsonNode actualNode) {
    if (patternNode.isTextual() && "#string".equals(patternNode.asText())) {
      return actualNode.isTextual();
    }
    if (patternNode.isTextual() && "#number".equals(patternNode.asText())) {
      return actualNode.isNumber();
    }
    if (patternNode.isTextual() && "#array".equals(patternNode.asText())) {
      return actualNode.isArray();
    }
    if (patternNode.isTextual() && "#object".equals(patternNode.asText())) {
      return actualNode.isObject();
    }
    if (patternNode.isTextual() && "#present".equals(patternNode.asText())) {
      return actualNode == null;
    }
    if (patternNode.isTextual() && "#notpresent".equals(patternNode.asText())) {
      return actualNode == null;
    }
    return patternNode.equals(actualNode);
  }
}
