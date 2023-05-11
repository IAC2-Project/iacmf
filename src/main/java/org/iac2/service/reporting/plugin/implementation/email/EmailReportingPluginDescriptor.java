package org.iac2.service.reporting.plugin.implementation.email;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.common.model.PluginConfigurationEntryType;
import org.iac2.service.reporting.common.interfaces.ReportingPluginDescriptor;

import java.util.Collection;
import java.util.List;

public class EmailReportingPluginDescriptor implements ReportingPluginDescriptor {
    public static final String TO = "to";
    public static final String HOST = "smtp-host";
    // public static final String SSL_PORT = "smtp-ssl-port";
    public static final String TLS_STARTTLS_PORT = "smtp-tsl-starttsl-port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @Override
    public String getIdentifier() {
        return "smtp-email-sending-plugin";
    }

    @Override
    public String getDescription() {
        return "Sends a report about the execution to the specified email address using smtp.";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return List.of(
                new PluginConfigurationEntryDescriptor(TO, PluginConfigurationEntryType.STRING, true, "The email address to send the report to."),
                new PluginConfigurationEntryDescriptor(HOST, PluginConfigurationEntryType.URL, true, "The host address of the SMTP server used for sending the email."),
                //  PluginConfigurationEntryDescriptor(SSL_PORT, PluginConfigurationEntryType.NUMBER, true, "The SSL port of the SMTP server used for sending the email."),
                new PluginConfigurationEntryDescriptor(TLS_STARTTLS_PORT, PluginConfigurationEntryType.NUMBER, true, "The TLS/STARTTLS port of the SMTP server used for sending the email."),
                new PluginConfigurationEntryDescriptor(USERNAME, PluginConfigurationEntryType.STRING, true, "The username to access the SMTP server."),
                new PluginConfigurationEntryDescriptor(PASSWORD, PluginConfigurationEntryType.STRING, true, "The password to access the SMTP server.")
        );
    }

    @Override
    public Plugin createPlugin() {
        return new EmailReportingPlugin(this);
    }
}
