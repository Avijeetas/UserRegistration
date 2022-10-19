package com.example.demo.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class EmailValidator implements Predicate<String> {


    public static boolean patternMatches(String email, String regex){
        return Pattern.compile(regex)
                .matcher(email).matches();
    }
    @Override
    public boolean test(String s) {
        //TODO : Regex to validate email
        String regexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        return patternMatches(s, regexPattern);
    }
}
