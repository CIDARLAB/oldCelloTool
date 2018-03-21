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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Builder for the Algorithm class in common.
 *
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public class RuntimeEnvBuilder extends Builder{

	public RuntimeEnvBuilder(final String pkg, final String name, final String abbrev) {
		super(pkg,name,abbrev);
	}

	public JavaFile build() {
		// get relevant classes
		Class<?> runtimeEnvClass = null;
		Class<?> stageRuntimeEnvClass = null;
		Class<?> optionClass = null;
		Class<?> argStringClass = null;
		Class<?> argDescriptionClass = null;
		try {
			runtimeEnvClass = Class.forName("org.cellocad.common.runtime.environment.RuntimeEnv");
			stageRuntimeEnvClass = Class.forName("org.cellocad.common.stage.runtime.environment.StageRuntimeEnv");
			optionClass = Class.forName("org.apache.commons.cli.Option");
			argStringClass = Class.forName("org.cellocad.common.runtime.environment.ArgString");
			argDescriptionClass = Class.forName("org.cellocad.common.runtime.environment.ArgDescription");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ClassName stageArgStringClass = ClassName.get(this.getPackageName() + "."
													  + this.getStageName() + ".runtime.environment",
													  this.getAbbrev() + argStringClass.getSimpleName());
		ClassName stageArgDescriptionClass = ClassName.get(this.getPackageName() + "."
														   + this.getStageName() + ".runtime.environment",
														   this.getAbbrev() + argDescriptionClass.getSimpleName());

		// class def
		TypeSpec.Builder builder = TypeSpec.classBuilder(this.getAbbrev() + runtimeEnvClass.getSimpleName())
			.addModifiers(javax.lang.model.element.Modifier.PUBLIC)
			.superclass(stageRuntimeEnvClass);

		MethodSpec method = null;
		method = MethodSpec
			.methodBuilder(this.getAbbrev() + runtimeEnvClass.getSimpleName())
			.addModifiers(Modifier.PUBLIC)
			.addParameter(String[].class,"args")
			.addStatement("super(args)")
			.build();
		builder.addMethod(method);

		method = getOptionGetter("InputNetlist", optionClass,stageArgStringClass.simpleName(),stageArgDescriptionClass.simpleName());
		builder.addMethod(method);

		method = getOptionGetter("ConfigFile", optionClass,stageArgStringClass.simpleName(),stageArgDescriptionClass.simpleName());
		builder.addMethod(method);

		method = getOptionGetter("OutputNetlist", optionClass,stageArgStringClass.simpleName(),stageArgDescriptionClass.simpleName());
		builder.addMethod(method);

		TypeSpec ts = builder.build();
		JavaFile javaFile = JavaFile.builder(this.getPackageName() + "."
											 + this.getStageName() + "."
											 + "runtime.environment",
											 ts).build();
		return javaFile;
	}

	private MethodSpec getOptionGetter(String name, Class<?> option, String argString, String argDescription) {
		MethodSpec rtn = MethodSpec
			.methodBuilder("get" + name + "Option")
			.addModifiers(Modifier.PROTECTED)
			.returns(option)
			.addStatement(BuilderUtils.classInstantiationByNew(option.getSimpleName(),
															   "rtn",
															   option.getSimpleName(),
															   argString + "." + name.toUpperCase(),
															   "true",
															   argDescription + "." + name.toUpperCase() + "_DESCRIPTION"))
			.addStatement(BuilderUtils.methodCall("this.makeRequired",
												  "true"))
			.addStatement("return rtn")
			.build();
		return rtn;
	}
}
