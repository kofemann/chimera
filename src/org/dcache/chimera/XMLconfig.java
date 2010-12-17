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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLconfig {

    private Document _config;

    private Map<Integer, DbConnectionInfo> _dbs = new HashMap<Integer, DbConnectionInfo>();
    private int _root = 0;

    public XMLconfig(File f) throws IOException {

        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            _config = docBuilder.parse(f);

            _config.getDocumentElement().normalize();

            // load config
            loadConfig();

        } catch (SAXException se) {
            throw new IOException(se.getMessage());
        } catch (ParserConfigurationException pce) {
            throw new IOException(pce.getMessage());
        }


    }

    private void loadConfig() {

        NodeList dbList = _config.getElementsByTagName("db");
        // it's not allowed to have more than one db element
        if (dbList.getLength() < 1) {
            throw new IllegalArgumentException("<db> entry not found");
        }

        for (int i = 0; i < dbList.getLength(); i++) {

            Element db = (Element) dbList.item(i);

            String dburl = db.getAttribute("url");
            String dbdrv = db.getAttribute("drv");
            String dbuser = db.getAttribute("user");
            String dbpass = db.getAttribute("pass");
            String dbdialect = db.getAttribute("dialect");

            Integer id = Integer.valueOf(db.getAttribute("fsid"));

            DbConnectionInfo dbInfo = new DbConnectionInfo(dburl, dbdrv, dbuser, dbpass, dbdialect);

            _dbs.put(id, dbInfo);

        }


        NodeList fsRoot = _config.getElementsByTagName("fsroot");
        if (fsRoot.getLength() > 1) {
            throw new IllegalArgumentException("only one fsroot can be defined");
        }

        if (fsRoot.getLength() == 1) {
            Element fsElement = (Element) fsRoot.item(0);
            try {
                _root = Integer.valueOf(fsElement.getAttribute("id"));
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("The id attribute of <fsroot> have to be non negative integer");
            }
        }
        // else - nothing defined. default is 0

        if (!_dbs.containsKey(Integer.valueOf(_root))) {
            throw new IllegalArgumentException("<db> with root id=" + _root + " is not defined");
        }

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("\nHinera:\n");

        for (Map.Entry<Integer, DbConnectionInfo> db : _dbs.entrySet()) {

            sb.append("Fsid : ").append(db.getKey()).append("'\n");
            sb.append("  url       : '").append(db.getValue().getDBurl()).append("'\n");
            sb.append("  drv       : '").append(db.getValue().getDBdrv()).append("'\n");
            sb.append("  user      : '").append(db.getValue().getDBuser()).append("'\n");
            sb.append("  pass      : '").append(db.getValue().getDBpass()).append("'\n");
            sb.append("  dialect   : '").append(db.getValue().getDBdialect()).append("'\n");
        }
        return sb.toString();

    }

    public boolean isMultiFs() {
        return _dbs.size() > 1;
    }

    public DbConnectionInfo getDbInfo(int id) {
        return _dbs.get(Integer.valueOf(id));
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: HimeraConfig <config>");
            System.exit(1);
        }

        try {
            System.out.println((new XMLconfig(new File(args[0]))).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
