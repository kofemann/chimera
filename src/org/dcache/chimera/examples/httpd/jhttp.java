package org.dcache.chimera.examples.httpd;

//**************************************
// Name: HTTP SERVER
// By: Fiach Reid
//Side Effects:None
//This code is copyrighted and has limited warranties.
//Please see http://www.1JavaStreet.com/xq/ASP/txtCodeId.1791/lngWId.2/qx/vb/scripts/ShowCode.htm
//for details.
//**************************************

import com.mchange.v2.c3p0.DataSources;

import java.net.*;
import java.io.*;

import org.dcache.chimera.*;

import java.util.*;
import javax.sql.DataSource;


public class jhttp extends Thread {
    private Socket _theConnection;

    private FileSystemProvider _fs = null;

    private HFile _docroot;

    private URL _redirectLocation;

    private static String indexfile = "index.html";

    public jhttp(FileSystemProvider fs, Socket s, String path, URL redirectLocation) {
        _theConnection = s;
        _fs = fs;
        _docroot = new HFile(fs, path);
        _redirectLocation = redirectLocation;
    }

    public static void main(String[] args) {
        int thePort;
        URL redirectLocation = null;
        ServerSocket ss;

        if (args.length < 1) {
            System.out.println("java http server");
            System.out.println("usage: ");
            System.out.println("jhttp <path to config.xml> <listenPort> [<redirectserver:port>]");
            System.exit(0);
        }


        // set the port to listen on

        try {
            thePort = Integer.parseInt(args[1]);
            if (thePort < 0 || thePort > 65535) thePort = 80;
        } catch (Exception e) {
            thePort = 80;
        }

        if (args.length > 2) {
            String s = args[2];
            try {
                redirectLocation = new URL("http", s.substring(0, s.indexOf(":")), Integer.parseInt(s.substring(s.indexOf(":") + 1)), "");
                System.out.println("redirect enabled to " + redirectLocation.getHost() + ":" + redirectLocation.getPort());
            } catch (MalformedURLException e) {
                System.out.println("Malformed redirect address. Disabled.");
            }

        }
        try {

            ss = new ServerSocket(thePort);

            System.out.println("Accepting connections on port " + ss.getLocalPort());

            XMLconfig config = new XMLconfig(new File(args[0]));
            DbConnectionInfo connectionInfo = config.getDbInfo(0);
            Class.forName(connectionInfo.getDBdrv());

            DataSource dataSource = DataSources.unpooledDataSource(connectionInfo.getDBurl(), connectionInfo.getDBuser(), connectionInfo.getDBpass());

            FileSystemProvider fs = new JdbcFs(DataSources.pooledDataSource(dataSource), connectionInfo.getDBdialect());

            while (true) {
                jhttp j = new jhttp(fs, ss.accept(), "/", redirectLocation);
                j.start();
            }
        } catch (Exception e) {
            System.err.println("Server aborted prematurely");
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        String method;
        String ct;
        String version = "";
        HFile theFile;

        try {
            PrintStream os = new PrintStream(_theConnection.getOutputStream());

            BufferedReader is = new BufferedReader(new InputStreamReader(_theConnection.getInputStream()));
            String get = is.readLine();
            StringTokenizer st = new StringTokenizer(get);
            method = st.nextToken();

            if (method.equals("GET")) {
                String file = st.nextToken();
                // if (file.endsWith("/"))
                //     file += indexfile;

                if (st.hasMoreTokens()) {
                    version = st.nextToken();
                }
                // loop through the rest of the input lines

                while ((get = is.readLine()) != null) {
                    if (get.trim().equals("")) break;
                }

                try {

                    if ((file == null) || file.equals("/")) {
                        theFile = _docroot;
                        file = null;
                    } else {
                        File f = new File(_docroot, file.substring(1, file.length()));
                        HFile theFileParent = new HFile(_fs, f.getParent());
                        theFile = new HFile(theFileParent, f.getName());

                    }


                    byte[] theData = null;
                    if (theFile.isDirectory()) {
                        // send directory listing
                        String[] list = theFile.list();

                        if (version.startsWith("HTTP/")) { // send a MIME

                            StringBuilder sb = new StringBuilder();

                            sb.append("<HTML><HEAD><TITLE>Index of ").append((file == null ? "/" : file)).append("</TITLE></HEAD>").append("\n");
                            sb.append("<BODY>").append("\n");

                            sb.append("<h1>Index of ").append((file == null ? "/" : file)).append("</h1>").append("\n");
                            sb.append("<hr>\n");
                            for (int i = 0; i < list.length; i++) {

                                StringBuilder newLocation = new StringBuilder();

                                HFile listEntry = new HFile(theFile, list[i]);
                                if (listEntry.isFile() && !(".").equals(listEntry.getName()) && !("..").equals(listEntry.getName()) && _redirectLocation != null) {

                                    //                            	    if configured, redirect the client to the http door for real I/O
                                    newLocation.append(_redirectLocation);

                                }

                                //                            	add the path

                                if (file == null) {
                                    //                            		we are in the root dir
                                    newLocation.append("/");
                                    newLocation.append(list[i]);
                                } else {
                                    //                            		we are in a subdir
                                    newLocation.append(file);
                                    newLocation.append(file.endsWith("/") ? "" : "/");
                                    newLocation.append(list[i]);
                                }


                                sb.append("<br><a href=\"");
                                sb.append(newLocation);
                                sb.append("\">").append(list[i]).append("</a>").append("\n");
                            }
                            sb.append("<hr>\n");
                            sb.append("</BODY></HTML>").append("\n");

                            theData = sb.toString().getBytes();
                            // header
                            os.print("HTTP/1.0 200 OK\r\n");
                            Date now = new Date();
                            os.print("Date: " + now + "\r\n");
                            os.print("Server: jhttp 1.0\r\n");
                            os.print("Content-length: " + theData.length + "\r\n");
                            os.print("Content-type: text/html\r\n\r\n");
                            os.write(theData);
                            os.close();
                        }

                    } else {

                        FileNameMap nameMap = URLConnection.getFileNameMap();

                        theData = new byte[(int) theFile.length()];
                        // need to check the number of bytes read here
                        theFile.read(theData);

                        if (version.startsWith("HTTP/")) { // send a MIME
                            // header
                            os.print("HTTP/1.0 200 OK\r\n");
                            Date now = new Date();
                            os.print("Date: " + now + "\r\n");
                            os.print("Server: jhttp 1.0\r\n");
                            os.print("Content-length: " + theData.length + "\r\n");
                            os.print("Content-type: " + nameMap.getContentTypeFor(file) + "\r\n\r\n");
                        } // end try

                        // send the file
                        os.write(theData);
                        os.close();
                    } // end isDir

                } // end try
                catch (IOException e) { // can't find the file
                    e.printStackTrace();
                    if (version.startsWith("HTTP/")) { // send a MIME header
                        os.print("HTTP/1.0 404 File Not Found\r\n");
                        Date now = new Date();
                        os.print("Date: " + now + "\r\n");
                        os.print("Server: jhttp 1.0\r\n");
                        os.print("Content-type: text/html" + "\r\n\r\n");
                    }
                    os.println("<HTML><HEAD><TITLE>File Not Found</TITLE></HEAD>");
                    os.println("<BODY><H1>HTTP Error 404: File Not Found (" + e.getMessage() + ")</H1></BODY></HTML>");
                    os.close();
                }
            } else { // method does not equal "GET"
                if (version.startsWith("HTTP/")) { // send a MIME header
                    os.print("HTTP/1.0 501 Not Implemented\r\n");
                    Date now = new Date();
                    os.print("Date: " + now + "\r\n");
                    os.print("Server: jhttp 1.0\r\n");
                    os.print("Content-type: text/html" + "\r\n\r\n");
                }
                os.println("<HTML><HEAD><TITLE>Not Implemented</TITLE></HEAD>");
                os.println("<BODY><H1>HTTP Error 501: Not Implemented</H1></BODY></HTML>");
                os.close();
            }
        } catch (IOException e) {

        }

        try {
            _theConnection.close();
        } catch (IOException e) {
        }
    }
}
