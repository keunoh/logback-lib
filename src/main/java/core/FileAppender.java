package core;

import core.util.FileSize;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.Map.Entry;

import static core.CoreConstants.CODES_URL;
import static core.CoreConstants.MORE_INFO_PREFIX;

public class FileAppender<E> extends OutputStreamAppender<E> {

    public static final long DEFAULT_BUFFER_SIZE = 8192;
    static protected String COLLISION_WITH_EARLIER_APPENDER_URL = CODES_URL + "#earlier_fa_collision";
    protected boolean append = true;
    protected String fileName = null;
    private boolean prudent = false;
    private FileSize bufferSize = new FileSize(DEFAULT_BUFFER_SIZE);

    public void setFile(String file) {
        if (file == null)
            fileName = file;
        else
            fileName = file.trim();
    }

    public boolean isAppend() {
        return append;
    }

    /**
     * This method is used by derived classes to obtain the raw file property.
     * Regular users should not be calling this method.
     *
     * @return the value of the file property
     */
    final public String rawFileProperty() {
        return fileName;
    }

    /**
     * Returns the value of the <b>File</b> property.
     *
     * <p>
     * This method may be overridden by derived classes.
     *
     */
    public String getFile() {
        return fileName;
    }

    public void start() {
        int errors = 0;
        if (getFile() != null) {
            addInfo("File property is set to [" + fileName + "]");

            if (prudent) {
                if (!isAppend()) {
                    setAppend(true);
                    addWarn("Setting \"Append\" property to true on account of\"Prudent\" mode");
                }
            }

            if (checkForFileCollisionInPreviousFileAppenders()) {
                addError("Collisions detected with FileAppender/RollingAppender instances defined earlier. Aborting.");
                addError(MORE_INFO_PREFIX + COLLISION_WITH_EARLIER_APPENDER_URL);
                errors++;
            } else {
                try {
                    openFile(getFile());
                } catch (java.io.IOException e) {
                    errors++;
                    addError("openFile(" + fileName + "," + append + ") call failed.", e);
                }
            }
        } else {
            errors++;
            addError("\"File\" property not set for appender named [" + name + "].");
        }
        if (errors == 0)
            super.start();
    }

    @Override
    public void stop() {
        super.stop();

        Map<String, String> map = ContextUtil.getFilenameCollisionMap(context);
        if (map == null || getName() == null)
            return;

        map.remove(getName());
    }

    protected boolean checkForFileCollisionInPreviousFileAppenders() {
        boolean collisionDetected = false;
        if (fileName == null) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) context.getObject(CoreConstants.FA_FILENAME_COLLISION_MAP);
        if (map == null)
            return collisionDetected;

        for (Entry<String, String> entry : map.entrySet()) {
            if (fileName.equals(entry.getValue())) {
                addErrorForCollision("File", entry.getValue(), entry.getKey());
                collisionDetected = true;
            }
        }
        if (name != null) {
            map.put(getName(), fileName);
        }
        return collisionDetected;
    }

    protected void addErrorForCollision(String optionName, String optionValue, String appenderName) {
        addError("'" + optionName + "' option has the same value \"" + optionValue + "\" as that given for appender [" + appenderName + "] defined earlier.");
    }

    public void openFile(String file_name) throws IOException {
        lock.lock();
        try {
            File file = new File(file_name);
            boolean result = FileUtil.createMissingParentDirectories(file);
            if (!result)
                addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");

            ResilientFileOutputStream resilientFos = new ResilientFileOutputStream(file, append, bufferSize.getSize());
            resilientFos.setContext(context);
            setOutputStream(resilientFos);
        } finally {
            lock.unlock();
        }
    }

    public boolean isPrudent() {
        return prudent;
    }

    /**
     * When prudent is set to true, file appenders from multiple JVMs can safely
     * write to the same file.
     *
     * @param prudent
     */
    public void setPrudent(boolean prudent) {
        this.prudent = prudent;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public void setBufferSize(FileSize bufferSize) {
        addInfo("Setting bufferSize to ["+bufferSize.toString()+"]");
        this.bufferSize = bufferSize;
    }

    private void safeWrite(E event) throws IOException {
        ResilientFileOutputStream resilientFOS = (ResilientFileOutputStream) getOutputStream();
        FileChannel fileChannel = resilientFOS.getChannel();
        if (fileChannel == null)
            return;

        boolean interrupted = Thread.interrupted();

        FileLock fileLock = null;
        try {
            fileLock = fileChannel.lock();
            long position = fileChannel.position();
            long size = fileChannel.size();
            if (size != position) {
                fileChannel.position(size);
            }
            super.writeOut(event);
        } catch (IOException e) {
            resilientFOS.postIOFailure(e);
        } finally {
            if (fileLock != null && fileLock.isValid()) {
                fileLock.release();
            }

            if (interrupted)
                Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void writeOut(E event) throws IOException {
        if (prudent)
            safeWrite(event);
        else
            super.writeOut(event);
    }
}
