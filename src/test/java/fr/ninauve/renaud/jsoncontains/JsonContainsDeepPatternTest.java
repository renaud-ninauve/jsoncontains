package fr.ninauve.renaud.jsoncontains;

import static fr.ninauve.renaud.jsoncontains.JsonContainsDeepPattern.jsonContainsDeep;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class JsonContainsDeepPatternTest {

  @ParameterizedTest
  @CsvSource(
      delimiter = ';',
      value = {
        "toto;toto;true",
        "toto;titi;false",
        "123;123;true",
        "123;999;false",
        "123;toto;false",
        "toto;123;false",
        "toto;{};false",
        "toto;[];false",
        "toto;[\"toto\"];false"
      })
  void value(String pattern, String json, boolean expected) {
    final boolean actual = jsonContainsDeep(pattern).matches(json);
    assertThat(actual).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = ';',
      value = {
        "[1,2];[1,2];true",
        "[1,2];[1,2,3];true",
        "[1,2];[2,1];true",
        "[1,2];[2];false",
        "[1,2];toto;false",
        "[1,2];123;false",
        "[1,2];{};false"
      })
  void jsonArray(String pattern, String json, boolean expected) {
    final boolean actual = jsonContainsDeep(pattern).matches(json);
    assertThat(actual).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = ';',
      value = {
        "{\"a\": 1};{\"a\": 1};true",
        "{};{\"a\": 1};true",
        "{\"a\": 1};{\"a\": 1, \"b\": 2};true",
        "{\"a\": 1};{\"b\": 2, \"a\": 1};true",
        "{\"a\": 1};{\"a\": 9};false",
        "{\"a\": 1};{\"b\": 2};false",
        "{\"a\": {\"b\": 2}};{\"a\": {\"b\": 2}};true",
        "{\"a\": {}};{\"a\": {\"b\": 2}};true",
        "{\"a\": {\"b\": 2}};{\"a\": {\"b\": 9}};false",
        "{\"a\": {\"b\": 2}};{\"a\": {\"c\": 2}};false",
        "{\"a\": [{\"b\": 2}]};{\"a\": [{\"b\": 2}]};true",
        "{\"a\": [{\"b\": 2}]};{\"a\": [{\"b\": 2}, {\"c\": 3}]};true",
        "{};toto;false",
        "{};123;false",
        "{};[];false"
      })
  void jsonObject(String pattern, String json, boolean expected) {
    final boolean actual = jsonContainsDeep(pattern).matches(json);
    assertThat(actual).isEqualTo(expected);
  }

  static Stream<Arguments> sharpString() {
    return Stream.of(
        Arguments.of("#string", "123", "false"),
        Arguments.of("#string", "[123]", "false"),
        Arguments.of("#string", "{\"hello\":\"toto\"}", "false"),
        Arguments.of("{\"hello\": \"#string\"}", "{\"hello\": \"toto\"}", "true"),
        Arguments.of("{\"hello\": \"#string\"}", "{\"hello\": 123}", "false"),
        Arguments.of("{\"hello\": [\"#string\"]}", "{\"hello\": [\"toto\"]}", "true"),
        Arguments.of("{\"hello\": [\"#string\"]}", "{\"hello\": [123]}", "false"));
  }

  @ParameterizedTest
  @MethodSource("sharpString")
  void sharpString(String pattern, String json, boolean expected) {
    final boolean actual = jsonContainsDeep(pattern).matches(json);
    assertThat(actual).isEqualTo(expected);
  }

  static Stream<Arguments> sharpArray() {
    return Stream.of(
        Arguments.of("#array", "[123]", "true"),
        Arguments.of("#array", "123", "false"),
        Arguments.of("#array", "{\"hello\":\"toto\"}", "false"),
        Arguments.of("{\"hello\": \"#array\"}", "{\"hello\": [\"toto\"]}", "true"),
        Arguments.of("{\"hello\": \"#array\"}", "{\"hello\": 123}", "false"),
        Arguments.of("{\"hello\": [\"#array\"]}", "{\"hello\": [\"toto\"]}", "false"),
        Arguments.of("{\"hello\": [\"#array\"]}", "{\"hello\": [[\"toto\"]]}", "true"));
  }

  @ParameterizedTest
  @MethodSource("sharpArray")
  void sharpArray(String pattern, String json, boolean expected) {
    final boolean actual = jsonContainsDeep(pattern).matches(json);
    assertThat(actual).isEqualTo(expected);
  }

  static Stream<Arguments> sharpNotPresent() {
    return Stream.of(
        Arguments.of("#notpresent", "toto", "false"),
        Arguments.of("#notpresent", "123", "false"),
        Arguments.of("{\"hello\": \"#notpresent\"}", "{}", "true"),
        Arguments.of("{\"hello\": \"#notpresent\"}", "{\"xxxx\": \"xxxx\"}", "true"),
        Arguments.of("{\"hello\": \"#notpresent\"}", "{\"hello\": \"toto\"}", "false"),
        Arguments.of(
            "{\"hello\": {\"firstname\": \"#notpresent\", \"name\": \"toto\"}}",
            "{\"hello\": {\"name\": \"toto\"}}",
            "true"));
  }

  @ParameterizedTest
  @MethodSource("sharpNotPresent")
  void sharpNotPresent(String pattern, String json, boolean expected) {
    final boolean actual = jsonContainsDeep(pattern).matches(json);
    assertThat(actual).isEqualTo(expected);
  }
}
