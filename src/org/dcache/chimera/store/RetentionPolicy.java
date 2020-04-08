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
package org.dcache.chimera.store;

public enum RetentionPolicy {

    REPLICA(2),
    OUTPUT(1),
    CUSTODIAL( 0);

    private final int _id;

    RetentionPolicy(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public static RetentionPolicy valueOf(int id) throws IllegalArgumentException {

        for(RetentionPolicy rp: values()) {
            if( rp.getId() == id) {
                return rp;
            }
        }
        
        throw new IllegalArgumentException("Unknown State Id");
    }

}
