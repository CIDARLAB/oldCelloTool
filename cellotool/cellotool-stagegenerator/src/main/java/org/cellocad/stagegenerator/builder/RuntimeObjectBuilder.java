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

import org.cellocad.common.algorithm.Algorithm;
import org.cellocad.common.algorithm.AlgorithmFactory;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.profile.AlgorithmProfile;
import org.cellocad.common.runtime.RuntimeObject;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.common.stage.StageConfiguration;
import org.cellocad.common.target.data.TargetData;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Builder for the RuntimeObject of a stage.
 *
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public class RuntimeObjectBuilder extends Builder{

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#RuntimeObjectBuilder(String,String,String)
	 */
	public RuntimeObjectBuilder(final String pkg, final String name, final String abbrev) {
		super(pkg,name,abbrev);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#build()
	 */
	public JavaFile build() {
		// get relevant classes
		String name = "";
		name = this.getPackageName() + "." + this.getStageName() + ".algorithm";
		ClassName myAlgorithmFactory = ClassName.get(name,this.getAbbrev() + AlgorithmFactory.class.getSimpleName());
		name = this.getPackageName() + "." + this.getStageName() + ".algorithm";
		ClassName myAlgorithm = ClassName.get(name,this.getAbbrev() + Algorithm.class.getSimpleName());

		// class def
		TypeSpec.Builder builder = TypeSpec.classBuilder(this.getAbbrev() + RuntimeObject.class.getSimpleName())
			.addModifiers(javax.lang.model.element.Modifier.PUBLIC)
			.superclass(RuntimeObject.class);

		MethodSpec method = null;

		// constructor
		method = MethodSpec
			.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.addParameter(StageConfiguration.class,"stageConfiguration",Modifier.FINAL)
			.addParameter(TargetData.class,"targetData",Modifier.FINAL)
			.addParameter(Netlist.class,"netlist",Modifier.FINAL)
			.addParameter(RuntimeEnv.class,"runEnv",Modifier.FINAL)
			.addStatement("super(stageConfiguration, targetData, netlist, runEnv)")
			.build();
		builder.addMethod(method);

		// run
		method = MethodSpec
			.methodBuilder("run")
			.addAnnotation(Override.class)
			.addModifiers(Modifier.PROTECTED)
			.returns(void.class)
			.addComment(AlgorithmProfile.class.getSimpleName())
			.addStatement("$T AProfile = this.get$L().get$L()",
						  AlgorithmProfile.class,
						  StageConfiguration.class.getSimpleName(),
						  AlgorithmProfile.class.getSimpleName())
			.addComment("run $L",Algorithm.class.getSimpleName())
			.addStatement("$T $L = new $T()",
						  myAlgorithmFactory,
						  this.getStageName().substring(0,1) + "AF",
						  myAlgorithmFactory)
			.addStatement("$T algo = $L.get$L(AProfile)",
						  myAlgorithm,
						  this.getStageName().substring(0,1) + "AF",
						  Algorithm.class.getSimpleName())
			.beginControlFlow("if (algo == null)")
			.addStatement("throw new $L(\"$L not found!\")",
						  RuntimeException.class.getSimpleName(),
						  Algorithm.class.getSimpleName())
			.endControlFlow()
			.addStatement("algo.execute(this.get$L(), this.get$L(), $L, this.get$L())",
						  Netlist.class.getSimpleName(),
						  TargetData.class.getSimpleName(),
						  "AProfile",
						  RuntimeEnv.class.getSimpleName())
			.build();
		builder.addMethod(method);

		TypeSpec ts = builder.build();
		name = this.getPackageName() + "." + this.getStageName() + ".runtime";
		JavaFile javaFile = JavaFile.builder(name,ts).build();

		return javaFile;
	}

}
