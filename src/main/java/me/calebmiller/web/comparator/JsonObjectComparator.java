package me.calebmiller.web.comparator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import me.calebmiller.web.views.model.FieldComparison;

public class JsonObjectComparator {

	private static class Field {

		private boolean hasChildren = false;
		private String fieldName;
		private JsonNode value1;    // Value of field on first object
		private JsonNode value2;    // Value of field on second object
		private List<Field> childFields = new ArrayList<>();

		boolean isHasChildren() {
			return hasChildren;
		}

		void setHasChildren(boolean hasChildren) {
			this.hasChildren = hasChildren;
		}

		String getFieldName() {
			return fieldName;
		}

		void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		JsonNode getValue1() {
			return value1;
		}

		void setValue1(JsonNode value1) {
			this.value1 = value1;
		}

		JsonNode getValue2() {
			return value2;
		}

		void setValue2(JsonNode value2) {
			this.value2 = value2;
		}

		List<Field> getChildFields() {
			return childFields;
		}

		void setChildFields(List<Field> childFields) {
			this.childFields = childFields;
		}
	}

	public List<FieldComparison> compare(ObjectNode jsonObject1, ObjectNode jsonObject2) {
		List<Field> fields = iterate(jsonObject1, jsonObject2);
		return fieldsToComparisons(fields);
	}

	private List<FieldComparison> fieldsToComparisons(List<Field> fields) {
		List<FieldComparison> fieldComparisons = new ArrayList<>();
		fields.forEach(field -> {
			FieldComparison fieldComparison = new FieldComparison();
			fieldComparison.setChildFields(fieldsToComparisons(field.getChildFields()));
			fieldComparison.setFieldName(field.getFieldName());
			fieldComparison.setHasChildren(field.isHasChildren());
			boolean isMatch = fieldComparison.getHasChildren() ? !isAnyFieldNotMatch(fieldComparison.getChildFields())
					: compareFieldValues(field);
			fieldComparison.setMatch(isMatch);
			fieldComparison.setField1Value(getAsString(field.getValue1()));
			fieldComparison.setField2Value(getAsString(field.getValue2()));
			fieldComparisons.add(fieldComparison);
		});
		return fieldComparisons;
	}

	private List<Field> iterate(ObjectNode jsonObject1, ObjectNode jsonObject2) {
		List<Field> fields = new ArrayList<>();
		Set<String> fieldNames = getAllFieldNames(jsonObject1, jsonObject2);
		fieldNames.forEach(fieldName -> {
			Field field = getField(fieldName, jsonObject1.get(fieldName), jsonObject2.get(fieldName));
			handleChildObjects(field);
			fields.add(field);
		});
		return fields;
	}

	private void iterateChildren(Field parentField, ObjectNode child1, ObjectNode child2) {
		ObjectNode childObject1 = child1 == null ? JsonNodeFactory.instance.objectNode() : child1;
		ObjectNode childObject2 = child2 == null ? JsonNodeFactory.instance.objectNode() : child2;
		Set<String> fieldNames = getAllFieldNames(childObject1, childObject2);
		fieldNames.forEach(fieldName -> {
			Field field = getField(fieldName, childObject1.get(fieldName), childObject2.get(fieldName));
			handleChildObjects(field);
			parentField.getChildFields().add(field);
		});
	}

	private Set<String> getAllFieldNames(ObjectNode jsonObject1, ObjectNode jsonObject2) {
		Set<String> fieldNames = new HashSet<>();
		jsonObject1.fieldNames().forEachRemaining(fieldNames::add);
		jsonObject2.fieldNames().forEachRemaining(fieldNames::add);
		return fieldNames;
	}

	private void handleChildObjects(Field field) {
		if (field.value1 instanceof ObjectNode || field.value2 instanceof ObjectNode) {
			iterateChildren(field, (ObjectNode) field.value1, (ObjectNode) field.value2);
			field.setHasChildren(true);
			field.setValue1(new TextNode("[...]"));
			field.setValue2(new TextNode("[...]"));
		}
	}

	private Field getField(String fieldName, JsonNode field1Value, JsonNode field2Value) {
		Field field = new Field();
		field.setHasChildren(false);
		field.setFieldName(fieldName);
		Pair<JsonNode, JsonNode> jsonElements = sanitizeJsonValues(field1Value, field2Value);
		field.setValue1(jsonElements.getLeft());
		field.setValue2(jsonElements.getRight());
		return field;
	}

	private Pair<JsonNode, JsonNode> sanitizeJsonValues(JsonNode jsonElement1, JsonNode jsonElement2) {
		// Ensure JSON values are of the same type
		JsonNode json1 = jsonElement1;
		JsonNode json2 = jsonElement2;
		if (json1 instanceof ObjectNode || json2 instanceof ObjectNode) {
			json1 = isNull(json1) ? JsonNodeFactory.instance.objectNode() : json1;
			json2 = isNull(json2) ? JsonNodeFactory.instance.objectNode() : json2;
		} else if (json1 instanceof ArrayNode || json2 instanceof ArrayNode) {
			json1 = isNull(json1) ? JsonNodeFactory.instance.arrayNode() : getJsonObject((ArrayNode) json1);
			json2 = isNull(json2) ? JsonNodeFactory.instance.arrayNode() : getJsonObject((ArrayNode) json2);
		} else {
			json1 = isNull(json1) ? new TextNode("") : json1;
			json2 = isNull(json2) ? new TextNode("") : json2;
		}
		return Pair.of(json1, json2);
	}

	private ObjectNode getJsonObject(ArrayNode jsonArray) {
		ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();
		for (int i = 0; i < jsonArray.size(); i++) {
			jsonObject.set(String.valueOf(i), jsonArray.get(i));
		}
		return jsonObject;
	}

	private Object getAsType(JsonNode jsonNode) {
		if (jsonNode == null) {
			return "";
		}
		if (jsonNode.isBoolean()) {
			return jsonNode.asBoolean();
		} else if (jsonNode.isNumber()) {
			return jsonNode.numberValue();
		} else {
			return jsonNode.asText();
		}
	}

	private boolean isNull(JsonNode jsonElement) {
		return jsonElement == null || jsonElement.isNull();
	}

	private boolean isEmpty(ObjectNode jsonObject) {
		return jsonObject.isEmpty();
	}

	private boolean isAnyFieldNotMatch(List<FieldComparison> fieldComparisons) {
		Optional<FieldComparison> nonMatchingField = fieldComparisons.stream().filter(field -> !field.getMatch()).findFirst();
		return nonMatchingField.isPresent();
	}

	private boolean compareFieldValues(Field field) {
		Object value1 = getAsType(field.getValue1());
		Object value2 = getAsType(field.getValue2());
		return compareAsType(value1, value2);
	}

	private boolean compareAsType(Object object1, Object object2) {
		// Return true if values match, else return false
		try {
			return CompareToBuilder.reflectionCompare(object1, object2) == 0;
		} catch (ClassCastException e) {
			// Assume we're comparing a value to null
			return false;
		}
	}

	private String getAsString(Object object) {
		if (object instanceof String) {
			return (String) object;
		}
		return ReflectionToStringBuilder.toString(object, ToStringStyle.SIMPLE_STYLE);
	}

	private Set<String> getAsSet(Iterator<String> iterator) {
		HashSet<String> result = new HashSet<>();
		iterator.forEachRemaining(result::add);
		return result;
	}
}
