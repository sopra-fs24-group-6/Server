package ch.uzh.ifi.hase.soprafs24.service;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.cloud.translate.TranslateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class TranslationServiceTest {

    @Autowired
    private TranslationService translationService;

//    @AfterEach
//    public void afterEachTest(TestInfo testInfo) {
//        System.out.println("AfterTranslationServiceTest: " + testInfo.getDisplayName());
//        System.out.println("Current Environment Variables:");
//        String googleCredentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
//        if (googleCredentials != null) {
//            System.out.println("GOOGLE_APPLICATION_CREDENTIALS = " + googleCredentials);
//        } else {
//            System.out.println("GOOGLE_APPLICATION_CREDENTIALS is not set.");
//        }
//    }
    @Test
    public void testTranslateText() {
        String originalText = "Your assigned word is wolf.";
        String targetLanguage = "de";
        String translatedText = translationService.translateText(originalText, targetLanguage);

        System.out.println("Original: " + originalText);
        System.out.println("Translated: " + translatedText);

        assertNotEquals(originalText, translatedText);
    }
}
