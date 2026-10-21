package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.PERSON_INDEX_FIRST;
import static seedu.address.testutil.TypicalIndexes.PERSON_INDEX_SECOND;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListFavouritesCommand;
import seedu.address.logic.commands.SetFavouriteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

class SetFavouriteCommandParserTest {
    @Test
    void parseCommand_validIdentifiers_usesSharedNameGrammar() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        for (String word : List.of("favourite", "unfavourite")) {
            boolean isFavourite = word.equals("favourite");
            assertEquals(new SetFavouriteCommand("John Tan", PERSON_INDEX_FIRST, isFavourite),
                    parser.parseCommand(word + "   john tan   "));
            assertEquals(new SetFavouriteCommand("John Tan", PERSON_INDEX_SECOND, isFavourite),
                    parser.parseCommand(word + " John Tan_2"));
        }
        assertInstanceOf(ListFavouritesCommand.class, parser.parseCommand("favourites   "));
    }

    @Test
    void parse_invalidIdentifier_rejectsForBothOperations() {
        for (String args : List.of("", " ", "John_0", "John_-1", "John_2147483648", "John_", "John_two",
                "John_2_3", "John _2", "_2", "John&", "John_2 extra")) {
            assertThrows(ParseException.class, () -> new SetFavouriteCommandParser(true).parse(args), args);
            assertThrows(ParseException.class, () -> new SetFavouriteCommandParser(false).parse(args), args);
        }
    }

    @Test
    void parseCommand_invalidCommand_rejects() {
        for (String input : List.of("favourites John", "favourites_2", "favourite", "unfavourite")) {
            assertThrows(ParseException.class, () -> new AddressBookParser().parseCommand(input), input);
        }
    }
}
