/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.stagegenerator.builder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.squareup.javapoet.JavaFile;

/**
 * Abstract class for Java file building.
 *
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public abstract class Builder{

	/**
	 * Create a new builder class for the given Java package name, stage name, and stage abbreviation.
	 *
	 * @param pkg the package name (group id).
	 * @param name the stage name (artifact id).
	 * @param abbrev the stage abbreviation.
	 */
	public Builder(final String pkg, final String name, final String abbrev) {
		Pattern p = Pattern.compile("([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*");
		Matcher m = p.matcher(pkg);
		if (!m.matches()) {
			throw new IllegalArgumentException("'" + pkg + "' is not a valid Java package name.");
		} else {
			this.setPackageName(pkg);
		}

		p = Pattern.compile("[\\p{L}_$][\\p{L}\\p{N}_$]*");
		m = p.matcher(name);
		if (!m.matches()) {
			throw new IllegalArgumentException("'" + name + "' is not a valid stage name.");
		} else {
			this.setStageName(name);
		}

		p = Pattern.compile("[\\p{L}_$][\\p{L}\\p{N}_$]*");
		m = p.matcher(abbrev);
		if (!m.matches()) {
			throw new IllegalArgumentException("'" + name + "' is not a valid stage abbreviation.");
		} else {
			this.setAbbrev(abbrev);
		}
	}

	/**
	 * Build the class.
	 */
	public abstract JavaFile build();

	private String packageName;

	/**
	 * @return the packageName
	 */
	public final String getPackageName() {
		return packageName;
	}

	/**
	 * @param packageName the packageName to set
	 */
	private void setPackageName(final String packageName) {
		this.packageName = packageName;
	}

	private String stageName;

	/**
	 * @return the stageName
	 */
	public String getStageName() {
		return stageName;
	}

	/**
	 * @param stageName the stageName to set
	 */
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	private String abbrev;

	/**
	 * @return the abbrev
	 */
	public String getAbbrev() {
		return abbrev;
	}

	/**
	 * @param abbrev the abbrev to set
	 */
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

}
