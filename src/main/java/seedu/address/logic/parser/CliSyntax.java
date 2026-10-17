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
    public static final Prefix PREFIX_CONTACT_ROLE = new Prefix("/role");
    public static final Prefix PREFIX_CONTACT_EMAIL = new Prefix("/email");
    public static final Prefix PREFIX_CONTACT_PHONE = new Prefix("/phone");
    public static final Prefix PREFIX_CONTACT_LOCATION = new Prefix("/location");
    public static final Prefix PREFIX_CONTACT_MAJOR = new Prefix("/major");
    public static final Prefix PREFIX_CONTACT_YEAR = new Prefix("/year");
    public static final Prefix PREFIX_CONTACT_FACULTY = new Prefix("/faculty");
    public static final Prefix PREFIX_CONTACT_DEPARTMENT = new Prefix("/department");
    public static final Prefix PREFIX_CONTACT_OFFICE_HOURS = new Prefix("/hours");

}
