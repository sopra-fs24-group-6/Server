package ch.uzh.ifi.hase.soprafs24.service;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    private final Translate translate;

    public TranslationService() {
        // Instantiates a client for Translate
        this.translate = TranslateOptions.getDefaultInstance().getService();
    }

    public String translateText(String originalText, String targetLanguage) {
        /**
         * To save cost for translation API, just returns originalText for now.
         */
        return originalText;

        // Translates some text into the target language
//        Translation translation = translate.translate(
//                originalText,
//                Translate.TranslateOption.targetLanguage(targetLanguage),
//                // Use "base" for standard model; "nmt" for the Neural Machine Translation model
//                // "base" is cost-effective but with lower quality while NMT is the opposite
//                Translate.TranslateOption.model("base")
//        );
//        return translation.getTranslatedText();
    }
}