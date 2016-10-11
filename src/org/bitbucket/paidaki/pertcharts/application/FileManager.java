package org.bitbucket.paidaki.pertcharts.application;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import org.bitbucket.paidaki.pertcharts.application.wrappers.ProjectWrapper;
import org.bitbucket.paidaki.pertcharts.gui.dialogs.ExceptionDialog;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class FileManager {

    public static boolean saveProjectToFile(Project project, File file) {
        try {
            // Wrap the project
            ProjectWrapper wrapper = new ProjectWrapper();
            wrapper.setProject(project);

            if (file.getName().toLowerCase().endsWith(".xml")) {
                JAXBContext context = JAXBContext.newInstance(ProjectWrapper.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                // Create and save XML file
                m.marshal(wrapper, file);
            } else if (file.getName().toLowerCase().endsWith(".pert") || file.getName().toLowerCase().endsWith(".ser")) {
                FileOutputStream fileOut = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);

                // Create and serialize file
                out.writeObject(wrapper);

                out.close();
                fileOut.close();
            } else {
                throw new Exception("IllegalFileExtension");
            }
        } catch (Exception e) {
            Alert alert = new ExceptionDialog(e);
            alert.setContentText("Could not save data to file:\n" + file.getPath());
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public static Project loadProjectFromFile(File file) {
        Project project;
        try {
            ProjectWrapper wrapper;
            if (file.getName().toLowerCase().endsWith(".xml")) {
                JAXBContext context = JAXBContext.newInstance(ProjectWrapper.class);
                Unmarshaller um = context.createUnmarshaller();

                // Reading XML from the file and unmarshalling
                wrapper = (ProjectWrapper) um.unmarshal(file);
            } else if (file.getName().toLowerCase().endsWith(".pert") || file.getName().toLowerCase().endsWith(".ser")) {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                // Deserialize file
                wrapper = (ProjectWrapper) in.readObject();

                in.close();
                fileIn.close();
            } else {
                throw new Exception("IllegalFileExtension");
            }
            project = wrapper.extractProject();
            project.setFile(file);
        } catch (Exception e) {
            Alert alert = new ExceptionDialog(e);
            alert.setContentText("Could not load data from file:\n" + file.getPath());
            alert.showAndWait();
            return null;
        }
        return project;
    }

    public static Project loadProjectFromFile(InputStream inputStream, String filename) {
        Project project;
        try {
            JAXBContext context = JAXBContext.newInstance(ProjectWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the stream and unmarshalling
            ProjectWrapper wrapper = (ProjectWrapper) um.unmarshal(inputStream);

            project = wrapper.extractProject();
            project.setProjectName(filename.substring(0, filename.lastIndexOf('.')));
        } catch (Exception e) {
            Alert alert = new ExceptionDialog(e);
            alert.setContentText("Could not load data from file:\n" + filename);
            alert.showAndWait();
            return null;
        }
        return project;
    }

    public static boolean saveChartToImage(WritableImage image, File file) {
        String imageFormat = "png";
        BufferedImage imageJPG = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        if (file.getName().toLowerCase().endsWith("jpg")) {
            imageFormat = "jpg";
            // Remove alpha channel as there is a bug
            BufferedImage imageBI = SwingFXUtils.fromFXImage(image, null);
            imageJPG = new BufferedImage(imageBI.getWidth(), imageBI.getHeight(), BufferedImage.OPAQUE);
            Graphics2D graphics = imageJPG.createGraphics();
            graphics.drawImage(imageBI, 0, 0, null);
            graphics.dispose();
        } else if (file.getName().toLowerCase().endsWith("gif")) {
            imageFormat = "gif";
        }

        try {
            if (imageFormat.equals("jpg")) {
                ImageIO.write(imageJPG, imageFormat, file);
            } else {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), imageFormat, file);
            }
        } catch (IOException e) {
            Alert alert = new ExceptionDialog(e);
            alert.setContentText("Could not save data to file:\n" + file.getPath());
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
