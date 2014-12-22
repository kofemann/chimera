/*
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program (see the file COPYING.LIB for more
 * details); if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.dcache.chimera.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * 
 */
public class CacheMap<K, V> {

    /**
     *
     */
    private static final long serialVersionUID = -446090383728952825L;
    private static final Logger _log = LoggerFactory.getLogger(CacheMap.class);
    private final Map<K, V> _elements = new ConcurrentHashMap<K, V>();
    private final ScheduledExecutorService _cleaner = new ScheduledThreadPoolExecutor(2);

    /**
     * @param key     :
     *                key
     * @param value   :
     *                value
     * @param timeout :
     *                timeout in seconds
     * @return : Object associated with this key
     */
    public V put(final K key, final V value, long timeout) {

        V ce = _elements.put(key, value);
        _cleaner.schedule(new Runnable() {
            public void run() {
                if (_elements.remove(key) != null) {
                    _log.debug("removing cache value for key" + key);
                }
            }
        }, timeout, TimeUnit.SECONDS);

        return ce;
    }

    public V remove(K key) {
        return _elements.remove(key);
    }

    public V get(K key) {
        return _elements.get(key);
    }

    public boolean isEmpty() {
        return _elements.isEmpty();
    }

    public void clear() {
        _elements.clear();
    }
}
