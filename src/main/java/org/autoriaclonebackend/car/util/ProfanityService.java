package org.autoriaclonebackend.car.util;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Service
public class ProfanityService {
    private final Set<String> forbiddenWords = new HashSet<>();

    public boolean containsProfanity(String text) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("profanity_words.txt")) {
            if (is == null) throw new RuntimeException("Файл profanity_words.txt не найден в resources");
            new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .forEach(line -> forbiddenWords.add(line.toLowerCase()));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить файл с запрещёнными словами", e);
        }

        if (text == null || text.isEmpty()) return false;

        String lowerText = text.toLowerCase();
        return forbiddenWords.stream().anyMatch(lowerText::contains);
    }
}
