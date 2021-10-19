/*
 * This file is part of Quill by Arcane Arts.
 *
 * Quill by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Quill by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Quill.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.Mohamad82.RUoM.updater;

import me.Mohamad82.RUoM.Ruom;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.CompletableFuture;

public class Download {

    private static JavaPlugin javaPlugin;
    private final InputStream in;
    private OutputStream out;
    private long monitorInterval;

    public Download(InputStream in) {
        this(in, -1);
    }

    public Download(InputStream in, long length) {
        this.in = in;
        monitorInterval = 250;
    }

    public static Download fromURL(JavaPlugin plugin, String url) {
        javaPlugin = plugin;
        return fromURL(url, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
    }

    public static Download fromURL(String url, String agent) {
        try {
            URLConnection c = new URL(url).openConnection();
            c.addRequestProperty("User-Agent", agent);
            return fromStream(c.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Download fromStream(InputStream in) {
        return new Download(in, -1);
    }

    public static Download fromStream(InputStream in, long length) {
        return new Download(in, length);
    }

    public Download agent(String userAgent) {
        return this;
    }

    public Download to(File file) {
        try {
            file.getParentFile().mkdirs();
        } catch (Throwable ignored) {}

        try {
            this.out = new BufferedOutputStream(new FileOutputStream(file), 8192 * 4);
            return this;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Download to(OutputStream out) {
        this.out = out;
        return this;
    }

    public CompletableFuture<Download> start() {
        CompletableFuture<Download> cf = new CompletableFuture<>();
        Ruom.runAsync(() -> {
            try {
                download(cf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return cf;
    }

    public Download monitorInterval(long monitorInterval) {
        this.monitorInterval = monitorInterval;
        return this;
    }

    private void download(CompletableFuture<Download> cf) throws IOException {
        ChronoLatch latch = new ChronoLatch(monitorInterval);
        ReadableByteChannel rbc = Channels.newChannel(in);
        WritableByteChannel wbc = Channels.newChannel(out);
        ByteBuffer buffer = ByteBuffer.allocate(8192 * 4);

        long write = 0;
        int read;

        while ((read = rbc.read(buffer)) > 0) {
            buffer.rewind();
            buffer.limit(read);
            write += read;

            while (read > 0) {
                read -= wbc.write(buffer);
            }

            buffer.clear();
        }

        out.flush();
        out.close();
        in.close();

        cf.complete(this);

    }

}