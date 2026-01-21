package pl.bankapp.cucumber;

import org.junit.jupiter.api.Tag;
import org.junit.platform.suite.api.*;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Tag("integration")
@Suite
@IncludeEngines("cucumber")
@SelectPackages("pl.bankapp.cucumber.features")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "pl.bankapp.cucumber"
)
public class CucumberTest {
}