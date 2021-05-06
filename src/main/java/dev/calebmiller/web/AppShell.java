package dev.calebmiller.web;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * Use the @PWA annotation make the application installable on phones, tablets and some desktop browsers.
 */
@Theme(value = "json-compare-java")
@PWA(name = "json-compare-java", shortName = "json-compare-java", offlineResources = {"images/logo.png"})
public class AppShell implements AppShellConfigurator {
}
