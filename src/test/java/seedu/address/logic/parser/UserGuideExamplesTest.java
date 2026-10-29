package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class UserGuideExamplesTest {
    @TempDir
    public Path testFolder;

    @Test
    public void addExamples_withExistingPhoto_parseSuccessfully() throws Exception {
        Path photo = testFolder.resolve("contact.png");
        ImageIO.write(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "png", photo.toFile());
        String userGuide = Files.readString(Path.of("docs", "UserGuide.md"));
        Matcher examples = Pattern.compile("`(add (?!NAME)[^`]+)`").matcher(userGuide);
        int exampleCount = 0;
        while (examples.find()) {
            String example = examples.group(1);
            // The guide asks users to replace the illustrative photo path with an existing image.
            String command = example.replaceAll("/photo \\S+",
                    Matcher.quoteReplacement("/photo " + photo));
            assertDoesNotThrow(() -> new AddressBookParser().parseCommand(command), example);
            exampleCount++;
        }
        assertTrue(exampleCount > 0, "The guide should contain executable add examples");
    }
}
