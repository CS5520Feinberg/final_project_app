package edu.northeastern.final_project.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericStringValidation<TPattern> implements GenericValidation<TPattern>{
    private TPattern pattern;

    public GenericStringValidation(TPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean validateString(String string) {
        return ((Pattern) pattern).matcher(string).matches();
    }
}
