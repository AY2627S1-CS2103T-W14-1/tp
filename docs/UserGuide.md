---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# CampusContacts User Guide

CampusContacts is a desktop application for managing contacts in the university community, optimized for use through a Command Line Interface (CLI) while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, CampusContacts can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-W14-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for CampusContacts.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar campuscontacts.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add John Doe /email johnd@example.com /phone 98765432 /location John street, block 123, #01-01` : Adds a contact named `John Doe` to CampusContacts.

   * `show John Doe` : Opens John Doe's full contact details.

   * `delete John Doe` : Deletes John Doe from the contact list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add NAME /email EMAIL /phone PHONE /location LOCATION`, replace `NAME` with `John Doe`.

* Items in square brackets are optional.<br>
  For example, `[t/TAG]` means that a tag such as `t/friend` may be supplied or omitted.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[t/TAG]... ` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Prefixed fields can be in any order. For `add`, the name must come before all prefixed fields.<br>
  For example, `add John Doe /phone 98765432 /location COM1 /email johnd@example.com` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Opens an in-app guide containing the available commands and examples.

Format: `help`


### Adding a person: `add`

Adds a person to the contact list.

Format: `add NAME /email EMAIL /phone PHONE /location LOCATION [t/TAG]...`

* The name, email, phone number, and location are all required and must not be blank.
* Write the name first, without an `n/` prefix. The remaining fields may appear in any order.
* Separate `/email`, `/phone`, and `/location` from their values with a space.
* Each of these three prefixes must appear exactly once; repeated prefixes are rejected, even with identical values.
* Location is stored in the existing address field. It may contain spaces and punctuation.
* Tags are optional and use the `t/TAG` syntax.

If a contact already has the same name, CampusContacts assigns it a distinct display suffix. For example, adding a second `John Tan` displays it as `John Tan_2`.

<box type="tip" seamless>

**Tip:** A person can have any number of tags, including zero.
</box>

Examples:
* `add John Doe /email johnd@example.com /phone 98765432 /location COM1, #02-01`
* `add Betsy Crowe /location COM2 /phone 1234567 /email betsycrowe@example.com t/friend`

### Listing all persons: `list`

Shows a list of all persons in the contact list.

Format: `list`

### Showing a contact's details: `show`

Opens a popup containing a contact's display name, contact ID, phone number, email, location, and tags.

Format: `show NAME[_NUMBER]`

* Use the contact's displayed name.
* The optional `_NUMBER` identifies a particular contact when several contacts have the same name.
* If the suffix is omitted, CampusContacts selects the unsuffixed contact.

Examples:

* `show John Tan` opens details for the unsuffixed John Tan.
* `show John Tan_2` opens details for the second displayed John Tan.

### Deleting a person: `delete`

Deletes the specified person from the contact list.

Format: `delete NAME[_INDEX]`

* Deletes the person with the specified `NAME`.
* The optional number identifies a particular contact with the specified name.
* The index **must be a positive integer** 1, 2, 3, ...
* If the index is omitted, it defaults to 1.

Examples:

Using the below list:

1. Alice
2. Alice_2
3. Betsy
4. Betsy_2

* `delete Alice_2` deletes the contact displayed as `Alice_2`.
* Either `delete Betsy` or `delete Betsy_1` deletes the unsuffixed `Betsy`.
* `delete Alice_3` fails because no contact with that displayed identifier exists.

### Clearing all entries: `clear`

Clears all entries from the contact list.

Format: `clear`

After entering `clear`, type `yes` to confirm deletion or `no` to cancel.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

CampusContacts automatically saves data after every command. You do not need to save manually.

### Editing the data file

CampusContacts data is saved automatically as a JSON file `[JAR file location]/data/campuscontacts.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, CampusContacts starts with an empty contact list at the next run. The invalid file remains on disk until you run a command (CampusContacts saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the CampusContacts to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous CampusContacts home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add NAME /email EMAIL /phone PHONE /location LOCATION [t/TAG]...` <br> e.g., `add James Ho /email jamesho@example.com /phone 22224444 /location COM1 t/friend t/colleague`
**Clear**  | `clear`
**Delete** | `delete NAME[_NUMBER]`<br> e.g., `delete James Ho_2`
**List**   | `list`
**Show**   | `show NAME[_NUMBER]`<br> e.g., `show James Ho_2`
**Help**   | `help`
**Exit**   | `exit`
