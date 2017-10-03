package com.ct.test.downloader.validation;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

/**
 * Created by justinschwartz on 10/2/17.
 * Simple Class that ensures the download url is well-formed (does not check if the URL exists)
 * Checks to ensure the save-directory exists before attempted to download chunks
 */

@Component
public class ConfigValidator {

    private final String[] schemes = {"http","https"};

    public boolean isUrlValid(String url) {

        boolean valid = false;


        UrlValidator urlValidator = new UrlValidator(schemes);

        valid = urlValidator.isValid(url);


        return valid;

    }

    public boolean isDirectoryValid(String directory) {

        boolean valid = false;

        Path f = Paths.get(directory);
        valid = Files.exists(f);

        return valid;
    }
}
