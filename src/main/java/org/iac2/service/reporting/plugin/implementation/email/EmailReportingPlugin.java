package org.iac2.service.reporting.plugin.implementation.email;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.model.compliancejob.execution.ExecutionStatus;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancejob.issue.IssueFixingReport;
import org.iac2.service.reporting.common.interfaces.ReportingPlugin;
import org.iac2.service.reporting.common.model.ExecutionReport;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailReportingPlugin implements ReportingPlugin {
    private final EmailReportingPluginDescriptor descriptor;
    private String host;
    // private int sslPort;
    private int tlsPort;
    private String username;
    private String password;
    private String to;

    public EmailReportingPlugin(EmailReportingPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }


    @Override
    public PluginDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        switch (inputName) {
            case EmailReportingPluginDescriptor.TO -> {
                this.to = inputValue;
            }
//            case EmailReportingPluginDescriptor.SSL_PORT -> {
//                this.sslPort = Integer.parseInt(inputValue);
//            }
            case EmailReportingPluginDescriptor.TLS_STARTTLS_PORT -> {
                this.tlsPort = Integer.parseInt(inputValue);
            }
            case EmailReportingPluginDescriptor.USERNAME -> {
                this.username = inputValue;
            }
            case EmailReportingPluginDescriptor.PASSWORD -> {
                this.password = inputValue;
            }
            case EmailReportingPluginDescriptor.HOST -> {
                this.host = inputValue;
            }
        }
    }

    @Override
    public String getConfigurationEntry(String name) {
        switch (name) {
            case EmailReportingPluginDescriptor.TO -> {
                return this.to;
            }
            case EmailReportingPluginDescriptor.TLS_STARTTLS_PORT -> {
                return String.valueOf(this.tlsPort);
            }
//            case EmailReportingPluginDescriptor.SSL_PORT -> {
//                return String.valueOf(this.sslPort);
//            }
            case EmailReportingPluginDescriptor.USERNAME -> {
                return this.username;
            }
            case EmailReportingPluginDescriptor.PASSWORD -> {
                return this.password;
            }
            case EmailReportingPluginDescriptor.HOST -> {
                return this.host;
            }
            default -> {
                return "";
            }
        }
    }

    private String createReportText(ExecutionReport report) {
        StringBuilder builder = new StringBuilder();
        builder.append("Report for Execution (id: ")
                .append(report.getExecution().getId())
                .append(")\n\n\n");
        builder.append("Status: ");

        if (report.getExecution().getStatus() == ExecutionStatus.SUCCESS) {
            builder.append("successful")
                    .append("\n")
                    .append("Violations Detected: ");

            if (report.getExecution().isViolationsDetected()) {
                builder.append("Yes")
                        .append("\n")
                        .append("Violation Details").append("\n");

                int counter = 1;

                for (ComplianceIssue issue : report.getFixingReports().keySet()) {
                    IssueFixingReport fixingReport = report.getFixingReports().get(issue);
                    builder.append("Issue ").append(counter++).append(":\n")
                            .append("Type: ").append(issue.getType()).append("\n")
                            .append("Description: ").append(issue.getDescription()).append("\n")
                            .append("Compliance Rule: ").append(issue.getRule().getId()).append("\n")
                            .append("Issue Fixed: ").append(fixingReport.isSuccessful()).append("\n")
                            .append("Fixing Attempt Details: ").append(fixingReport.getDescription()).append("\n\n");
                }
            } else {
                builder.append("No");
            }

        } else {
            builder.append("failure")
                    .append("\n")
                    .append("Failure Details: ")
                    .append(report.getExecution().getDescription());
        }


        builder.append("\n");
        builder.append("Production System:")
                .append(report.getProductionSystem().getDescription())
                .append("\n");
        builder.append("Started: ").append(report.getExecution().getStartTime()).append("\n")
                .append("Ended: ").append(report.getExecution().getEndTime()).append("\n");

        return builder.toString();

    }

    @Override
    public void reportExecutionOutcome(ExecutionReport report) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", tlsPort);

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject("IACMF: Report for execution #" + report.getExecution().getId());
            message.setText(createReportText(report));

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
