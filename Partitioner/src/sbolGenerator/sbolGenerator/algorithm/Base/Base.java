/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sbolGenerator.algorithm.Base;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;
import org.virtualparts.VPRException;
import org.virtualparts.VPRTripleStoreException;
import org.virtualparts.data.SBOLInteractionAdder_GeneCentric;

import common.CObject;
import common.CObjectCollection;
import common.netlist.Netlist;
import common.netlist.NetlistNode;

import sbolGenerator.algorithm.SGAlgorithm;
import sbolGenerator.common.UCFReader;
import sbolGenerator.data.Gate;
import sbolGenerator.data.Part;
import sbolGenerator.data.PartType;
import sbolGenerator.data.RepositoryType;

/**
 * @author: Timothy Jones
 *
 * @author
 * @date: Feb 27, 2018
 * @version
 *
 */
public class Base extends SGAlgorithm{

	@Override
	protected void setDefaultParameterValues() {
		this.setRepositoryType(RepositoryType.SYNBIOHUB);
		try {
			URL url = new URL("https://synbiohub.programmingbiology.org/");
			this.setRepositoryUrl(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.setSbolFilename("design.sbol");
	}

	@Override
	protected void setParameterValues() {
		String repositoryUrlParameter = this.getAlgorithmProfile().getStringParameter("repository_url").getSecond();
		String repositoryTypeParameter = this.getAlgorithmProfile().getStringParameter("repository_type").getSecond();
		if (repositoryTypeParameter.equals("synbiohub")) {
			this.setRepositoryType(RepositoryType.SYNBIOHUB);
		} else {
			throw new RuntimeException("Unknown repository type.");
		}
		try {
			URL repositoryUrl = new URL(repositoryUrlParameter);
			this.setRepositoryUrl(repositoryUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed repository url.");
		}

		this.setPartLibrary(UCFReader.getParts(this.getTargetData()));
		this.setGateLibrary(UCFReader.getGates(this.getTargetData()));
	}

	@Override
	protected void validateParameterValues() {		
	}

	@Override
	protected void preprocessing() {
	}
	
	@Override
	protected void run() {
		Netlist netlist = this.getNetlist();
		try {
			SBOLDocument sbolDocument = this.createSBOLDocument(netlist);
			this.setSbolDocument(sbolDocument);
		} catch (SynBioHubException | SBOLValidationException e) {
			e.printStackTrace();
		}
		try {
			SBOLDocument sbolDocument = generateModel(this.getRepositoryUrl().toString(),this.getSbolDocument());
			this.setSbolDocument(sbolDocument);
		} catch (IOException | SBOLValidationException | SBOLConversionException | VPRException | VPRTripleStoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void postprocessing() {
		try {
			SBOLWriter.write(this.getSbolDocument(),getSbolFilename());
		} catch (SBOLConversionException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create an SBOL document for the netlist.
	 * @param netlist - The netlist from which to build the SBOLDocument.
	 * @return The generated SBOLDocument.
	 * @throws SynBioHubException - Unable to fetch SBOL from SynBioHub for a part.
	 * @throws SBOLValidationException - Unable to create Component or ComponentDefinition.
	 */
	private SBOLDocument createSBOLDocument(Netlist netlist) throws SynBioHubException, SBOLValidationException {
		SBOLDocument sbolDocument = new SBOLDocument();
		URI gateTypeUri = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");
		
		Set<String> uniquePartNames = new HashSet<>();

		for (int i = 0; i < netlist.getNumVertex(); i++) {
            NetlistNode node = netlist.getVertexAtIdx(i);
			if (!node.getNodeType().equals("TopOutput")) {
				for (CObject part : node.getParts()) {
					uniquePartNames.add(part.getName());
				}
			}
		}

		SynBioHubFrontend frontend = null;
		if (this.getRepositoryUrl() != null) {
			frontend = new SynBioHubFrontend(this.getRepositoryUrl().toString());
		}
		
		// create part component definitions
		sbolDocument.setDefaultURIprefix("http://cellocad.org/v2");
		for (String partName : uniquePartNames) {
			Part p = this.getPartLibrary().findCObjectByName(partName);
			if ( p == null ) {
				throw new RuntimeException(partName + " not found in TargetData.");
			}
			URI partUri = p.getUri();
			ComponentDefinition cd = null;
			if ((partUri != null) && (this.getRepositoryType() == RepositoryType.SYNBIOHUB)) {
				SBOLDocument partSbol = frontend.getSBOL(partUri);
				cd = partSbol.getComponentDefinition(partUri);
				if (cd != null) {
					sbolDocument.createCopy(cd);
					Set<Sequence> sequences = cd.getSequences();
					if (sequences != null) {
						for (Sequence s : sequences) {
							sbolDocument.createCopy(s);
						}
					}
				} else {
					URI partTypeUri = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");
					cd = sbolDocument.createComponentDefinition(partName,partTypeUri);
					p.setUri(cd.getIdentity());
				}
			} else {
				URI partTypeUri = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");
				cd = sbolDocument.createComponentDefinition(partName,partTypeUri);
				p.setUri(cd.getIdentity());
			}
		}

		// create transcriptional unit component definitions
        for (int i = 0; i < netlist.getNumVertex(); i++) {
            NetlistNode node = netlist.getVertexAtIdx(i);
            if (!node.getNodeType().equals("TopInput") && !node.getNodeType().equals("TopOutput")) {
				List<String> txnUnit = new ArrayList<>();
				String unitNamePrefix = "";
                for (int j = 0; j < node.getNumInEdge(); j++) {
                    NetlistNode upstreamNode = node.getInEdgeAtIdx(j).getSrc();
                    for (CObject part : upstreamNode.getParts()) {
						if (part.getType() == PartType.PROMOTER.ordinal()) {
							txnUnit.add(part.getName());
							unitNamePrefix += part.getName() + "_";
						}
					}
                }
				for (CObject part : node.getParts()) {
					if (part.getType() != PartType.PROMOTER.ordinal()) {
						txnUnit.add(part.getName());
					}
				}
				URI txnUnitUri = URI.create("http://cellocad.org/v2/" + unitNamePrefix + node.getGate());
				ComponentDefinition cd = null;
				cd = sbolDocument.createComponentDefinition(unitNamePrefix + node.getGate(),txnUnitUri);
				for (String partName : txnUnit) {
					Part p = this.getPartLibrary().findCObjectByName(partName);
					cd.createComponent(partName,AccessType.PUBLIC,p.getUri());
				}
            }
		}
		return sbolDocument;
	}

	/**
	 * Perform VPR model generation. 
	 * @param selectedRepo - The specified synbiohub repository the user wants VPR model generator to connect to. 
	 * @param generatedModel - The file to generate the model from.
	 * @return The generated model.
	 * @throws SBOLValidationException
	 * @throws IOException - Unable to read or write the given SBOLDocument
	 * @throws SBOLConversionException - Unable to perform conversion for the given SBOLDocument.
	 * @throws VPRException - Unable to perform VPR Model Generation on the given SBOLDocument.
	 * @throws VPRTripleStoreException - Unable to perform VPR Model Generation on the given SBOLDocument.
	 */
	public static SBOLDocument generateModel(String selectedRepo, SBOLDocument generatedModel) throws SBOLValidationException, IOException, SBOLConversionException, VPRException, VPRTripleStoreException
	{ 
		//"https://synbiohub.org/sparql"
		String endpoint = selectedRepo + "/sparql";
		SBOLInteractionAdder_GeneCentric interactionAdder = new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		interactionAdder.addInteractions(generatedModel);
		return generatedModel;
	}
	
	/*
	 * Getter and Setter
	 */
	/**
	 * @return the repositoryType
	 */
	public RepositoryType getRepositoryType() {
		return repositoryType;
	}

	/**
	 * @param repositoryType the repositoryType to set
	 */
	public void setRepositoryType(RepositoryType repositoryType) {
		this.repositoryType = repositoryType;
	}

	/**
	 * @return the repositoryUrl
	 */
	public URL getRepositoryUrl() {
		return repositoryUrl;
	}

	/**
	 * @param repositoryUrl the repositoryUrl to set
	 */
	public void setRepositoryUrl(URL repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	/**
	 * @return the partLibrary
	 */
	public CObjectCollection<Part> getPartLibrary() {
		return partLibrary;
	}

	/**
	 * @param partLibrary the partLibrary to set
	 */
	public void setPartLibrary(CObjectCollection<Part> partLibrary) {
		this.partLibrary = partLibrary;
	}

	/**
	 * @return the gateLibrary
	 */
	public CObjectCollection<Gate> getGateLibrary() {
		return gateLibrary;
	}

	/**
	 * @param gateLibrary the gateLibrary to set
	 */
	public void setGateLibrary(CObjectCollection<Gate> gateLibrary) {
		this.gateLibrary = gateLibrary;
	}
	
	/**
	 * @return the sbolDocument
	 */
	public SBOLDocument getSbolDocument() {
		return sbolDocument;
	}

	/**
	 * @param sbolDocument the sbolDocument to set
	 */
	public void setSbolDocument(SBOLDocument sbolDocument) {
		this.sbolDocument = sbolDocument;
	}

	/**
	 * @return the sbolFilename
	 */
	public String getSbolFilename() {
		return sbolFilename;
	}

	/**
	 * @param sbolFilename the sbolFilename to set
	 */
	public void setSbolFilename(String sbolFilename) {
		this.sbolFilename = sbolFilename;
	}

	private RepositoryType repositoryType;
	private URL repositoryUrl;
	private CObjectCollection<Part> partLibrary;
	private CObjectCollection<Gate> gateLibrary;
	private SBOLDocument sbolDocument;
	private String sbolFilename;
	
}
