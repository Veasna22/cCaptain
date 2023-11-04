package io.veasna.ccaptain.enumeration;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 16:49
 */
public enum VerificationType {
    ACCOUNT("ACCOUNT"),
    PASSWORD("PASSWORD");

    private final String type;

    VerificationType(String type) {this.type = type; }

    public String getType(){
        return this.type.toLowerCase();
    }
}
