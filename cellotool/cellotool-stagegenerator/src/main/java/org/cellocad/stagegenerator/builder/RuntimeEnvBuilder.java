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

import org.apache.commons.cli.Option;
import org.cellocad.common.runtime.environment.ArgDescription;
import org.cellocad.common.runtime.environment.ArgString;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.common.stage.runtime.environment.StageRuntimeEnv;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Builder for the RuntimeEnv class of a stage.
 *
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public class RuntimeEnvBuilder extends Builder{

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#RuntimeEnvBuilder(String,String,String)
	 */
	public RuntimeEnvBuilder(final String pkg, final String name, final String abbrev) {
		super(pkg,name,abbrev);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#build()
	 */
	public JavaFile build() {
		// get relevant classes
		ClassName myArgString = ClassName.get(this.getPackageName() + "."
													  + this.getStageName() + ".runtime.environment",
													  this.getAbbrev() + ArgString.class.getSimpleName());
		ClassName myArgDescription = ClassName.get(this.getPackageName() + "."
														   + this.getStageName() + ".runtime.environment",
														   this.getAbbrev() + ArgDescription.class.getSimpleName());

		// class def
		TypeSpec.Builder builder = TypeSpec.classBuilder(this.getAbbrev() + RuntimeEnv.class.getSimpleName())
			.addModifiers(javax.lang.model.element.Modifier.PUBLIC)
			.superclass(StageRuntimeEnv.class);

		// constructor
		MethodSpec method = null;
		method = MethodSpec
			.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.addParameter(String[].class,"args")
			.addStatement("super(args)")
			.build();
		builder.addMethod(method);

		method = getOptionGetter("InputNetlist",Option.class,myArgString,myArgDescription);
		builder.addMethod(method);

		method = getOptionGetter("ConfigFile",Option.class,myArgString,myArgDescription);
		builder.addMethod(method);

		method = getOptionGetter("OutputNetlist",Option.class,myArgString,myArgDescription);
		builder.addMethod(method);

		TypeSpec ts = builder.build();
		JavaFile javaFile = JavaFile.builder(this.getPackageName() + "."
											 + this.getStageName() + "."
											 + "runtime.environment",
											 ts).build();
		return javaFile;
	}

	/**
	 * Generate an option getter method.
	 *
	 * @param name the name of the option.
	 * @param option the option class.
	 * @param argString the name of the ArgString class for this stage.
	 * @param argDescription the name of the ArgDescription class for this stage.
	 * @return the MethodSpec for the getter.
	 */
	private MethodSpec getOptionGetter(String name, Class<?> option, ClassName argString, ClassName argDescription) {
		MethodSpec rtn = MethodSpec
			.methodBuilder("get" + name + option.getSimpleName())
			.addModifiers(Modifier.PROTECTED)
			.returns(option)
			.addStatement("$T rtn = new $T($T.$L, $L, $T.$L_DESCRIPTION)",
						  option,
						  option,
						  argString,
						  name.toUpperCase(),
						  "true",
						  argDescription,
						  name.toUpperCase())
			.addStatement("this.makeRequired(rtn)")
			.addStatement("return rtn")
			.build();
		return rtn;
	}
}
