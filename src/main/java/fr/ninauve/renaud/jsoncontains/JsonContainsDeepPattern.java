package fr.ninauve.renaud.jsoncontains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Iterator;
import java.util.Map.Entry;

public class JsonContainsDeepPattern {
  private final JsonNode patternTree;

  public JsonContainsDeepPattern(JsonNode patternTree) {
    this.patternTree = patternTree;
  }

  public static JsonContainsDeepPattern jsonContainsDeep(String pattern) {
    return jsonContainsDeep(parse(pattern));
  }

  public static JsonContainsDeepPattern jsonContainsDeep(JsonNode pattern) {
    return new JsonContainsDeepPattern(pattern);
  }

  public boolean matches(String actual) {
    return matches(patternTree, parse(actual));
  }

  public boolean matches(JsonNode actual) {
    return matches(patternTree, actual);
  }

  private boolean matches(JsonNode patternNode, JsonNode actualNode) {
    if (patternNode.isValueNode()) {
      return new ValueNodeMatcher(patternNode).matches(actualNode);
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
    while (patternElements.hasNext()) {
      final JsonNode patternElement = patternElements.next();
      final Iterator<JsonNode> actualElements = actualNode.elements();
      boolean matches = false;
      while (actualElements.hasNext()) {
        final JsonNode actualElement = actualElements.next();
        if (matches(patternElement, actualElement)) {
          matches = true;
          break;
        }
      }
      if (!matches) {
        return false;
      }
    }
    return true;
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
    return true;
  }

  private static JsonNode parse(String json) {
    try {
      return new ObjectMapper().readTree(json);
    } catch (JsonProcessingException e) {
      return new TextNode(json);
    }
  }
}
