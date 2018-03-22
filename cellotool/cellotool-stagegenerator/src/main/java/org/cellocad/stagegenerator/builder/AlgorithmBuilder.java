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

import org.cellocad.common.Utils;
import org.cellocad.common.algorithm.Algorithm;
import org.cellocad.common.netlist.Netlist;
import org.cellocad.common.profile.AlgorithmProfile;
import org.cellocad.common.runtime.environment.RuntimeEnv;
import org.cellocad.common.target.data.TargetData;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Builder for the Algorithm class of a stage.
 *
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public class AlgorithmBuilder extends Builder{

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#AlgorithmBuilder(String,String,String)
	 */
	public AlgorithmBuilder(final String pkg, final String name, final String abbrev) {
		super(pkg,name,abbrev);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Builder#build()
	 */
	public JavaFile build() {
		// class def
		TypeSpec.Builder builder = TypeSpec.classBuilder(this.getAbbrev() + Algorithm.class.getSimpleName());
		builder.addModifiers(javax.lang.model.element.Modifier.PUBLIC,
							 javax.lang.model.element.Modifier.ABSTRACT);

		builder.superclass(Algorithm.class);

		String netlistVar = "netlist";
		String targetDataVar = "targetData";
		String AProfileVar = "AProfile";
		String runtimeEnvVar = "runtimeEnv";
		MethodSpec.Builder methodBuilder = MethodSpec
			.methodBuilder("execute")
			.returns(void.class)
			.addModifiers(Modifier.PUBLIC)
			.addParameter(Netlist.class,netlistVar,Modifier.FINAL)
			.addParameter(TargetData.class,targetDataVar,Modifier.FINAL)
			.addParameter(AlgorithmProfile.class,AProfileVar,Modifier.FINAL)
			.addParameter(RuntimeEnv.class,runtimeEnvVar,Modifier.FINAL)
			.addStatement("$T.isNullRuntimeException($L, $S)",Utils.class,netlistVar,netlistVar)
			.addStatement("$T.isNullRuntimeException($L, $S)",Utils.class,targetDataVar,targetDataVar)
			.addStatement("$T.isNullRuntimeException($L, $S)",Utils.class,AProfileVar,AProfileVar)
			.addStatement("$T.isNullRuntimeException($L, $S)",Utils.class,runtimeEnvVar,runtimeEnvVar)
			.addComment("init")
			.addStatement("this.setNetlist($L)",netlistVar)
			.addStatement("this.setTargetData($L)",targetDataVar)
			.addStatement("this.setAlgorithmProfile($L)",AProfileVar)
			.addStatement("this.setRuntimeEnv($L)",runtimeEnvVar)
			.addComment("execute");
		Method m[] = Algorithm.class.getDeclaredMethods();
		for (int i = 0; i < m.length; i++) {
			if (java.lang.reflect.Modifier.isAbstract(m[i].getModifiers())) {
				methodBuilder.addStatement("this.$L()",m[i].getName());
			}
		}
		MethodSpec method = methodBuilder.build();
		builder.addMethod(method);

		// getter setter
		addGetterSetter(builder,"netlist","Netlist",Netlist.class);
		addGetterSetter(builder,"targetData","TargetData",TargetData.class);
		addGetterSetter(builder,"AProfile","AlgorithmProfile",AlgorithmProfile.class);
		addGetterSetter(builder,"runtimeEnv","RuntimeEnv",RuntimeEnv.class);

		builder.addField(Netlist.class,netlistVar,Modifier.PRIVATE);
		builder.addField(TargetData.class,targetDataVar,Modifier.PRIVATE);
		builder.addField(AlgorithmProfile.class,AProfileVar,Modifier.PRIVATE);
		builder.addField(RuntimeEnv.class,runtimeEnvVar,Modifier.PRIVATE);

		TypeSpec ts = builder.build();
		JavaFile javaFile = JavaFile.builder(this.getPackageName() + "."
											 + this.getStageName() + ".algorithm",
											 ts).build();

		return javaFile;
	}

	/**
	 * Add a getter and a setter method to the TypeSpec Builder for a given field.
	 *
	 * @param builder the TypeSpec builder to which to add the method.
	 * @param var the field name.
	 * @param name the base name of the getter and setter functions, e.g. "Foo" in "getFoo()".
	 * @param c the class of the field.
	 */
	private void addGetterSetter(TypeSpec.Builder builder, String var, String name, Class<?> c) {
		MethodSpec m = null;
		m = getGetter(var,name,c);
		builder.addMethod(m);
		m = getSetter(var,name,c);
		builder.addMethod(m);
	}

	/**
	 * Generate a getter method for a given field.
	 *
	 * @param var the field name.
	 * @param name the base name of the getter functions, e.g. "Foo" in "getFoo()".
	 * @param c the class of the field.
	 * @return the generated MethodSpec.
	 */
	private MethodSpec getGetter(String var, String name, Class<?> c) {
		MethodSpec rtn = MethodSpec
			.methodBuilder("get" + name)
			.addModifiers(Modifier.PROTECTED)
			.returns(c)
			.addStatement("return this.$L",var)
			.build();
		return rtn;
	}

	/**
	 * Generate a setter method for a given field.
	 *
	 * @param var the field name.
	 * @param name the base name of the setter functions, e.g. "Foo" in "getFoo()".
	 * @param c the class of the field.
	 * @return the generated MethodSpec.
	 */
	private MethodSpec getSetter(String var, String name, Class<?> c) {
		MethodSpec rtn = MethodSpec
			.methodBuilder("set" + name)
			.addModifiers(Modifier.PRIVATE)
			.returns(void.class)
			.addParameter(c,var,Modifier.FINAL)
			.addStatement("this.$L = $L",var,var)
			.build();
		return rtn;
	}

}
