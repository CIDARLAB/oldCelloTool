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
public class AlgorithmImplBuilder extends Builder{

	public AlgorithmImplBuilder(final String pkg, final String name, final String abbrev, final String algorithm) {
		super(pkg,name,abbrev);
		this.setAlgorithm(algorithm);
	}

	public JavaFile build() {
		TypeSpec.Builder builder = TypeSpec.classBuilder(this.getAlgorithm());
		builder.addModifiers(javax.lang.model.element.Modifier.PUBLIC);

		// execution methods
		Class<?> c = null;
		try {
			c = Class.forName("org.cellocad.common.algorithm.Algorithm");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Method m[] = c.getDeclaredMethods();
		for (int i = 0; i < m.length; i++) {
			if (java.lang.reflect.Modifier.isAbstract(m[i].getModifiers())) {
				MethodSpec method = MethodSpec
					.methodBuilder(m[i].getName())
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(m[i].getReturnType())
					.build();
				builder.addMethod(method);
			}
		}
		TypeSpec algorithm = builder.build();
		JavaFile javaFile = JavaFile.builder(this.getPackageName() + "."
											 + this.getStageName() + ".algorithm."
											 + this.getAlgorithm(),
											 algorithm).build();

		return javaFile;
	}

	private String algorithm;

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

}
