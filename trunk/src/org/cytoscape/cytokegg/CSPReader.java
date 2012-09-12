package org.cytoscape.cytokegg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;


import cytoscape.CyNetwork;
import cytoscape.util.URLUtil;

public class CSPReader {
	
	private static final String PACKAGE_NAME = "org.cytoscape.KGML.generated";
	
	public CSPReader(URL url) throws IOException {
		System.out.println("Debug: KGML URL name = " + url);
		
		InputStream is = null;
		org.cytoscape.cytokegg.generated.Pathway pathway = null;
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE_NAME, this.getClass().getClassLoader());
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			is = URLUtil.getBasicInputStream(url);
			pathway = (org.cytoscape.cytokegg.generated.Pathway) unmarshaller.unmarshal(is);
			

		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Could not unmarshall KGML file");
		} finally {
			if (is != null) {
				is.close();
			}
		}

		if(pathway != null){
			CSPathwayMapper mapper = new CSPathwayMapper(pathway);
			CyNetwork net = mapper.doMapping();
			mapper.updateView(net);
			mapper.bypassAttributes(net);
			
			String dSet = "";
			if(Plugin.getInstance().getCurrentDataSet() != null)
				dSet = Plugin.getInstance().getCurrentDataSetName();
			
			Pathway p = new Pathway(pathway.getName(), pathway.getTitle(), dSet, mapper, net);
			Plugin.getInstance().setPathway(p);
		}
	}

}
