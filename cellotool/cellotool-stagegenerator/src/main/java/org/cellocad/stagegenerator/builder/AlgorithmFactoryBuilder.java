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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Builder for the AlgorithmFactory class in common.
 *
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public class AlgorithmFactoryBuilder extends Builder{

	public AlgorithmFactoryBuilder(final String pkg, final String name, final String abbrev) {
		super(pkg,name,abbrev);
		List<String> alg = new ArrayList<>();
		alg.add("Base");
		this.setAlgorithmNames(alg);
	}

	public AlgorithmFactoryBuilder(final String pkg, final String name, final String abbrev, final List<String> algorithms) {
		super(pkg,name,abbrev);
		initAlgorithms(algorithms);
	}

	private void initAlgorithms(final List<String> algorithms) {
		List<String> accept = new ArrayList<>();
		Pattern p = Pattern.compile("[\\p{L}_$][\\p{L}\\p{N}_$]*");
		for (String algorithm : algorithms) {
			Matcher m = p.matcher(algorithm);
			if (!m.matches()) {
				throw new IllegalArgumentException("Algorithm name " + algorithm + " is not a valid Java class name.");
			} else {
				accept.add(algorithm);
			}
		}
		this.setAlgorithmNames(accept);
	}

	public JavaFile build() {
		TypeSpec.Builder builder = TypeSpec.classBuilder(this.getAbbrev() + "AlgorithmFactory");
		builder.addModifiers(javax.lang.model.element.Modifier.PUBLIC);
		Class<?> c = null;
		try {
			c = Class.forName("org.cellocad.common.algorithm.AlgorithmFactory");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Method m[] = c.getDeclaredMethods();
		ClassName stageAlgorithm = ClassName.get(this.getPackageName() + "." + this.getStageName() + ".algorithm", this.getAbbrev() + "Algorithm");
		for (int i = 0; i < m.length; i++) {
			if (java.lang.reflect.Modifier.isAbstract(m[i].getModifiers())
				&& m[i].getName().equals("getAlgorithm")) {
				MethodSpec.Builder methodBuilder = MethodSpec
					.methodBuilder(m[i].getName())
					.addModifiers(Modifier.PROTECTED)
					.addAnnotation(Override.class)
					.addParameter(String.class,"name",Modifier.FINAL)
					.returns(stageAlgorithm)
					.addStatement("$T rtn = null",stageAlgorithm);
				for (String alg : this.getAlgorithmNames()) {
					ClassName algClass = ClassName.get(this.getPackageName() + "." + this.getStageName() + ".algorithm." + alg, alg);
					methodBuilder.beginControlFlow("if (name.equals(\"$L\"))",alg)
						.addStatement("rtn = new $T()",algClass)
						.endControlFlow();
				}
				methodBuilder.addStatement("return rtn");
				MethodSpec method = methodBuilder.build();
				builder.addMethod(method);
			}
		}
		TypeSpec factory = builder.build();
		JavaFile javaFile = JavaFile.builder(this.getPackageName() + "." + this.getStageName() + ".algorithm",factory).build();

		return javaFile;
	}

		private List<String> algorithmNames;

	/**
	 * @return the algorithmNames
	 */
	public final List<String> getAlgorithmNames() {
		return algorithmNames;
	}

	/**
	 * @param algorithmNames the algorithmNames to set
	 */
	private void setAlgorithmNames(final List<String> algorithmNames) {
		this.algorithmNames = algorithmNames;
	}

}
