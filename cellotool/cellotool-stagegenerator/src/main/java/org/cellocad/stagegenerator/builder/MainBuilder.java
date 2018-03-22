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
 * Builder for the Main class of a stage.
 *
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public class MainBuilder extends Builder{

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#MainBuilder(String,String,String)
	 */
	public MainBuilder(final String pkg, final String name, final String abbrev) {
		super(pkg,name,abbrev);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#build()
	 */
	public JavaFile build() {
		// get relevant classes
		Class<?> runtimeEnvClass = null;
		Class<?> netlistClass = null;
		Class<?> stageConfigurationClass = null;
		Class<?> stageUtilsClass = null;
		Class<?> targetDataClass = null;
		Class<?> targetDataUtilsClass = null;
		Class<?> argStringClass = null;
		Class<?> runtimeObjectClass = null;
		Class<?> netlistUtilsClass = null;
		try {
			runtimeEnvClass = Class.forName("org.cellocad.common.runtime.environment.RuntimeEnv");
			netlistClass = Class.forName("org.cellocad.common.netlist.Netlist");
			stageConfigurationClass = Class.forName("org.cellocad.common.stage.StageConfiguration");
			stageUtilsClass = Class.forName("org.cellocad.common.stage.StageUtils");
			targetDataClass = Class.forName("org.cellocad.common.target.data.TargetData");
			targetDataUtilsClass = Class.forName("org.cellocad.common.target.data.TargetDataUtils");
			argStringClass = Class.forName("org.cellocad.common.runtime.environment.ArgString");
			runtimeObjectClass = Class.forName("org.cellocad.common.runtime.RuntimeObject");
			netlistUtilsClass = Class.forName("org.cellocad.common.netlist.NetlistUtils");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ClassName stageRuntimeEnvClass = ClassName.get(this.getPackageName() + "."
													   + this.getStageName() + ".algorithm",
													   this.getAbbrev() + runtimeEnvClass.getSimpleName());
		ClassName stageArgStringClass = ClassName.get(this.getPackageName() + "."
													  + this.getStageName() + ".runtime.environment.",
													  this.getAbbrev() + argStringClass.getSimpleName());
		ClassName stageRuntimeObjectClass = ClassName.get(this.getPackageName() + "."
														  + this.getStageName() + ".runtime.environment.",
														  this.getAbbrev() + runtimeObjectClass.getSimpleName());

		// class def
		TypeSpec.Builder builder = TypeSpec.classBuilder("Main");
		builder.addModifiers(javax.lang.model.element.Modifier.PUBLIC);

		String runEnvVar = "runEnv";
		String stageConfigurationVar = "sc";
		String targetDataVar = "td";
		String netlistVar = "netlist";
		String outputFilenameVar = "outputFilename";
		MethodSpec method = MethodSpec
			.methodBuilder("main")
			.addModifiers(Modifier.PUBLIC,Modifier.STATIC)
			.returns(void.class)
			.addParameter(String[].class,"args")
			.addStatement(BuilderUtils.instantiateByNew(runtimeEnvClass.getSimpleName(),
															   runEnvVar,
															   stageRuntimeEnvClass.simpleName(),
															   "args"))
			.addStatement(BuilderUtils.methodCall(runEnvVar + ".setName",
												  "\"" + this.getStageName() + "\""))
			.addComment("Read "
						+ netlistClass.getSimpleName())
			.addStatement(BuilderUtils.instantiateByCall(netlistClass.getSimpleName(),
																netlistVar,
																netlistUtilsClass.getSimpleName()
																+ ".get"
																+ netlistClass.getSimpleName(),
																runEnvVar,
																stageArgStringClass.simpleName()
																+ ".INPUTNETLIST"))
			.addComment("get "
						+ stageConfigurationClass.getSimpleName())
			.addStatement(BuilderUtils.instantiateByCall(stageConfigurationClass.getSimpleName(),
																stageConfigurationVar,
																stageUtilsClass.getSimpleName()
																+ ".get"
																+ stageConfigurationClass.getSimpleName(),
																runEnvVar,
																stageArgStringClass.simpleName()
																+ ".CONFIGFILE)"))
			.addComment("get " + targetDataClass.getSimpleName())
			.addStatement(BuilderUtils.instantiateByCall(targetDataClass.getSimpleName(),
																targetDataVar,
																targetDataUtilsClass.getSimpleName()
																+ ".getTarget"
																+ targetDataClass.getSimpleName(),
																runEnvVar,
																stageArgStringClass.simpleName()
																+ ".TARGETDATAFILE",
																stageArgStringClass.simpleName()
																+ ".TARGETDATADIR"))
			.addComment("Execute")
			.addStatement(BuilderUtils.instantiateByNew(stageRuntimeObjectClass.simpleName(),
															   this.getAbbrev(),
															   stageRuntimeObjectClass.simpleName(),
															   stageConfigurationVar,
															   targetDataVar,
															   netlistVar,
															   runEnvVar))
			.addStatement(BuilderUtils.methodCall(this.getAbbrev() + ".setName","\"" + this.getStageName() + "\""))
			.addStatement(BuilderUtils.methodCall(this.getAbbrev() + ".execute"))
			.addComment("Write " + netlistClass.getSimpleName())
			.addStatement(BuilderUtils.instantiateByCall(String.class.getSimpleName(),
																outputFilenameVar,
																runEnvVar
																+ ".getOptionalValue",
																stageArgStringClass.simpleName()
																+ ".OUTPUTNETLIST"))
			.addStatement(BuilderUtils.methodCall(netlistUtilsClass.getSimpleName() + ".writeJSONForNetlist",
												  netlistVar,
												  outputFilenameVar))
			.build();

		builder.addMethod(method);
		TypeSpec main = builder.build();
		JavaFile javaFile = JavaFile.builder(this.getPackageName() + "."
											 + this.getStageName() + "."
											 + "runtime",
											 main).build();
		return javaFile;
	}

}
