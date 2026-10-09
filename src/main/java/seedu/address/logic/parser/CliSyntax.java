package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");

    /* CampusContacts add syntax; edit retains the existing AB3 prefixes. */
    public static final Prefix PREFIX_CONTACT_EMAIL = new Prefix("/email");
    public static final Prefix PREFIX_CONTACT_PHONE = new Prefix("/phone");
    public static final Prefix PREFIX_CONTACT_LOCATION = new Prefix("/location");

}
