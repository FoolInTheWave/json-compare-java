package dev.calebmiller.web.comparator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.calebmiller.web.views.model.FieldComparison;

class JsonObjectComparatorTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final JsonObjectComparator comparator = new JsonObjectComparator();

	@Test
	void when_JsonObjectsWithEqualField_Expect_FieldsToMatch() throws JsonProcessingException {
		ObjectNode object1 = (ObjectNode) objectMapper.readTree("{ \"name\": \"Test\", \"age\": 24 }");
		ObjectNode object2 = (ObjectNode) objectMapper.readTree("{ \"name\": \"Test\", \"age\": 30 }");

		List<FieldComparison> fieldComparisons = comparator.compare(object1, object2);
		Optional<FieldComparison> fieldComparison = fieldComparisons.stream().filter(f -> f.getFieldName().equals("name")).findAny();
		if (fieldComparison.isPresent()) {
			assertTrue(fieldComparison.get().getMatch());
		} else {
			fail("Expected field was not in the comparison result.");
		}
	}

	@Test
	void when_JsonObjectsWithNonEqualField_Expect_FieldsToNotMatch() throws JsonProcessingException {
		ObjectNode object1 = (ObjectNode) objectMapper.readTree("{ \"name\": \"Test\", \"age\": 24 }");
		ObjectNode object2 = (ObjectNode) objectMapper.readTree("{ \"name\": \"Test\", \"age\": 30 }");

		List<FieldComparison> fieldComparisons = comparator.compare(object1, object2);
		Optional<FieldComparison> fieldComparison = fieldComparisons.stream().filter(f -> f.getFieldName().equals("age")).findAny();
		if (fieldComparison.isPresent()) {
			assertFalse(fieldComparison.get().getMatch());
		} else {
			fail("Expected field was not in the comparison result.");
		}
	}

	@Test
	void when_JsonObjectsWithDifferentFields_Expect_ResultToContainAllFields() throws JsonProcessingException {
		ObjectNode object1 = (ObjectNode) objectMapper.readTree("{ \"name\": \"Test\", \"age\": 24, \"id\": 1234 }");
		ObjectNode object2 = (ObjectNode) objectMapper.readTree("{ \"name\": \"Test 2\", \"age\": 30, \"date\": 20210224 }");

		List<FieldComparison> fieldComparisons = comparator.compare(object1, object2);
		List<FieldComparison> flattenedComparisons = fieldComparisons.stream().flatMap(FieldComparison::streamAll)
				.collect(Collectors.toList());

		String[] result = flattenedComparisons.stream().map(FieldComparison::getFieldName).distinct().toArray(String[]::new);
		String[] expected = new String[]{"name", "age", "id", "date"};

		Arrays.sort(result);
		Arrays.sort(expected);
		assertArrayEquals(expected, result);
	}

	@Test
	void when_JsonObjectsWithChildObjects_Expect_ResultToContainChildFields() throws JsonProcessingException {
		ObjectNode object1 = (ObjectNode) objectMapper.readTree("{ \"name\": \"Test\", \"child\": { \"date\": 20210424 } }");
		ObjectNode object2 = (ObjectNode) objectMapper.readTree("{ \"name\": \"Test 2\", \"child\": { \"id\": 123 } }");

		List<FieldComparison> fieldComparisons = comparator.compare(object1, object2);
		List<FieldComparison> flattenedComparisons = fieldComparisons.stream().flatMap(FieldComparison::streamAll)
				.collect(Collectors.toList());

		String[] result = flattenedComparisons.stream().map(FieldComparison::getFieldName).distinct().toArray(String[]::new);
		String[] expected = new String[]{"name", "child", "id", "date"};

		Arrays.sort(result);
		Arrays.sort(expected);
		assertArrayEquals(expected, result);
	}

	@Test
	void when_JsonObjectsWithDuplicateKeys_Expect_ResultToContainLastKeyValue() throws JsonProcessingException {
		ObjectNode object1 = (ObjectNode) objectMapper.readTree("{ \"key\": 1, \"key\": 2, \"key\": 3 }");
		ObjectNode object2 = (ObjectNode) objectMapper.readTree("{ \"key\": 1, \"key\": 2, \"key\": 3 }");

		List<FieldComparison> fieldComparisons = comparator.compare(object1, object2);

		assertEquals(1, fieldComparisons.size());
		assertEquals(3, Integer.valueOf(fieldComparisons.get(0).getField1Value()));
		assertEquals(3, Integer.valueOf(fieldComparisons.get(0).getField2Value()));
	}

	@Test
	void when_JsonObjectsWithSameKeysWithDifferentValueTypes_Expect_FieldsToNotMatch() throws JsonProcessingException {
		ObjectNode object1 = (ObjectNode) objectMapper.readTree("{ \"key\": 1, \"key2\": \"2\" }");
		ObjectNode object2 = (ObjectNode) objectMapper.readTree("{ \"key\": \"value\", \"key2\": 2 }");

		List<FieldComparison> fieldComparisons = comparator.compare(object1, object2);

		assertEquals(2, fieldComparisons.size());
		assertFalse(fieldComparisons.get(0).getMatch());
		assertFalse(fieldComparisons.get(1).getMatch());
	}

	@Test
	void when_JsonObjectsWithSameKeysWithOneValueOfNull_Expect_FieldsToNotMatch() throws JsonProcessingException {
		ObjectNode object1 = (ObjectNode) objectMapper.readTree("{ \"key\": 1 }");
		ObjectNode object2 = (ObjectNode) objectMapper.readTree("{ \"key\": null }");

		List<FieldComparison> fieldComparisons = comparator.compare(object1, object2);

		assertEquals(1, fieldComparisons.size());
		assertFalse(fieldComparisons.get(0).getMatch());
	}

	@Test
	void when_JsonObjectsWithSameKeysWithBothValueOfNull_Expect_FieldsToMatch() throws JsonProcessingException {
		ObjectNode object1 = (ObjectNode) objectMapper.readTree("{ \"key\": null }");
		ObjectNode object2 = (ObjectNode) objectMapper.readTree("{ \"key\": null }");

		List<FieldComparison> fieldComparisons = comparator.compare(object1, object2);

		assertEquals(1, fieldComparisons.size());
		assertTrue(fieldComparisons.get(0).getMatch());
	}
}