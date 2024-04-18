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
        String originalText = "Your assigned word is wolf.";
        String targetLanguage = "de"; // Prefer language
        String translatedText = translationService.translateText(originalText, targetLanguage);

        System.out.println("Original: " + originalText);
        System.out.println("Translated: " + translatedText);

        // Basic assertion to check if translation is not the same as the original text
        assertNotEquals(originalText, translatedText);
    }
}
