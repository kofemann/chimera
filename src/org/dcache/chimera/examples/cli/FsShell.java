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
package org.dcache.chimera.examples.cli;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.dcache.chimera.DirectoryStreamB;
import org.dcache.chimera.FileSystemProvider;
import org.dcache.chimera.FsInode;
import org.dcache.chimera.HimeraDirectoryEntry;


public class FsShell {

    public static void main(String args[]) {

        if (args.length != 1) {
            System.err.println("Usage: FsShell " + FsFactory.USAGE);
            System.exit(1);
        }

        try {

            FileSystemProvider fs = FsFactory.createFileSystem(args);

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            boolean prompt = true;
            while (true) {
                try {


                    if (prompt) {
                        System.out.print("jFs $ ");
                    }

                    String line = br.readLine();
                    if (line == null) {
                        break;
                    } else {
                        line = line.trim();
                    }

                    if (line.length() == 0) continue;

                    String[] cmdArgs = line.split("[ \t]+");

                    if (cmdArgs[0].equals("prompt")) {
                        try {
                            prompt = Boolean.valueOf(cmdArgs[1]).booleanValue();
                        } catch (Exception e) {
                            System.out.println("prompt < true | false >");
                        }
                        continue;
                    }

                    if (cmdArgs[0].equals("mkdir")) {
                        if (cmdArgs.length != 2) {
                            System.out.println("mkdir <path>");
                            continue;
                        }
                        int li = cmdArgs[1].lastIndexOf('/');
                        String path = cmdArgs[1].substring(li + 1);
                        String dir = null;
                        if (li > 1) {
                            dir = cmdArgs[1].substring(0, li);
                        } else {
                            dir = "/";
                        }

                        fs.mkdir(fs.path2inode(dir), path);
                        continue;
                    }

                    if (cmdArgs[0].equals("touch")) {
                        if (cmdArgs.length != 2) {
                            System.out.println("touch <path>");
                            continue;
                        }


                        try {
                            fs.path2inode(cmdArgs[1]);
                            System.out.println("path already exist");
                        } catch (Exception e) {
                            try {
                                fs.createFile(cmdArgs[1]);
                            } catch (Exception ee) {
                                System.out.println("unable to create file " + cmdArgs[1] + " : " + ee.getMessage());
                            }
                        }
                        continue;
                    }


                    if (cmdArgs[0].equals("cat")) {
                        if (cmdArgs.length != 2) {
                            System.out.println("cat <path>");
                            continue;
                        }

                        byte[] buf = new byte[512];

                        FsInode inode = fs.path2inode(cmdArgs[1]);
                        int n;
                        long offset = 0;
                        do {
                            n = inode.read(offset, buf, 0, 512);
                            offset += n;
                            System.out.print(new String(buf, 0, n));
                        } while (n == 512);
                        System.out.println();
                        continue;
                    }

                    if (cmdArgs[0].equals("p2i")) {
                        if (cmdArgs.length < 2 || cmdArgs.length > 3) {
                            System.out.println("p2i <path> [rootInode] ");
                            continue;
                        }

                        if (cmdArgs.length == 2) {
                            System.out.println(fs.path2inode(cmdArgs[1]));
                        } else { // cmdArgs.length == 3
                            System.out.println(fs.path2inode(cmdArgs[1], new FsInode(fs, cmdArgs[2])));
                        }
                        continue;
                    }

                    if (cmdArgs[0].equals("i2p")) {
                        if (cmdArgs.length < 2 || cmdArgs.length > 4) {
                            System.out.println("i2p <inode> [statFrom] [isInclusive]");
                            continue;
                        }


                        if (cmdArgs.length > 2) {
                            boolean inclusive = true;
                            FsInode startFrom = new FsInode(fs, cmdArgs[2]);
                            if (cmdArgs.length > 3) inclusive = Boolean.valueOf(cmdArgs[3]);
                            System.out.println(fs.inode2path(new FsInode(fs, cmdArgs[1]), startFrom, inclusive));
                        } else {
                            System.out.println(fs.inode2path(new FsInode(fs, cmdArgs[1])));
                        }
                        continue;
                    }

                    if (cmdArgs[0].equals("stat")) {
                        if (cmdArgs.length != 2) {
                            System.out.println("stat <path>");
                            continue;
                        }

                        org.dcache.chimera.posix.Stat stat = fs.path2inode(cmdArgs[1]).stat();

                        System.out.println(stat.toString());
                        continue;
                    }

                    if (cmdArgs[0].equals("ls")) {

                        if (cmdArgs.length != 2) {
                            System.out.println("ls  <path>");
                            continue;
                        }
                        FsInode inode = fs.path2inode(cmdArgs[1]);
                        DirectoryStreamB<HimeraDirectoryEntry> dirStream = inode.newDirectoryStream();
                        try {
                            for (HimeraDirectoryEntry dirEntry : dirStream) {
                                System.out.println(dirEntry.getName());
                            }
                        } finally {
                            dirStream.close();
                        }

                        continue;
                    }


                    if (cmdArgs[0].equals("dir")) {

                        if (cmdArgs.length != 2) {
                            System.out.println("dir  <path>");
                            continue;
                        }

                        FsInode inode = fs.path2inode(cmdArgs[1]);
                        DirectoryStreamB<HimeraDirectoryEntry> dirStream = inode.newDirectoryStream();
                        try {
                            for (HimeraDirectoryEntry dirEntry : dirStream) {
                                System.out.println(dirEntry.getStat() + " " + dirEntry.getName());
                            }
                        } finally {
                            dirStream.close();
                        }
                        continue;
                    }

                    if (cmdArgs[0].equals("setsize")) {

                        if (cmdArgs.length != 3) {
                            System.out.println("setsize  <path> <newsize>");
                            continue;
                        }
                        fs.setFileSize(fs.path2inode(cmdArgs[1]), Long.parseLong(cmdArgs[2]));
                        continue;
                    }


                    if (cmdArgs[0].equals("chmod")) {

                        if (cmdArgs.length != 3) {
                            System.out.println("chmod  <path> <mode>");
                            continue;
                        }
                        fs.setFileMode(fs.path2inode(cmdArgs[1]), Integer.parseInt(cmdArgs[2], 8));
                        continue;
                    }


                    if (cmdArgs[0].equals("delete")) {

                        if (cmdArgs.length != 2) {
                            System.out.println("delete  <path>");
                            continue;
                        }

                        fs.remove(cmdArgs[1]);
                        continue;
                    }

                    if (cmdArgs[0].equals("chown")) {

                        if (cmdArgs.length != 3) {
                            System.out.println("chown  <path> <uid>");
                            continue;
                        }
                        fs.setFileOwner(fs.path2inode(cmdArgs[1]), Integer.parseInt(cmdArgs[2]));
                        continue;
                    }

                    if (cmdArgs[0].equals("chgrp")) {

                        if (cmdArgs.length != 3) {
                            System.out.println("chgrp  <path> <gid>");
                            continue;
                        }
                        fs.setFileGroup(fs.path2inode(cmdArgs[1]), Integer.parseInt(cmdArgs[2]));
                        continue;
                    }


                    if (cmdArgs[0].equals("exit") || cmdArgs[0].equals("quit")) {
                        System.out.println("bye-bye");
                        break;
                    }

                    if (cmdArgs[0].equals("help")) {
                        System.out.println("  $Id:FsShell.java 140 2007-06-07 13:44:55Z tigran $");
                        System.out.println("    format                : reinitialize file system (destry all data).");
                        System.out.println("    mkdir <path>          : create a directory.");
                        System.out.println("    touch <path>          : create a file.");
                        System.out.println("    chmod <path> <mode>   : change file permissions.");
                        System.out.println("    setsize <path> <size> : set a file size.");
                        System.out.println("    ls <path>             : list a directory.");
                        System.out.println("    dir <path>            : long list.");
                        System.out.println("    stat <path>           : show file attributes.");
                        System.out.println("    p2i <path>            : show file inode.");
                        System.out.println("    i2p <inode>           : show name of inode");
                        System.out.println("    delete <path>         : remove file or directory");
                        System.out.println("    exit                  : exit shell");
                        System.out.println("    help                  : this help");
                        continue;
                    }

                    // none of above
                    System.out.println("Command not found: " + line);

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
