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
package common.runtime.environment;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import common.CObject;
import common.Utils;

/**
 * @author: Vincent Mirian
 * 
 * @date: Nov 13, 2017
 *
 */
public class RuntimeEnv extends CObject{
	
	private void init(){
		this.parser = new DefaultParser();
		this.options = new Options();		
	}
	
	public RuntimeEnv(final String[] args){
		super();
		init();
		this.setOptions();
		try {
			line = parser.parse(this.getOptions(), args);
	    }
	    catch(ParseException e) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
	    }
		if (this.hasOption(ArgString.HELP)) {
			this.printHelp();
			Utils.exit(0);
		}
	}
	
	// print help
	public void printHelp() {
		 HelpFormatter formatter = new HelpFormatter();
		 String name = this.getName();
		 if (name.isEmpty()) {
			 name = "\"EXECUTABLE\"";
		 }
		 formatter.printHelp(name, this.getOptions(), true);
	}

	// getter and setter
	protected Options getOptions() {
		return this.options;
	}
	
	protected void setOptions() {
		Options options = this.getOptions();
		options.addOption(this.getHelpOption());
		options.addOption(this.getTargetDataFileOption());
		options.addOption(this.getTargetDataDirOption());
	}

	/*
	 * Options
	 */
	private Option getHelpOption(){
		Option rtn = new Option( ArgString.HELP, false, ArgDescription.HELP_DESCRIPTION);
		return rtn;
	}
	
	private Option getTargetDataFileOption(){
		Option rtn = new Option( ArgString.TARGETDATAFILE, true, ArgDescription.TARGETDATAFILE_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}
	
	private Option getTargetDataDirOption(){
		Option rtn = new Option( ArgString.TARGETDATADIR, true, ArgDescription.TARGETDATADIR_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}
	
	// get Values
	protected String getDefault(final String str){
		String rtn = null;
		return rtn;
	}
	
	public String getOptionValue(final String str){
		String rtn = null;
		rtn = this.getDefault(str);
		rtn = line.getOptionValue(str);
		return rtn;
	}
	
	public String getOptionValue(final char c){
		String rtn = null;
		rtn = line.getOptionValue(c);
		return rtn;
	}
	
	public boolean hasOption(final String str){
		boolean rtn = false;
		rtn = line.hasOption(str);
		return rtn;
	}
	
	public boolean hasOption(final char c){
		boolean rtn = false;
		rtn = line.hasOption(c);
		return rtn;
	}

	protected void makeRequired(final Option arg){
		arg.setRequired(true);		
	}
	    
	private CommandLineParser parser;
    private CommandLine line;
    private Options options;
}
