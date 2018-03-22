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

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

/**
 * Builder for the ArgDescription class of a stage.
 *
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public class ArgDescriptionBuilder extends Builder{

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#ArgDescriptionBuilder(String,String,String)
	 */
	public ArgDescriptionBuilder(final String pkg, final String name, final String abbrev) {
		super(pkg,name,abbrev);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#build()
	 */
	public JavaFile build() {
		// get relevant classes
		Class<?> argDescriptionClass = null;
		Class<?> stageArgDescriptionClass = null;
		try {
			argDescriptionClass = Class.forName("org.cellocad.common.runtime.environment.ArgDescription");
			stageArgDescriptionClass = Class.forName("org.cellocad.common.stage.runtime.environment.StageArgDescription");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// class def
		TypeSpec.Builder builder = TypeSpec.classBuilder(this.getAbbrev() + argDescriptionClass.getSimpleName())
			.addModifiers(javax.lang.model.element.Modifier.PUBLIC)
			.superclass(stageArgDescriptionClass);

		FieldSpec f = null;
		f = FieldSpec.builder(String.class,"CONFIGFILE" + this.getAbbrev() + "_DESCRIPTION")
			.addModifiers(Modifier.FINAL,Modifier.STATIC,Modifier.PUBLIC)
			.initializer("$S","config file for " + this.getStageName() + " (" + this.getAbbrev() + ")")
			.build();
		builder.addField(f);
		f = FieldSpec.builder(String.class,"INPUTNETLIST" + this.getAbbrev() + "_DESCRIPTION")
			.addModifiers(Modifier.FINAL,Modifier.STATIC,Modifier.PUBLIC)
			.initializer("$S","input netlist file for " + this.getStageName() + " (" + this.getAbbrev() + ")")
			.build();
		builder.addField(f);
		f = FieldSpec.builder(String.class,"OUTPUTNETLIST" + this.getAbbrev() + "_DESCRIPTION")
			.addModifiers(Modifier.FINAL,Modifier.STATIC,Modifier.PUBLIC)
			.initializer("$S","output netlist file for " + this.getStageName() + " (" + this.getAbbrev() + ")")
			.build();
		builder.addField(f);

		TypeSpec ts = builder.build();
		JavaFile javaFile = JavaFile.builder(this.getPackageName() + "."
											 + this.getStageName() + "."
											 + "runtime.environment",
											 ts).build();
		return javaFile;
	}
}
