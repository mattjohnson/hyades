package org.hyades.common;

public enum KafkaTopic {

    NOTIFICATION_ANALYZER("dtrack.notification.analyzer"),
    NOTIFICATION_BOM_CONSUMED("dtrack.notification.bom-consumed"),
    NOTIFICATION_BOM_PROCESSED("dtrack.notification.bom-processed"),
    NOTIFICATION_CONFIGURATION("dtrack.notification.configuration"),
    NOTIFICATION_DATASOURCE_MIRRORING("dtrack.notification.datasource-mirroring"),
    NOTIFICATION_FILE_SYSTEM("dtrack.notification.file-system"),
    NOTIFICATION_INDEXING_SERVICE("dtrack.notification.indexing-service"),
    NOTIFICATION_INTEGRATION("dtrack.notification.integration"),
    NOTIFICATION_NEW_VULNERABILITY("dtrack.notification.new-vulnerability"),
    NOTIFICATION_NEW_VULNERABLE_DEPENDENCY("dtrack.notification.new-vulnerable-dependency"),
    NOTIFICATION_POLICY_VIOLATION("dtrack.notification.policy-violation"),
    NOTIFICATION_PROJECT_AUDIT_CHANGE("dtrack.notification.project-audit-change"),
    NOTIFICATION_REPOSITORY("dtrack.notification.repository"),
    NOTIFICATION_VEX_CONSUMED("dtrack.notification.vex-consumed"),
    NOTIFICATION_VEX_PROCESSED("dtrack.notification.vex-processed"),
    REPO_META_ANALYSIS_COMPONENT("dtrack.repo-meta-analysis.component"),
    REPO_META_ANALYSIS_RESULT("dtrack.repo-meta-analysis.result"),
    VULN_ANALYSIS_SCANNER_RESULT("dtrack.vuln-analysis.scanner.result"),
    VULN_ANALYSIS_COMPONENT("dtrack.vuln-analysis.component"),
    VULN_ANALYSIS_RESULT("dtrack.vuln-analysis.result"),
    MIRROR_OSV("dtrack.vulnerability.mirror.osv"),
    MIRROR_NVD("dtrack.vulnerability.mirror.nvd"),
    NEW_VULNERABILITY("dtrack.vulnerability");

    private final String name;

    KafkaTopic(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
