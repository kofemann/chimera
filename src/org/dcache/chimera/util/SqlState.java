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


import java.sql.SQLException;

/**
 * SqlState is an helper class to analyze SQLExceptions and
 * explain the reason of it.
 * <p/>
 * Bases on:
 * SQLSTATE values are comprised of a two-character class code value,
 * followed by a three-character subclass code value.
 * Class code values represent classes of successful
 * and unsuccessful completion conditions.
 */


/*
 * @Immutable
 */


public class SqlState {

    private final String _class;
    private final String _subclass;
    private final String _sqlState;

    public SqlState(SQLException sqlExeption) {

        _sqlState = sqlExeption.getSQLState();
        _class = _sqlState.substring(0, 2);
        _subclass = _sqlState.substring(2);
    }

    public String stateClass() {
        return _class;
    }

    public String stateSubclass() {
        return _subclass;
    }

    public String state() {
        return _sqlState;
    }

    private static final String[][] CLASS_CODE = {{" 00 ", "Unqualified Successful Completion"}, {" 01 ", "Warning"}, {" 02 ", "No Data"}, {" 07 ", "Dynamic SQL Error"}, {" 08 ", "Connection Exception"}, {" 09 ", "Triggered Action Exception"}, {" 0A ", "Feature Not Supported"}, {" 0D ", "Invalid Target Type Specification"}, {" 0F ", "Invalid Token"}, {" 0K ", "Invalid RESIGNAL Statement"}, {" 20 ", "Case Not Found for CASE Statement"}, {" 21 ", "Cardinality Violation"}, {" 22 ", "Data Exception 	Table"}, {" 23 ", "Constraint Violation"}, {" 24 ", "Invalid Cursor State"}, {" 25 ", "Invalid Transaction State"}, {" 26 ", "Invalid SQL Statement Identifier"}, {" 28 ", "Invalid Authorization Specification"}, {" 2D ", "Invalid Transaction Termination"}, {" 2E ", "Invalid Connection Name"}, {" 34 ", "Invalid Cursor Name"}, {" 36 ", "Cursor Sensitivity Exception"}, {" 38 ", "External Function Exception"}, {" 39 ", "External Function Call Exception"}, {" 3B ", "Invalid SAVEPOINT"}, {" 40 ", "Transaction Rollback"}, {" 42 ", "Syntax Error or Access Rule Violation"}, {" 44 ", "WITH CHECK OPTION Violation"}, {" 46 ", "Java DDL"}, {" 51 ", "Invalid Application State"}, {" 53 ", "Invalid Operand or Inconsistent Specification"}, {" 54 ", "SQL or Product Limit Exceeded"}, {" 55 ", "Object Not in Prerequisite State"}, {" 56 ", "Miscellaneous SQL or Product Error"}, {" 57 ", "Resource Not Available or Operator Intervention"}, {" 58 ", "System Error"}};


    @Override
    public String toString() {
        return this.state();
    }


    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;

        boolean equals = false;

        if ((obj instanceof SqlState) && (((SqlState) obj).state().equals(this.state()))) {
            equals = true;
        }
        return equals;
    }


    @Override
    public int hashCode() {
        return this.state().hashCode();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this;
    }

}
/*
 * $Log: SqlState.java,v $
 * Revision 1.1  2006/12/04 21:34:10  tigran
 * herlper class to recognize SQLException couse
 *
 */
