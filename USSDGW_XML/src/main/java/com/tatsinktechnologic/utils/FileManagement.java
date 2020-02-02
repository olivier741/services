/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 *
 * @author olivier
 */
public class FileManagement {

    public static void createfile(String file_name) throws IOException {
        File myfile = new File(file_name);
        FileUtils.touch(myfile);

        if (myfile.exists()) {
            System.out.println("The file exists");
        } else {
            System.out.println("The file does not exist");
        }

    }

    public static void deletefile(String file_name) throws IOException {
        File myfile = new File(file_name);
        FileUtils.deleteQuietly(myfile);

        if (myfile.exists()) {
            System.out.println("The file exists");
        } else {
            System.out.println("The file does not exist");
        }

    }

    public static boolean check_file_exist(String file_name) throws IOException {
        File myfile = new File(file_name);
        if (myfile.exists()) {
            return true;
        } else {
            return false;
        }

    }

    public static List<File> getListFile(String folder, String extend) throws IOException {
        File directory = new File(folder);

        if (!directory.exists()) {
            FileUtils.forceMkdir(directory);
        }

        Collection<File> files = FileUtils.listFiles(directory,
                new WildcardFileFilter("*." + extend, IOCase.SENSITIVE),
                new NotFileFilter(DirectoryFileFilter.DIRECTORY));
        if (files != null && !files.isEmpty()) {
            return new ArrayList<File>(files);
        } else {
            return null;
        }

    }

}
