package de.mpg.biochem.cytokegg.internal.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ComboSelections {

public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, UnsupportedLookAndFeelException {

UIManager.setLookAndFeel((LookAndFeel) Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance());

final JPopupMenu menu = new JPopupMenu();
JMenuItem one = new JCheckBoxMenuItem("One");
JMenuItem two = new JCheckBoxMenuItem("Two");
JMenuItem three = new JCheckBoxMenuItem("Three");
JMenuItem four = new JCheckBoxMenuItem("Four");
menu.add(one);
menu.add(two);
menu.add(three);
menu.add(four);


final JButton button = new JButton("Click me");
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!menu.isVisible()) {
            Point p = button.getLocationOnScreen();
            menu.setInvoker(button);
            menu.setLocation((int) p.getX(),
                    (int) p.getY() + button.getHeight());
            menu.setVisible(true);
        } else {
            menu.setVisible(false);
        }

    }
});

one.addActionListener(new OpenAction(menu, button));
two.addActionListener(new OpenAction(menu, button));
three.addActionListener(new OpenAction(menu, button));
four.addActionListener(new OpenAction(menu, button));

JFrame frame = new JFrame();
JPanel panel = new JPanel();
panel.add(button);
frame.getContentPane().add(panel);
frame.pack();
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setVisible(true);
}

private static class OpenAction implements ActionListener {

    private JPopupMenu menu;
    private JButton button;

    private OpenAction(JPopupMenu menu, JButton button) {
        this.menu = menu;
        this.button = button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        menu.show(button, 0, button.getHeight());
    }
}
}