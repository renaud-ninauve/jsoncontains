package fr.ninauve.renaud.jsoncontains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Iterator;
import java.util.Map.Entry;

public class JsonIsEqualToPattern {
  private final JsonNode patternTree;

  public JsonIsEqualToPattern(JsonNode patternTree) {
    this.patternTree = patternTree;
  }

  public static JsonIsEqualToPattern jsonIsEqualTo(String pattern) {
    return jsonIsEqualTo(parse(pattern));
  }

  public static JsonIsEqualToPattern jsonIsEqualTo(JsonNode pattern) {
    return new JsonIsEqualToPattern(pattern);
  }

  public boolean matches(String actual) {
    return matches(patternTree, parse(actual));
  }

  public boolean matches(JsonNode actual) {
    return matches(patternTree, actual);
  }

  private boolean matches(JsonNode patternNode, JsonNode actualNode) {
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
    if (patternNode.isValueNode()) {
      return patternNode.equals(actualNode);
    }
    if (patternNode.isArray()) {
      return matchesArray(patternNode, actualNode);
    }
    return matchesObject(patternNode, actualNode);
  }

  private boolean matchesArray(JsonNode patternNode, JsonNode actualNode) {
    if (!actualNode.isArray()) {
      return false;
    }
    final Iterator<JsonNode> patternElements = patternNode.elements();
    final Iterator<JsonNode> actualElements = actualNode.elements();
    if (!patternElements.hasNext()) {
      return !actualElements.hasNext();
    }
    while (patternElements.hasNext()) {
      if (!actualElements.hasNext()) {
        return false;
      }
      final JsonNode patternElement = patternElements.next();
      final JsonNode actualElement = actualElements.next();
      if (!matches(patternElement, actualElement)) {
        return false;
      }
    }
    return !actualElements.hasNext();
  }

  private boolean matchesObject(JsonNode patternNode, JsonNode actualNode) {
    if (!actualNode.isObject()) {
      return false;
    }
    final Iterator<Entry<String, JsonNode>> patternFields = patternNode.fields();

    while (patternFields.hasNext()) {
      final Entry<String, JsonNode> patternField = patternFields.next();
      final JsonNode actualField = actualNode.get(patternField.getKey());

      if (!matches(patternField.getValue(), actualField)) {
        return false;
      }
    }
    return actualNode.size() <= patternNode.size();
  }

  private static JsonNode parse(String json) {
    try {
      return new ObjectMapper().readTree(json);
    } catch (JsonProcessingException e) {
      return new TextNode(json);
    }
  }
}
