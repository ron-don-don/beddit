package app.rondondon.beddit;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TestUtils {
    public static String generateRandomString(int length) {
        String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        return ThreadLocalRandom.current()
                .ints(length, 0, allowedChars.length())
                .mapToObj(allowedChars::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
