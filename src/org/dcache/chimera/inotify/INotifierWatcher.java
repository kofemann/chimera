package org.dcache.chimera.inotify;

/**
 * A interface of file system events consumer.
 */
public interface INotifierWatcher {

    void consume(INotifyEvent event);
}
