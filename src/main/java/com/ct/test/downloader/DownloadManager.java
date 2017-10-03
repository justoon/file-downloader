package com.ct.test.downloader;

import com.ct.test.downloader.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;


import java.io.IOException;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Created by justinschwartz on 10/2/17.
 * DownloadManager method will attempt to download part of a file, given a configurable download url.
 The DownloadManager will use specified chunk size and total number of chunks from configuration file or via interactive shell from the user.
 Each request is dispatched asynchronously with the specified Range header
 If the request completes successfully it will write the partial chunk to disk and complete.
 A file re-assembler (ChunkObserver) listens for changes to the directory where the chunks are written
 Partial chunks are named using the chunk # to help the re-assembler put the file back in the correct sequence

 If the operation to write the chunk to disk fails, the app simply prints the error and completes. Future enhancement should provide
 the user with actionable feedback.

 In this implementation a failed download simply prints the stack trace and completes. Adding more robust error handling and retry logic
 would be recommended for additional future enhancements.
 */
public class DownloadManager implements DownloadManagerIntf {

    private Logger log = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    AppConfig config;

    @Autowired
    Environment env;

    public DownloadManager() {

    }


    @Override
    public void download() {

        if(log.isInfoEnabled()) {
            String currentConfig = MessageFormat.format(env.getProperty("messages.info.setup"),
                    config.getDownloadUrl(),
                    config.getSaveDirectory(),
                    config.getOutputFile(),
                    config.getChunkSize(),
                    config.getChunks());

            log.info(currentConfig);
        }

        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        HttpHeaders headers = new HttpHeaders();

        long chunkSize = config.getChunkSize();

        for(int i=0; i < config.getChunks() ; i++) {
            long byteFrom = chunkSize * i ;
            long byteTo = (chunkSize * i + chunkSize) - 1;

            headers.setRange(Arrays.asList(HttpRange.createByteRange(byteFrom, byteTo)));


            if(log.isInfoEnabled()) {
                String byteRange = MessageFormat.format("requesting bytes={0}-{1}", byteFrom, byteTo);
                log.info(byteRange);
            }

            String fileId = MessageFormat.format("file-chunk-{0}", i);

            HttpEntity entity = new HttpEntity("parameters", headers);

            ListenableFuture<ResponseEntity<ByteArrayResource>> futureEntity = restTemplate.exchange(config.getDownloadUrl(), HttpMethod.GET, entity, ByteArrayResource.class);
            ResponseCallback callback = new ResponseCallback(fileId, i);

            futureEntity.addCallback(callback);

        }

    }



    //inner class that contains success and failure functions
    private class ResponseCallback implements ListenableFutureCallback<ResponseEntity<ByteArrayResource>> {

        private String fileId;
        private int chunk;
        private byte[] content;

        public ResponseCallback(String fileId, int chunk) {
            this.fileId = fileId;
            this.chunk = chunk;
        }

        @Override
        public void onSuccess(ResponseEntity<ByteArrayResource> result) {


            if(log.isInfoEnabled()) {
                log.info(MessageFormat.format("received chunk: {0}",result.getHeaders().get("Content-Range").get(0)));

            }

            content = result.getBody().getByteArray();
            Path p = Paths.get(config.getSaveDirectory(), fileId);
            try {
                Files.write(p, content);
            }
            catch (IOException ioex) {
                log.error("Error writing chunk content", ioex);
            }

        }

        @Override
        public void onFailure(Throwable ex) {
            log.error("error downloading", ex);

        }
    }

}
