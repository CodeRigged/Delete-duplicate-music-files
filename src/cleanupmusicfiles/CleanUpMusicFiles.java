/* 
 * The MIT License
 *
 * Copyright 2018 Timothy Antonovics.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cleanupmusicfiles;

import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Tim
 */
public class CleanUpMusicFiles {

    /**
     * @param main Method
     */
    public static void main(String[] args) {
        JFileChooser choseDirectory = new JFileChooser();
        choseDirectory.setCurrentDirectory(new File("."));
        choseDirectory.setDialogTitle("choser");
        choseDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        choseDirectory.setAcceptAllFileFilterUsed(false);
        
        if (choseDirectory.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                List<String> files = Files.walk(Paths.get(choseDirectory.getCurrentDirectory().toURI())).map(filePath -> filePath.toString()).filter(filePath -> filePath.endsWith(".mp3")).map((filePath) -> {
                    String updatedFilePath = filePath;
                    if (updatedFilePath.endsWith("(2).mp3")) {
                        updatedFilePath = updatedFilePath.substring(0, updatedFilePath.length() - 7);
                    } else {
                        updatedFilePath = updatedFilePath.substring(0, updatedFilePath.length() - 4);
                    }
                    return updatedFilePath;
                }).sorted().collect(Collectors.toList());
                
                Set<String> toBeKept = CleanUpMusicFiles.findDuplicates(files).stream().map(filePath -> filePath + ".mp3").collect(Collectors.toSet());
                Set<String> toBeDeleted = CleanUpMusicFiles.findDuplicates(files).stream().map(filePath -> filePath + "(2).mp3").collect(Collectors.toSet());
                
                List<String> checkForSafety = Stream.concat(toBeKept.stream(), toBeDeleted.stream()).sorted().collect(Collectors.toList());
                checkForSafety.forEach(System.out::println);
                
                if (toBeDeleted.size() > 0) {
                    int option = JOptionPane.showConfirmDialog(new Frame(), "Are you sure you want to delete the following files?\n" + toBeDeleted, "Warning", JOptionPane.WARNING_MESSAGE);
                    System.out.println(option);
                    boolean delete = option == 0;
                    if (delete) {
                        CleanUpMusicFiles.deleteFiles(toBeDeleted);
                    }
                } else {
                    JOptionPane.showMessageDialog(new Frame(), "No duplicates found");
                }
                System.exit(0);
            } catch (IOException ex) {
                Logger.getLogger(CleanUpMusicFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("No Selection ");
        }
        
    }
    
    private static Set<String> findDuplicates(List<String> files) {
        Set<String> returnedSet = new HashSet<>();
        Set<String> compareTo = new HashSet<>();
        files.stream().filter((s2) -> (!compareTo.add(s2))).forEachOrdered((s2) -> {
            returnedSet.add(s2);
        });
        return returnedSet;
    }
    
    private static void deleteFiles(Set<String> toBeDeleted) {
        toBeDeleted.forEach((filePath) -> {
            File file = new File(filePath);
            String message = file.delete() ? "File successfully deleted: " + file.getAbsolutePath() : "Failed to delete file at: " + file.getAbsolutePath();
            System.out.println(message);
        });
    }
}
