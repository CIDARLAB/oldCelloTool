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
package partition.testing;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import common.runtime.environment.RuntimeEnv;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 28, 2017
 *
 */

//TODO: create runtime environment, then pass to each stage of CAD tool
public class PTestingEnv extends RuntimeEnv{

	
	public PTestingEnv(final String[] args){
		super(args);
	}
	
	private Option getHelpOption(){
		Option rtn = new Option( HELP, "print this message" );
		return rtn;		
	}

	private Option getProfileFilenameOption(){
		Option rtn = new Option( PROFILEFILENAME, true, "profile filename" );
		this.makeRequired(rtn);
		return rtn;		
	}

	private Option getProfileNameOption(){
		Option rtn = new Option( PROFILENAME, true, "profile name" );
		this.makeRequired(rtn);
		return rtn;		
	}
	
	private Option getGraphFilenameOption(){
		Option rtn = new Option( GRAPHFILENAME, true, "graph filename" );
		this.makeRequired(rtn);
		return rtn;		
	}
	
	private Option getCelloDirOption(){
		Option rtn = new Option( CELLODIR, true, "cello directory top directory" );
		this.makeRequired(rtn);
		return rtn;		
	}
	
	protected Options getOptions(){
		Options rtn = new Options();
		// help
		rtn.addOption(this.getHelpOption());
		// profileFilename
		rtn.addOption(this.getProfileFilenameOption());
		// profileName
		rtn.addOption(this.getProfileNameOption());
		// graphFilename
		rtn.addOption(this.getGraphFilenameOption());
		// celloDir
		rtn.addOption(this.getCelloDirOption());
		return rtn;
	}

    static public String HELP = "help";
    static public String PROFILEFILENAME= "profileFilename";
    static public String PROFILENAME = "profileName";
    static public String GRAPHFILENAME = "graphFilename";
    static public String CELLODIR = "celloDir";
}
