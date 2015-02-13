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

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlHelper.class);

    private SqlHelper() {
        // no instance allowed
    }

    /**
     * database resource cleanup
     *
     * @param o
     */
    public static void tryToClose(PreparedStatement o) {
        try {
            if (o != null) o.close();
        } catch (SQLException e) {
            LOGGER.error("failed to close prepared statement: {}", e.getMessage());
        }
    }

    /**
     * database resource cleanup
     *
     * @param o
     */
    public static void tryToClose(Statement o) {
        try {
            if (o != null) o.close();
        } catch (SQLException e) {
            LOGGER.error("failed to close result statement: {}", e.getMessage());
        }
    }

    /**
     * database resource cleanup
     *
     * @param o
     */
    public static void tryToClose(ResultSet o) {
        try {
            if (o != null) o.close();
        } catch (SQLException e) {
            LOGGER.error("failed to close result set: {}", e.getMessage());
        }
    }

    /**
     * database resource cleanup
     *
     * @param o
     */
    public static void tryToClose(Connection o) {
        try {
            if (o != null) o.close();
        } catch (SQLException e) {
            LOGGER.error("failed to close connection: {}", e.getMessage());
        }
    }

    /**
     * database resource cleanup
     *
     * @param o
     */
    public static void tryToRollback(Connection o) {
        try {
            o.rollback();
        } catch (SQLException e) {
            LOGGER.error("failed to rollback transaction: {}", e.getMessage());
        }
    }
}
