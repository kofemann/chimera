package org.dcache.chimera.inotify;

/**
 * Inode activity notification event.
 *
 * @param <T> The type of the context object associated with the event
 */
public class INotifyEvent<T> {

    /**
     * Standard notification event types as specified by Linux inotify
     * interface.
     */
    public enum Kind {

        /**
         * File was accessed (e.g., read(2), execve(2)).
         */
        ACCESS,
        /**
         * Metadata changed—for example, permissions (e.g., chmod(2)),
         * timestamps (e.g., utimensat(2)), extended attributes (setxattr(2)),
         * link count (since Linux 2.6.25; e.g., for the target of link(2) and
         * for unlink(2)), and user/group ID (e.g., chown(2)).
         */
        ATTRIB,
        /**
         * File opened for writing was closed.
         */
        CLOSE_WRITE,
        /**
         * File or directory not opened for writing was * closed.
         */
        CLOSE_NOWRITE,
        /**
         * File/directory created in watched directory (e.g., open(2) O_CREAT,
         * mkdir(2), link(2), symlink(2), bind(2) on a UNIX domain socket).
         */
        CREATE,
        /**
         * File/directory deleted from watched directory.
         */
        DELETE,
        /**
         * Watched file/directory was itself deleted. (This event also occurs if
         * an object is moved to another filesystem, since mv(1) in effect
         * copies the file to the other filesystem and then deletes it from the
         * original filesystem.) In addition, an IN_IGNORED event will
         * subsequently be generated for the watch descriptor.
         */
        DELETE_SELF,
        /**
         * File was modified (e.g., write(2), truncate(2)).
         */
        MODIFY,
        /**
         * Watched file/directory was itself moved.
         */
        MOVE_SELF,
        /**
         * Generated for the directory containing the old filename when a file
         * is renamed.
         */
        MOVED_FROM,
        /**
         * Generated for the directory containing the new filename when a file
         * is renamed.
         */
        MOVED_TO,
        /**
         * File or directory was opened.
         */
        OPEN
    }

    /**
     * The context associated with the event.
     */
    private final T context;

    /**
     * The event kind.
     */
    private final Kind kind;

    /**
     * Construct a new event of given kind and for given context.
     *
     * @param kind kind of the event.
     * @param conntext context associated with the event.
     */
    public INotifyEvent(Kind kind, T conntext) {
        this.kind = kind;
        this.context = conntext;
    }

    /**
     * Returns the context for the event.
     *
     * @return the event context; may be null
     */
    public T context() {
        return context;
    }

    /**
     * Returns the event kind.
     *
     * @return the event kind.
     */
    public Kind kind() {
        return kind;
    }
}
