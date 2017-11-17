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
package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 28, 2017
 *
 */
public class Utils {
	
	static public String getFilename(final String name){
		String rtn = name;
		int index = 0;
		index = rtn.lastIndexOf(System.getProperty("file.separator"));
		if (index != -1){
			rtn = rtn.substring(index + 1);
		}
		index = rtn.lastIndexOf(".");
		if ((index != -1) && (rtn.length() > 1)){
			rtn = rtn.substring(0, index);
		}
		return rtn;
	}


	public static boolean isMac() {
		return (Utils.isMac(Utils.getOS()));
	}

	public static boolean isUnix() {
		return (Utils.isUnix(Utils.getOS()));
	}
	
	private static String getOS() {
		return System.getProperty("os.name");
	}

	private static boolean isMac(final String OS) {
		return (OS.toLowerCase().indexOf("mac") >= 0);
	}

	private static boolean isUnix(final String OS) {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}

	private static Process executeAndWaitForCommand(final String cmd){
		Process rtn = null;
		try{
			rtn = Runtime.getRuntime().exec(cmd);
			rtn.waitFor();
		}catch(InterruptedException | IOException e){
	        e.printStackTrace();
		}
		return rtn;
	}
	
	static public boolean executeDot2PDFShellFilename(final String filename){
		final String script = "dot2pdf.sh";
		boolean rtn = false;
		// supported OS (Mac and Unix)
		if (Utils.isMac() || Utils.isUnix()){
			Process proc = executeAndWaitForCommand("which " + script);
			// dot2pdf.sh exists
			if (proc.exitValue() == 0)
			{
				try {
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					String cmdLocation = null;
					boolean contains = false;
					while ((!contains) && ((cmdLocation = stdInput.readLine()) != null)) {
					    contains = cmdLocation.contains(script);
					}
					if (contains) {
						proc = executeAndWaitForCommand("bash " + cmdLocation + " " + filename);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	    return rtn;
	}
	
	static public String getTimeString(){
		String rtn;
        Date date = new Date();
        rtn = String.valueOf(date.getTime());
		return rtn;
	}

	static public String getWorkingDirectory() {
		String rtn = System.getProperty("line.separator").toString();
		return rtn;
	}
	
	static public String getNewLine() {
		String rtn = System.getProperty("line.separator").toString();
		return rtn;
	}

	static public String addIndent(int numIndent, final String str) {
		String rtn = "";
		String replace = "";
		String numTab = Utils.getTabCharacterRepeat(numIndent);
		replace = Utils.getNewLine() + numTab;
		rtn = numTab + str.replace(Utils.getNewLine(), replace);
		return rtn;		
	}

	static public String getTabCharacter() {
		String rtn = "\t";
		return rtn;
	}

	static public String getTabCharacterRepeat(int num) {
		String rtn = "";
		String tab = Utils.getTabCharacter();
		for (int i = 0; i < num; i++) {
			rtn = rtn + tab;	
		}		
		return rtn;
	}

	static public boolean isBoolean(final Object Obj) {
		boolean rtn = false;
		rtn = (Obj instanceof Boolean);
		return rtn;
	}

	static public boolean isLong(final Object Obj) {
		boolean rtn = false;
		rtn = (Obj instanceof Long);
		return rtn;
	}

	static public boolean isDouble(final Object Obj) {
		boolean rtn = false;
		rtn = (Obj instanceof Double);
		return rtn;
	}

	static public boolean isString(final Object Obj) {
		boolean rtn = false;
		rtn = (Obj instanceof String);
		return rtn;
	}

	static public boolean isNullRuntimeException(final CObject cObj, String name) {
		boolean rtn = false;
		rtn = (cObj == null);
		if (rtn) {
			throw new RuntimeException(name + " cannot be null!");
		}
		return rtn;
	}
	
}
