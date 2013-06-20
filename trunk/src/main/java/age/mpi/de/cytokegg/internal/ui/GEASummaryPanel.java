package age.mpi.de.cytokegg.internal.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.VerticalLayout;
import org.jdom2.Document;
import org.jdom2.Element;

import age.mpi.de.cytokegg.internal.ui.widget.LinkLabel;

public class GEASummaryPanel extends JPanel {
	
	public GEASummaryPanel(Document doc) throws MalformedURLException, IOException{
		List<Element> features = doc.getRootElement().getChild("GFF").getChild("SEGMENT").getChildren("FEATURE");
		
		JLabel anatogram = null;
		JPanel side = new JPanel(new VerticalLayout());
		for(Element feature : features){
			String id = feature.getAttributeValue("id");
			
			if(id.equalsIgnoreCase("Anatomogram")){
				anatogram = decodeAnatogram(feature);
			}else if(id.equalsIgnoreCase("Provenance")){
				// Do nothing
			}else{
				side.add(decodeFeature(feature));
			}
		}
		
		setLayout(new BorderLayout());
		setBackground(Color.white);
		add(anatogram, BorderLayout.CENTER);
		add(new JScrollPane(side), BorderLayout.LINE_END);
		
	}

	private JLabel decodeAnatogram(Element feature) throws MalformedURLException, IOException {
		String link = feature.getChild("LINK").getAttributeValue("href");
		BufferedImage img = ImageIO.read(new URL(link));
		JLabel label = new JLabel(new ImageIcon(img));
		label.setBackground(Color.white);
		return label;
	}

	private JPanel decodeFeature(Element feature) {
		String id = feature.getAttributeValue("id");
		String note = feature.getChildText("NOTE");
		
		JTextArea label = new JTextArea(note, 1, 20);
		label.setEditable(false);
		
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(id.toUpperCase()));
		panel.setBackground(Color.white);
		panel.add(label);
		
		if(feature.getChild("LINK") != null){
			final String link = feature.getChild("LINK").getAttributeValue("href");
			
			LinkLabel lLabel = new LinkLabel("View  ");
			lLabel.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
						
					if(Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						
						try{
							URI uri = new URI(link);
							desktop.browse(uri);
						}catch (Exception e) {
						}
					}
				}
			});
			panel.add(lLabel);
		}
		return panel;
	}

}
