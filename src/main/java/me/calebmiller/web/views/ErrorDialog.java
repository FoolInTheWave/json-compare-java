package me.calebmiller.web.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

public class ErrorDialog extends PolymerTemplate<ErrorDialog.ErrorDialogModel> {

	@Id("dialog")
	private Dialog dialog;

	public interface ErrorDialogModel extends TemplateModel {

		void setMessage(String message);
		Boolean getOpened();
	}

	public void open(String message) {
		getModel().setMessage(message);
		dialog.open();
	}
}
