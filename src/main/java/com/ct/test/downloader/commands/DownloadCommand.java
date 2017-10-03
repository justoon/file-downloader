package com.ct.test.downloader.commands;

import com.ct.test.downloader.DownloadManager;
import com.ct.test.downloader.DownloadManagerIntf;
import com.ct.test.downloader.config.AppConfig;
import com.ct.test.downloader.event.ChunkObserver;
import com.ct.test.downloader.validation.ConfigValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.shell.standard.ShellOption.NULL;

/**
 * Created by justinschwartz on 10/2/17.
 *
 Download Command will accept an ad-hoc URL, or use the configured URL
 Issuing a new Download Command will delete the downloaded file if it exists already in order to avoid issues appending chunks
 Command creates a ChunkObserver which will monitor the save directory for downloaded chunks, which allows the Download Manager to request all chunks asynchronously
 */

@ShellComponent
public class DownloadCommand {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    Environment env;

    @Autowired
    AppConfig config;

    @Autowired
    ConfigValidator validator;



    @ShellMethod("Download content from URL, given the setup configuration or defaults")
    public String download(
            @ShellOption(defaultValue=NULL) String url
    ) {
        if(url != null) {
            config.setDownloadUrl(url);
        }

        //check the validity of the configuration before downloading
        if(!validator.isDirectoryValid(config.getSaveDirectory())) {
            return(MessageFormat.format(env.getProperty("messages.error.directory"), config.getSaveDirectory()));
        }

        if (!validator.isUrlValid(config.getDownloadUrl())) {
            return(MessageFormat.format(env.getProperty("messages.error.url"), config.getDownloadUrl()));

        }

        try {
            Path dir = Paths.get(config.getSaveDirectory());
            Path outputFile = Paths.get(config.getOutputFile());
            if(Files.exists(outputFile))
                Files.delete(outputFile);

            ChunkObserver watcher = new ChunkObserver(dir, outputFile, config.getChunks());

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(watcher);

            DownloadManagerIntf manager = config.downloadManager();
            manager.download();

        } catch(IOException ioex) {
            return "error configuring download";
        }

        return "file download running.";
    }
}
