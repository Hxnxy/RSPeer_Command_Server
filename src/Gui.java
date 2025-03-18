import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gui extends Frame {

    private JPanel panel = new JPanel();
    private JTextField input = new JTextField();
    private JLabel output = new JLabel();

    public void Gui() {
        init();
    }

    public void init() {
        setTitle("Lower case a name");
        setSize(new Dimension(400, 400));
        setLocationRelativeTo(null);
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        input.addPropertyChangeListener(event -> {
            if(input.getText() != null && !input.getText().isEmpty())
                output.setText(input.getText().toLowerCase());
        });
        output.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                StringSelection stringSelection = new StringSelection(output.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });
        panel.add(input);
        panel.add(output);
        add(panel);
        pack();
    }
}
