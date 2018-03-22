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

import java.lang.reflect.Method;

import javax.lang.model.element.Modifier;

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
public class AlgorithmBuilder extends Builder{

	public AlgorithmBuilder(final String pkg, final String name, final String abbrev) {
		super(pkg,name,abbrev);
	}

	public JavaFile build() {
		// get relevant classes
		Class<?> algorithmClass = null;
		Class<?> netlistClass = null;
		Class<?> targetDataClass = null;
		Class<?> algorithmProfileClass = null;
		Class<?> runtimeEnvClass = null;
		try {
			algorithmClass = Class.forName("org.cellocad.common.algorithm.Algorithm");
			netlistClass = Class.forName("org.cellocad.common.netlist.Netlist");
			targetDataClass = Class.forName("org.cellocad.common.target.data.TargetData");
			algorithmProfileClass = Class.forName("org.cellocad.common.profile.AlgorithmProfile");
			runtimeEnvClass = Class.forName("org.cellocad.common.runtime.environment.RuntimeEnv");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// class def
		TypeSpec.Builder builder = TypeSpec.classBuilder(this.getAbbrev() + algorithmClass.getSimpleName());
		builder.addModifiers(javax.lang.model.element.Modifier.PUBLIC,
							 javax.lang.model.element.Modifier.ABSTRACT);

		builder.superclass(algorithmClass);
		MethodSpec.Builder methodBuilder = MethodSpec
			.methodBuilder("execute")
			.returns(void.class)
			.addModifiers(Modifier.PUBLIC)
			.addParameter(netlistClass,"netlist",Modifier.FINAL)
			.addParameter(targetDataClass,"targetData",Modifier.FINAL)
			.addParameter(algorithmProfileClass,"AProfile",Modifier.FINAL)
			.addParameter(runtimeEnvClass,"runtimeEnv",Modifier.FINAL)
			.addStatement("Utils.isNullRuntimeException(netlist, \"netlist\")")
			.addStatement("Utils.isNullRuntimeException(targetData, \"targetData\")")
			.addStatement("Utils.isNullRuntimeException(AProfile, \"AProfile\")")
			.addStatement("Utils.isNullRuntimeException(runtimeEnv, \"runtimeEnv\")")
			.addComment("init")
			.addStatement("this.setNetlist(netlist)")
			.addStatement("this.setTargetData(targetData)")
			.addStatement("this.setAlgorithmProfile(AProfile)")
			.addStatement("this.setRuntimeEnv(runtimeEnv)")
			.addComment("execute");
		Method m[] = algorithmClass.getDeclaredMethods();
		for (int i = 0; i < m.length; i++) {
			if (java.lang.reflect.Modifier.isAbstract(m[i].getModifiers())) {
				methodBuilder.addStatement("this." + m[i].getName() + "()");
			}
		}
		MethodSpec method = methodBuilder.build();
		builder.addMethod(method);

		// getter setter
		addGetterSetter(builder,"netlist","Netlist",netlistClass);
		addGetterSetter(builder,"targetData","TargetData",targetDataClass);
		addGetterSetter(builder,"AProfile","AlgorithmProfile",algorithmProfileClass);
		addGetterSetter(builder,"runtimeEnv","RuntimeEnv",runtimeEnvClass);

		builder.addField(netlistClass,"netlist",Modifier.PRIVATE);
		builder.addField(targetDataClass,"targetData",Modifier.PRIVATE);
		builder.addField(algorithmProfileClass,"AProfile",Modifier.PRIVATE);
		builder.addField(runtimeEnvClass,"runtimeEnv",Modifier.PRIVATE);

		TypeSpec ts = builder.build();
		JavaFile javaFile = JavaFile.builder(this.getPackageName() + "."
											 + this.getStageName() + ".algorithm",
											 ts).build();

		return javaFile;
	}

	private void addGetterSetter(TypeSpec.Builder builder, String var, String name, Class<?> c) {
		MethodSpec m = null;
		m = getGetter(var,name,c);
		builder.addMethod(m);
		m = getSetter(var,name,c);
		builder.addMethod(m);
	}

	private MethodSpec getGetter(String var, String name, Class<?> c) {
		MethodSpec rtn = MethodSpec
			.methodBuilder("get" + name)
			.addModifiers(Modifier.PROTECTED)
			.returns(c)
			.addStatement("return this." + var)
			.build();
		return rtn;
	}

	private MethodSpec getSetter(String var, String name, Class<?> c) {
		MethodSpec rtn = MethodSpec
			.methodBuilder("set" + name)
			.addModifiers(Modifier.PRIVATE)
			.returns(void.class)
			.addParameter(c,var,Modifier.FINAL)
			.addStatement("this." + var + " = " + var)
			.build();
		return rtn;
	}

}
