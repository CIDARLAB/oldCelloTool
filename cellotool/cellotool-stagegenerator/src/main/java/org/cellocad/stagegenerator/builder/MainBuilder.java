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

import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.netlist.NetlistUtils;
import org.cellocad.common.runtime.RuntimeObject;
import org.cellocad.common.runtime.environment.ArgString;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.common.stage.StageConfiguration;
import org.cellocad.common.stage.StageUtils;
import org.cellocad.common.target.data.TargetData;
import org.cellocad.common.target.data.TargetDataUtils;

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
		String name = "";
		name = this.getPackageName() + "." + this.getStageName() + ".runtime.environment";
		ClassName myRuntimeEnv = ClassName.get(name,this.getAbbrev() + RuntimeEnv.class.getSimpleName());
		name = this.getPackageName() + "." + this.getStageName() + ".runtime.environment";
		ClassName myArgString = ClassName.get(name,this.getAbbrev() + ArgString.class.getSimpleName());
		name = this.getPackageName() + "."+ this.getStageName() + ".runtime";
		ClassName myRuntimeObject = ClassName.get(name,this.getAbbrev() + RuntimeObject.class.getSimpleName());

		// class def
		TypeSpec.Builder builder = TypeSpec.classBuilder("Main")
			.addModifiers(javax.lang.model.element.Modifier.PUBLIC);

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
			.addStatement("$T $L = new $T(args)",
						  RuntimeEnv.class,
						  runEnvVar,
						  myRuntimeEnv)
			.addStatement("$L.setName($S)",runEnvVar,this.getStageName())
			.addComment("Read $L",Netlist.class.getSimpleName())
			.addStatement("$T $L = $T.get$L($L, $T.INPUTNETLIST)",
						  Netlist.class,
						  netlistVar,
						  NetlistUtils.class,
						  Netlist.class.getSimpleName(),
						  runEnvVar,
						  myArgString)
			.addComment("get $L",StageConfiguration.class.getSimpleName())
			.addStatement("$T $L = $T.get$L($L, $T.CONFIGFILE)",
						  StageConfiguration.class,
						  stageConfigurationVar,
						  StageUtils.class,
						  StageConfiguration.class.getSimpleName(),
						  runEnvVar,
						  myArgString)
			.addComment("get $L",TargetData.class.getSimpleName())
			.addStatement("$T $L = $T.getTarget$L($L, $T.TARGETDATAFILE, $T.TARGETDATADIR)",
						  TargetData.class,
						  targetDataVar,
						  TargetDataUtils.class,
						  TargetData.class.getSimpleName(),
						  runEnvVar,
						  myArgString,
						  myArgString)
			.addComment("Execute")
			.addStatement("$T $L = new $T($L, $L, $L, $L)",
						  myRuntimeObject,
						  this.getAbbrev(),
						  myRuntimeObject,
						  stageConfigurationVar,
						  targetDataVar,
						  netlistVar,
						  runEnvVar)
			.addStatement("$L.setName($S)",this.getAbbrev(),this.getStageName())
			.addStatement("$L.execute()",this.getAbbrev())
			.addComment("Write $L",Netlist.class.getSimpleName())
			.addStatement("$T $L = $L.getOptionValue($T.OUTPUTNETLIST)",
						  String.class,
						  outputFilenameVar,
						  runEnvVar,
						  myArgString)
			.addStatement("$T.writeJSONForNetlist($L,$L)",
						  NetlistUtils.class,
						  netlistVar,
						  outputFilenameVar)
			.build();

		builder.addMethod(method);
		TypeSpec ts = builder.build();
		name = this.getPackageName() + "." + this.getStageName() + "." + "runtime";
		JavaFile javaFile = JavaFile.builder(name,ts).build();
		return javaFile;
	}

}
