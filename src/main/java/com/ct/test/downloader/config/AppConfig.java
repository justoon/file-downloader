package com.ct.test.downloader.config;

import com.ct.test.downloader.DownloadManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by justinschwartz on 10/2/17.
 * App Config loads default configuration from application.properties
 * SetupCommand can override these values
 */

@Configuration
public class AppConfig {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    private Environment env;

    private String downloadUrl;
    private String saveDirectory;
    private String outputFile;

    private long chunkSize;
    private int chunks;

    @Bean
    public AppConfig config() {
        if(downloadUrl == null) {
            downloadUrl = env.getProperty("url");

        }

        if(saveDirectory == null) {
            saveDirectory = env.getProperty("save.directory");
        }

        if(outputFile == null) {
            outputFile = env.getProperty("output.file");
        }
        if(chunkSize == 0 ) {
            chunkSize = Long.valueOf(env.getProperty("chunk.size"));
        }
        if(chunks == 0) {
            chunks = Integer.valueOf(env.getProperty("chunks"));
        }

        return this;
    }

    @Bean
    public DownloadManager downloadManager() {

        return new DownloadManager();
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }
}
