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
package org.cellocad.sbolgenerator.algorithm.Base;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Utils;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistNode;
import org.cellocad.sbolgenerator.algorithm.SGAlgorithm;
import org.cellocad.sbolgenerator.common.UCFReader;
import org.cellocad.sbolgenerator.data.Gate;
import org.cellocad.sbolgenerator.data.Part;
import org.cellocad.sbolgenerator.data.PartType;
import org.cellocad.sbolgenerator.data.RepositoryType;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.Component;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core2.SequenceOntology;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;
import org.virtualparts.VPRException;
import org.virtualparts.VPRTripleStoreException;
import org.virtualparts.data.SBOLInteractionAdder_GeneCentric;

/**
 * @author: Timothy Jones
 *
 * @date: Feb 27, 2018
 *
 */
public class Base extends SGAlgorithm{

	@Override
	protected void setDefaultParameterValues() {
		this.setRepositoryType(RepositoryType.SYNBIOHUB);
		try {
			URL url = new URL("https://synbiohub.utah.edu/");
			this.setRepositoryUrl(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.setSbolFilename(this.getRuntimeEnv().getOptionValue("outputDir")
							 + Utils.getFileSeparator()
							 + this.getNetlist().getName() + ".xml");
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
		logInfo("creating SBOL document");
		try {
			SBOLDocument sbolDocument = this.createSBOLDocument(netlist);
			this.setSbolDocument(sbolDocument);
		} catch (SynBioHubException | SBOLValidationException e) {
			e.printStackTrace();
		}
		logInfo("modeling component interactions");
		try {
			SBOLDocument sbolDocument = generateModel(this.getRepositoryUrl().toString(),this.getSbolDocument());
			this.setSbolDocument(sbolDocument);
		} catch (IOException | SBOLValidationException | SBOLConversionException | VPRException | VPRTripleStoreException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void postprocessing() {
		logInfo("writing SBOL document");
		logDebug("SBOL filename " + getSbolFilename());
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
					cd = sbolDocument.createComponentDefinition(partName,ComponentDefinition.DNA);
					p.setUri(cd.getIdentity());
				}
			} else {
				cd = sbolDocument.createComponentDefinition(partName,ComponentDefinition.DNA);
				p.setUri(cd.getIdentity());
			}
		}

		// create transcriptional unit component definitions and sequences
        for (int i = 0; i < netlist.getNumVertex(); i++) {
            NetlistNode node = netlist.getVertexAtIdx(i);
            if (!node.getNodeType().equals("TopInput") && !node.getNodeType().equals("TopOutput")) {
				// build transcriptional unit
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
				ComponentDefinition cd = null;
				cd = sbolDocument.createComponentDefinition(unitNamePrefix + node.getGate(),ComponentDefinition.DNA);
				cd.addRole(SequenceOntology.ENGINEERED_REGION);
				// for (String partName : txnUnit) {
				int seqCounter = 1;
				String sequence = "";
				for (int j = 0; j < txnUnit.size(); j++) {
					String partName = txnUnit.get(j);
					Part p = this.getPartLibrary().findCObjectByName(partName);
					Component c = cd.createComponent(partName,AccessType.PUBLIC,p.getUri());
					SequenceAnnotation sa =
						cd.createSequenceAnnotation("SequenceAnnotation" + String.valueOf(j),
													"SequenceAnnotation" + String.valueOf(j) + "_Range",
													seqCounter,
													seqCounter + p.getSequence().length());
					sa.setComponent(c.getIdentity());
					seqCounter += p.getSequence().length() + 1;
					sequence += p.getSequence();
					if (j != 0) {
						cd.createSequenceConstraint(cd.getDisplayId() + "Constraint" + String.valueOf(j),
													RestrictionType.PRECEDES,
													cd.getComponent(txnUnit.get(j-1)).getIdentity(),
													cd.getComponent(partName).getIdentity());
					}
				}
				Sequence s = sbolDocument.createSequence(cd.getDisplayId() + "_sequence",sequence,Sequence.IUPAC_DNA);
				cd.addSequence(s);
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
	public static SBOLDocument generateModel(String selectedRepo, SBOLDocument generatedModel) throws SBOLValidationException, IOException, SBOLConversionException, VPRException, VPRTripleStoreException, URISyntaxException
	{
		URI endpoint = new URL(new URL(selectedRepo),"/sparql").toURI();
		SBOLInteractionAdder_GeneCentric interactionAdder = new SBOLInteractionAdder_GeneCentric(endpoint);
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
