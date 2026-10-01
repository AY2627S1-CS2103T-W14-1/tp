# Tutorial 2: Adding a remark command

This branch follows the [AB3 adding-a-command tutorial](https://se-education.org/guides/tutorials/ab3AddRemark.html).
It is a learning exercise, not a proposed CampusContacts feature integration.

## Try it

1. Run the application with `./gradlew run` using the project's JDK 25.
2. Enter `remark 1 r/Likes coffee` to add a remark to the first displayed contact.
3. Enter `remark 1 r/Prefers tea` to replace it.
4. Restart the application: the remark should still appear on the contact card.
5. Enter `edit 1 n/Alice Yeoh`: the existing remark should remain.
6. Enter `remark 1 r/` to clear it. Following the tutorial, `remark 1` also clears it.
7. After a `find` command, indexes refer to the displayed results, not the full address book.
8. Zero, negative, nonnumeric, overflowing and out-of-range indexes are rejected.
   Duplicate `r/` prefixes are rejected consistently with other single-valued fields.

## Data flow

`AddressBookParser` selects `RemarkCommandParser`, which constructs a `RemarkCommand`
from an index and a `Remark` value. Execution replaces the immutable `Person` in the
model while preserving all other fields. The observable list updates `PersonCard`;
the existing `LogicManager` storage path persists the change.

`Person` equality includes remarks, but contact identity remains name-based as in AB3.
Existing JSON files without a remark load with an empty remark; no data deletion is required.
Existing add/sample-data callers likewise create contacts with an empty remark.

## Verification

Run `./gradlew check` for the full test suite and Checkstyle. New tests cover parser
routing, malformed input, add/replace/clear, filtered indexes, failed-command state,
ordinary edit preservation, value equality, legacy data and actual JSON disk round-trips.

## Assistance

Implementation and regression tests were prepared with OpenAI Codex assistance,
using the official tutorial and this repository's existing patterns.
