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
package org.dcache.chimera;

/*
 * @Immutable
 */
public class DbConnectionInfo {


    private final String _dburl;
    private final String _dbdrv;
    private final String _dbuser;
    private final String _dbpass;
    private final String _dbdialect;


    public DbConnectionInfo(String url, String drv, String user, String pass, String dialect) {

        _dburl = url;
        _dbdrv = drv;
        _dbpass = pass;
        _dbuser = user;
        _dbdialect = dialect;

    }

    /**
     * @return db driver string
     */
    public String getDBdrv() {
        return _dbdrv;
    }

    /**
     * @return db user passord
     */
    public String getDBpass() {
        return _dbpass;
    }

    /**
     * @return db connection url
     */
    public String getDBurl() {
        return _dburl;
    }

    /**
     * @return db user
     */
    public String getDBuser() {
        return _dbuser;
    }

    /**
     * @return db dialect string
     */
    public String getDBdialect() {
        return _dbdialect;
    }

}

/*
 * $Log: DbConnectionInfo.java,v $
 * Revision 1.2  2006/12/05 13:13:17  tigran
 * all fields final
 *
 * Revision 1.1  2006/03/30 09:10:44  tigran
 * removed: format and create statements
 * added fisr working db reverence
 *
 */
