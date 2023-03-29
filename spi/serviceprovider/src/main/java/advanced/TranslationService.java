package advanced;

import java.util.Locale;

public interface TranslationService {
    String translate(String message, Locale from, Locale to);
}