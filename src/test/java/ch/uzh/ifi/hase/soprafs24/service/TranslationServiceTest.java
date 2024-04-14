package ch.uzh.ifi.hase.soprafs24.service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class TranslationServiceTest {

    @Autowired
    private TranslationService translationService;

    @Test
    public void testTranslateText() {
        String originalText = "这届话事人我选吹鸡，他说可以带我们上月球";
        String targetLanguage = "en"; // English
        String translatedText = translationService.translateText(originalText, targetLanguage);

        System.out.println("Original: " + originalText);
        System.out.println("Translated: " + translatedText);

        // Basic assertion to check if translation is not the same as the original text
        assertNotEquals(originalText, translatedText);
    }
}
