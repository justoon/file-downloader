package com.ct.test.downloader.commands;

import com.ct.test.downloader.config.AppConfig;
import com.ct.test.downloader.validation.ConfigValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.text.MessageFormat;

import static org.springframework.shell.standard.ShellOption.NULL;

/**
 * Created by justinschwartz on 10/2/17.
 *
 Setup command allows the user to change any/all of the configuration defaults
 Setup assumes that the configuration options are named eg. --url <url> --save-directory <directory>
 If the options are not named, Setup will assume the values are provided in order url saveDirectory, etc.
 */

@ShellComponent
public class SetupCommand {

    private Logger log = LoggerFactory.getLogger(getClass().getName());


    @Autowired
    AppConfig config;

    @Autowired
    ConfigValidator validator;

    @Autowired
    Environment env;

    @ShellMethod("Setup downloader app - type help setup for usage")
    public String setup(
            @ShellOption(defaultValue=NULL) String url,
            @ShellOption(defaultValue=NULL) String saveDirectory,
            @ShellOption(defaultValue=NULL) String outputFile,
            @ShellOption(defaultValue="0") long chunkSize,
            @ShellOption(defaultValue="0") int chunks
    ) {
        if(url != null) {
            if (!validator.isUrlValid(url)) {
                return(MessageFormat.format(env.getProperty("messages.error.url"), url));

            }
            config.setDownloadUrl(url);
        }


        if(saveDirectory != null) {

            if(!validator.isDirectoryValid(saveDirectory)) {
                return(MessageFormat.format(env.getProperty("messages.error.directory"), saveDirectory));
            }
            config.setSaveDirectory(saveDirectory);
        }

        if(outputFile != null) {

            config.setOutputFile(outputFile);
        }

        if(chunkSize > 0)
            config.setChunkSize(chunkSize);

        if(chunks > 0)
            config.setChunks(chunks);


        return "app configured.";
    }

    @ShellMethod("Print current configuration")
    public String env(
    ) {

        String currentConfig = MessageFormat.format(env.getProperty("messages.info.setup"),
                config.getDownloadUrl(),
                config.getSaveDirectory(),
                config.getOutputFile(),
                config.getChunkSize(),
                config.getChunks());

        return currentConfig;
    }
}
