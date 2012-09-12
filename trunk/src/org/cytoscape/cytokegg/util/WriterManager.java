/*
 * Copyright (C) 2011-2012 José María Villaveces Max Plank institute for biology
 * of ageing (MPI-age)
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cytoscape.cytokegg.util;

import java.io.File;
import java.io.IOException;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.cytoscape.cytokegg.Repository;

public class WriterManager {
	
	private static WriterManager instance = new WriterManager();
	private IndexWriter indexWriter;
	private FSDirectory dir;
	
	/**
	 * Constructor, private because of singleton
	 */
	private WriterManager() {
		try {
			dir = FSDirectory.open(new File(PluginProperties.getInstance().getIndexPath()));
			indexWriter = new IndexWriter(dir,new StandardAnalyzer(Version.LUCENE_30), !Repository.getInstance().exists(), IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the current instance
	 * @return WriterManager
	 */
	/*public static WriterManager getInstance() {
		return instance;
	}*/
	
	public IndexWriter getIndexWriter(){
		return indexWriter;
	}
}
