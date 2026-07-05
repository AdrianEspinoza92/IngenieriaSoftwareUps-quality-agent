package org.citasalud.bdd;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty,html:build/reports/cucumber/cucumber-report.html")
@ConfigurationParameter(key = "cucumber.glue", value = "org.citasalud.bdd")
public class CucumberRunner {
}
