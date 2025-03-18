import org.hxnry.rsp.watcher_server.api.util.ImageEncoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Encoder {

    public static void main(String[] args) {

        String path = "C:\\Users\\Hxnry\\Documents\\Hxnry\\Iconify\\";

        String[] names = {
                "save.png",
                "new.bmp",
                "new3.png",
                "switch.png",
                "profile.png",
                "profile_dark.png",
                "launch.png",
                "repair.png"
        };

        BufferedImage[] output = new BufferedImage[names.length];

        for(int i = 0; i < names.length; i++) {
            System.out.println(names[i] + " @ path: " + path + names[i]);
            output[i] = getImage(path + names[i]);
        }

        SwingUtilities.invokeLater(() -> {
            Gui gui = new Gui(output);
            gui.setVisible(true);
        });

    }

    public static BufferedImage getImage(String path) {
        try {
            return (BufferedImage) ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Gui extends JFrame {

        JPanel jPanel = new JPanel();

        public Gui(BufferedImage... images) {

            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setTitle("Test");
            setSize(300, 200);


            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));
            Arrays.stream(images).forEach(image -> {
                JPanel imageInfo = new JPanel();
                imageInfo.setLayout(new BoxLayout(imageInfo, BoxLayout.X_AXIS));
                JLabel iconLabel = new JLabel(new ImageIcon(image));
                JLabel info = new JLabel("<- press for encode");
                iconLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        StringSelection stringSelection = new StringSelection(ImageEncoder.encodeToString(image));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                    }
                    @Override
                    public void mousePressed(MouseEvent event) {
                        iconLabel.setBorder(new LineBorder(Color.BLACK, 1, true));
                        info.setText("release for encode");
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        iconLabel.setBorder(null);
                        info.setText("copied!");
                    }
                });
                imageInfo.add(iconLabel);
                imageInfo.add(Box.createRigidArea(new Dimension(2, 0)));
                imageInfo.add(info);
                jPanel.add(imageInfo);
                System.out.println("added image");
            });
            add(jPanel);
        }

    }

}
