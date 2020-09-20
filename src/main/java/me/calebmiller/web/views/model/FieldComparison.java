package com.oreillyauto.testutil.view.model;

import java.util.List;

public class FieldComparison {

	private Boolean match = false;
	private String fieldName;
	private String field1Value;
	private String field2Value;
	private Boolean hasChildren = false;
	private List<FieldComparison> childFields;

	public Boolean getMatch() {
		return match;
	}

	public void setMatch(Boolean match) {
		this.match = match;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getField1Value() {
		return field1Value;
	}

	public void setField1Value(String field1Value) {
		this.field1Value = field1Value;
	}

	public String getField2Value() {
		return field2Value;
	}

	public void setField2Value(String field2Value) {
		this.field2Value = field2Value;
	}

	public Boolean getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(Boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public List<FieldComparison> getChildFields() {
		return childFields;
	}

	public void setChildFields(List<FieldComparison> childFields) {
		this.childFields = childFields;
	}
}
