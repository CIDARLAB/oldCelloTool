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
public class RuntimeObjectBuilder extends Builder{

	public RuntimeObjectBuilder(final String pkg, final String name, final String abbrev) {
		super(pkg,name,abbrev);
	}

	public JavaFile build() {
		// get relevant classes
		Class<?> stageConfigurationClass = null;
		Class<?> targetDataClass = null;
		Class<?> netlistClass = null;
		Class<?> runtimeEnvClass = null;
		Class<?> algorithmProfileClass = null;
		Class<?> algorithmClass = null;
		Class<?> algorithmFactoryClass = null;
		Class<?> runtimeExceptionClass = null;
		Class<?> runtimeObjectClass = null;
		try {
			stageConfigurationClass = Class.forName("org.cellocad.common.stage.StageConfiguration");
			targetDataClass = Class.forName("org.cellocad.common.target.data.TargetData");
			netlistClass = Class.forName("org.cellocad.common.netlist.Netlist");
			runtimeEnvClass = Class.forName("org.cellocad.common.runtime.environment.RuntimeEnv");
			algorithmProfileClass = Class.forName("org.cellocad.common.profile.AlgorithmProfile");
			algorithmClass = Class.forName("org.cellocad.common.algorithm.Algorithm");
			algorithmFactoryClass = Class.forName("org.cellocad.common.algorithm.AlgorithmFactory");
			runtimeExceptionClass = Class.forName("java.lang.RuntimeException");
			runtimeObjectClass = Class.forName("org.cellocad.common.runtime.RuntimeObject");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ClassName stageAlgorithmFactoryClass = ClassName.get(this.getPackageName() + "."
															 + this.getStageName() + ".algorithm",
															 this.getAbbrev() + algorithmFactoryClass.getSimpleName());
		ClassName stageAlgorithmClass = ClassName.get(this.getPackageName() + "."
													  + this.getStageName() + ".algorithm",
													  this.getAbbrev() + algorithmClass.getSimpleName());

		// class def
		TypeSpec.Builder builder = TypeSpec.classBuilder(this.getAbbrev() + runtimeObjectClass.getSimpleName())
			.addModifiers(javax.lang.model.element.Modifier.PUBLIC)
			.superclass(runtimeObjectClass);

		MethodSpec method = null;

		// constructor
		method = MethodSpec
			.methodBuilder(this.getAbbrev() + "RuntimeObject")
			.addModifiers(Modifier.PUBLIC)
			.addParameter(stageConfigurationClass,"stageConfiguration",Modifier.FINAL)
			.addParameter(targetDataClass,"targetData",Modifier.FINAL)
			.addParameter(netlistClass,"netlist",Modifier.FINAL)
			.addParameter(runtimeEnvClass,"runEnv",Modifier.FINAL)
			.addStatement("super(stageConfiguration, targetData, netlist, runEnv)")
			.build();
		builder.addMethod(method);

		// run
		method = MethodSpec
			.methodBuilder("run")
			.addAnnotation(Override.class)
			.addModifiers(Modifier.PROTECTED)
			.returns(void.class)
			.addComment(algorithmProfileClass.getSimpleName())
			.addStatement(BuilderUtils.instantiateByCall(algorithmProfileClass.getSimpleName(),
																"AProfile",
																"this.get"
																+ stageConfigurationClass.getSimpleName()
																+ "().get"
																+ algorithmProfileClass.getSimpleName()))
			.addComment("run " + algorithmClass.getSimpleName())
			.addStatement(BuilderUtils.instantiateByNew(stageAlgorithmFactoryClass.simpleName(),
														 this.getStageName().substring(0,1) + "AF",
														 stageAlgorithmFactoryClass.simpleName()))
			.addStatement(BuilderUtils.instantiateByCall(stageAlgorithmClass.simpleName(),
														 "algo",
														 this.getStageName().substring(0,1)
														 + "AF.get"
														 + algorithmClass.getSimpleName(),
														 "AProfile"))
			.beginControlFlow("if (algo == null)")
			.addStatement("throw new "
						  + runtimeExceptionClass.getSimpleName()
						  + "(\""
						  + algorithmClass.getSimpleName()
						  + " not found!\")")
			.endControlFlow()
			.addStatement(BuilderUtils.methodCall("algo.execute",
												  "this.get"
												  + netlistClass.getSimpleName()
												  + "()",
												  "this.get"
												  + targetDataClass.getSimpleName()
												  + "()",
												  "AProfile",
												  "this.get"
												  + runtimeEnvClass.getSimpleName()
												  + "())"))
			.build();
		builder.addMethod(method);

		TypeSpec factory = builder.build();
		JavaFile javaFile = JavaFile.builder(this.getPackageName() + "."
											 + this.getStageName() + ".runtime",
											 factory).build();
		return javaFile;
	}

}
