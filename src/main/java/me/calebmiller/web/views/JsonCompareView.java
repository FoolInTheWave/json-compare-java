package me.calebmiller.web.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oreillyauto.testutil.view.model.FieldComparison;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import me.calebmiller.web.comparator.JsonObjectComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Route(value = "compare")
@RouteAlias(value = "")
@PageTitle("JSON Compare")
@CssImport("./themes/json-compare-java/views/json-compare-view.css")
@CssImport(value = "./themes/json-compare-java/components/vaadin-grid.css", themeFor = "vaadin-grid")
public class JsonCompareView extends HorizontalLayout {

	private final Logger logger = LoggerFactory.getLogger(JsonCompareView.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final transient JsonObjectComparator jsonObjectComparator = new JsonObjectComparator();

	private ObjectNode object1;
	private ObjectNode object2;

	private TextArea uiTextArea1;
	private TextArea uiTextArea2;
	private Button uiCompareButton;

	public JsonCompareView() {
		setId("json-compare-view");
		addClassName("json-compare-view");

		uiTextArea1 = new TextArea("JSON Object 1");
		uiTextArea2 = new TextArea("JSON Object 2");
		uiCompareButton = new Button("Compare");

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setPadding(true);
		// Adds and flex-grows both components
		horizontalLayout.addAndExpand(uiTextArea1, uiTextArea2);

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.add(horizontalLayout, uiCompareButton);
		verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, uiCompareButton);
		verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		add(verticalLayout);

		Accordion accordion = new Accordion();
		accordion.add("Input", verticalLayout);
		AccordionPanel disabledPanel = accordion.add("Result", new Span("TODO"));
		disabledPanel.setEnabled(false);
		add(accordion);

		uiCompareButton.addClickListener(e-> {
			Notification.show("Comparing JSON objects...");
			compareObjects();
		});
	}

	private void compareObjects() {
		getJsonObjects();
		if (object1 != null && object2 != null) {
			List<FieldComparison> fieldComparisons = jsonObjectComparator.compare(object1, object2);
			try {
				String fieldComparisonJson = objectMapper.writeValueAsString(fieldComparisons);
				// TODO Set view data and initialize grid on view
//				getModel().setFieldComparisonJson(fieldComparisonJson);
//				getElement().callJsFunction("_initializeGrid");
			} catch (JsonProcessingException e) {
				logger.error("error parsing comparison result into JSON: {}", e.getMessage());
				showErrorDialog("Error parsing comparison result into JSON.");
			}
		}
	}

	private void getJsonObjects() {
		String jsonString1 = uiTextArea1.getValue();
		String jsonString2 = uiTextArea2.getValue();
		try {
			JsonNode jsonNode1 = objectMapper.readTree(jsonString1);
			JsonNode jsonNode2 = objectMapper.readTree(jsonString2);
			object1 = jsonNode1.isObject() ? (ObjectNode) jsonNode1 : JsonNodeFactory.instance.objectNode();
			object2 = jsonNode2.isObject() ? (ObjectNode) jsonNode2 : JsonNodeFactory.instance.objectNode();
		} catch (JsonProcessingException e) {
			logger.error("error parsing json into object: {}", e.getMessage());
			showErrorDialog("Error parsing entered JSON into object.");
		}
	}

	private void showErrorDialog(String message) {
		Dialog dialog = new Dialog();
		dialog.add(new Text(message));
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		Button confirmButton = new Button("OK", event -> dialog.close());
		dialog.add(new Div(confirmButton));

		dialog.open();
	}
}
