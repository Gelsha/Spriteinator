
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class spriteinator3 {

    public static void main(String[] args) {
        String[] options = {"Map Sprite", "Regular Sprite"};
        int choice = JOptionPane.showOptionDialog(null, "Choose an option", "Option Selector",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            // Option 1 selected
            option1();
        } else if (choice == 1) {
            // Option 2 selected
            option2();
        }
    }

    private static void option1() {
        List<String> successMessages = new ArrayList<>();

        JFileChooser fileChooser = new JFileChooser("D:\\!pixel\\sat\\!bikinin sprite dong");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File mainFolder = fileChooser.getSelectedFile();

            // Create the output folder
            File outputFolder = new File(mainFolder, "!output_mapsprite");
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }

            File[] subfolders = mainFolder.listFiles(File::isDirectory);

            if (subfolders != null) {
                for (File subfolder : subfolders) {
                    if (subfolder.getName().equals("!output_mapsprite")) {
                        continue;
                    }
                    if (subfolder.getName().equalsIgnoreCase("running")) {
                        try {
                            handleRunningFolder(subfolder, outputFolder, successMessages);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            createGenericSprite(subfolder, outputFolder, successMessages);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
			}
            showSuccessDialog(successMessages);
        }
    }

    private static void handleRunningFolder(File folder, File outputFolder, List<String> successMessages) throws IOException {
        File[] imageFiles = folder.listFiles((dir, name) -> name.endsWith(".png"));

        if (imageFiles != null && imageFiles.length == 16) {
            Arrays.sort(imageFiles, (f1, f2) -> {
                String name1 = f1.getName().toLowerCase();
                String name2 = f2.getName().toLowerCase();
                int n1 = extractNumber(name1);
                int n2 = extractNumber(name2);

                if (name1.contains("front")) return -3;
                if (name1.contains("left")) return -2;
                if (name1.contains("right")) return -1;
                return Integer.compare(n1, n2);
            });

            createSpriteGrid(imageFiles, 4, 4, "Run_Sprite", outputFolder, successMessages);
        } else {
            JOptionPane.showMessageDialog(null, "'running' folder must contain exactly 16 images.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createGenericSprite(File folder, File outputFolder, List<String> successMessages) throws IOException {
        File[] imageFiles = folder.listFiles((dir, name) -> name.endsWith(".png"));

        if (imageFiles != null && imageFiles.length > 0) {
            Arrays.sort(imageFiles, (f1, f2) -> {
                int n1 = extractNumber(f1.getName());
                int n2 = extractNumber(f2.getName());
                return Integer.compare(n1, n2);
            });

            int gridSize = (int) Math.ceil(Math.sqrt(imageFiles.length));
            createSpriteGrid(imageFiles, gridSize, gridSize, folder.getName() + "_Sprite", outputFolder, successMessages);
        } else {
            JOptionPane.showMessageDialog(null, folder.getName() + " folder contains no valid images.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createSpriteGrid(File[] imageFiles, int rows, int cols, String spriteName, File outputFolder, List<String> successMessages) throws IOException {
        BufferedImage spriteSheet = null;
        Graphics g = null;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = i * cols + j;
                if (index >= imageFiles.length) break;

                BufferedImage img = ImageIO.read(imageFiles[index]);
                if (spriteSheet == null) {
                    spriteSheet = new BufferedImage(img.getWidth() * cols, img.getHeight() * rows, BufferedImage.TYPE_INT_ARGB);
                    g = spriteSheet.getGraphics();
                }
                g.drawImage(img, j * img.getWidth(), i * img.getHeight(), null);
            }
        }

        if (g != null) g.dispose();

        if (spriteSheet != null) {
            File outputFile = new File(outputFolder, spriteName + ".png");
            ImageIO.write(spriteSheet, "png", outputFile);
            successMessages.add(spriteName + " created successfully at: " + outputFile.getAbsolutePath());
        }
    }

    private static void option2() {
        JFileChooser fileChooser = new JFileChooser("D:\\!pixel\\sat\\!bikinin sprite dong");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File mainFolder = fileChooser.getSelectedFile();
            File outputFolder = new File(mainFolder, "!output_sprite");
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }

            File[] subfolders = mainFolder.listFiles(File::isDirectory);

            if (subfolders != null) {
				
                List<String> successMessages = new ArrayList<>();
                for (File subfolder : subfolders) {
					if (subfolder.getName().equals("!output_sprite")) {
                        continue;
                    }
                    try {
                        createGenericSprite(subfolder, outputFolder, successMessages);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                showSuccessDialog(successMessages);
            }
        }
    }

    private static void showSuccessDialog(List<String> successMessages) {
        if (successMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sprites were created.", "No Action", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder message = new StringBuilder("All sprites created successfully!\n\n");
            for (String msg : successMessages) {
                message.append(msg).append("\n");
            }

            JTextArea textArea = new JTextArea(message.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 200));

            JOptionPane.showMessageDialog(null, scrollPane, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static int extractNumber(String name) {
        String num = name.replaceAll("[^0-9]", "");
        return num.isEmpty() ? 0 : Integer.parseInt(num);
    }
}
