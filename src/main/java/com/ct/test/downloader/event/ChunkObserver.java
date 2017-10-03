package com.ct.test.downloader.event;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.text.MessageFormat;

/**
 * Created by justinschwartz on 10/3/17.
 *
 * ChunkObserver watches the save-directory for chunks to allow asynchronous requests
 * Chunks are only written to disk on successful completion to minimize issues with concurrency and network issues
 */


public class ChunkObserver implements Runnable {

    private Logger log = LoggerFactory.getLogger(getClass().getName());

    private final Path dir;
    private final Path outputFile;
    private final WatchService watcher;
    private final WatchKey key;
    private int numberChunks;
    private int totalChunks;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public ChunkObserver(Path dir, Path outputFile, int numberChunks) throws IOException {
        this.dir = dir;
        this.numberChunks = numberChunks;
        this.totalChunks = numberChunks;
        this.outputFile = outputFile;

        this.watcher = FileSystems.getDefault().newWatchService();
        this.key = dir.register(watcher, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
    }

    public void run() {


        try {
            for (;;) {
                // wait for key to be signalled
                WatchKey key = watcher.take();

                if (this.key != key) {
                    log.warn("WatchKey not recognized!");
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent<Path> ev = cast(event);

                    numberChunks--;
                    log.info(MessageFormat.format("detected downloaded chunk, have {0} left", numberChunks));

                    if(numberChunks == 0) {
                       log.info("all chunks complete, assembling file");

                       assembleFile();

                       return;
                    }


                }

                // reset key
                if (!key.reset()) {
                    break;
                }
            }
        } catch (InterruptedException x) {
            return;
        }
    }

    private void assembleFile() {

        try {
            for(int i=0;i<totalChunks;i++) {

                String fileId = MessageFormat.format("file-chunk-{0}", i);
                Path p = Paths.get(dir.toString(), fileId);
                byte[] content = Files.readAllBytes(p);
                Files.write(outputFile, content, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                Files.delete(p);

            }
        } catch (IOException ioException) {
            log.error("error assembling file", ioException);
        }

    }
}
