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

public class ChronoLatch {
    private final long interval;
    private long since;

    public static Runnable wrap(ChronoLatch l, Runnable f) {
        return () -> {
            if (l.flip()) {
                f.run();
            }
        };
    }

    public static Runnable wrap(long interval, Runnable f) {
        return wrap(new ChronoLatch(interval), f);
    }

    public ChronoLatch(long interval, boolean openedAtStart) {
        this.interval = interval;
        since = System.currentTimeMillis() - (openedAtStart ? interval * 2 : 0);
    }

    public ChronoLatch(long interval) {
        this(interval, true);
    }

    public boolean flip() {
        if (System.currentTimeMillis() - since > interval) {
            since = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    public Runnable wrap(Runnable r) {
        return wrap(this, r);
    }
}