package org.motechproject.ebodac.domain;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents language of Subject
 */
public enum Language {
    English("eng"),
    Krio("kri"),
    Limba("lma"),
    Susu("sus"),
    Temne("tem");

    private String code;

    private Language(String code) {
        this.code = code;
    }

    public static Language getByCode(String code) {
        for (Language language : Language.values()) {
            if (language.getCode().equals(code)) {
                return language;
            }
        }
        return null;
    }

    public static Set<String> getListOfCodes() {
        Set<String> codes = new HashSet<>();

        for (Language language : values()) {
            codes.add(language.getCode());
        }
        return ImmutableSet.copyOf(codes);
    }

    public String getCode() {
        return code;
    }


}
