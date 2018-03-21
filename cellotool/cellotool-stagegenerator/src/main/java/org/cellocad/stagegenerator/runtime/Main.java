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
package org.cellocad.stagegenerator.runtime;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import org.cellocad.stagegenerator.builder.AlgorithmBuilder;
import org.cellocad.stagegenerator.builder.ArgDescriptionBuilder;
import org.cellocad.stagegenerator.builder.AlgorithmFactoryBuilder;
import org.cellocad.stagegenerator.builder.ArgStringBuilder;
import org.cellocad.stagegenerator.builder.AlgorithmImplBuilder;
import org.cellocad.stagegenerator.builder.RuntimeObjectBuilder;
import org.cellocad.stagegenerator.builder.RuntimeEnvBuilder;
import org.cellocad.stagegenerator.builder.MainBuilder;

/**
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public class Main {

	public static void main(String args[]) {
		if (args.length < 3) {
			displayHelp();
			throw new IllegalArgumentException("Must specify a package name, stage name, and stage abbreviation.");
		}

		List<String> algorithms = new ArrayList<>();
		for (int i = 3; i < args.length; i++) {
			algorithms.add(args[i]);
		}
		if (algorithms.size() == 0) {
			algorithms.add("Base");
		}

		AlgorithmBuilder alg = new AlgorithmBuilder(args[0],args[1],args[2]);
		try {
			alg.build().writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		AlgorithmFactoryBuilder fac = new AlgorithmFactoryBuilder(args[0],args[1],args[2],algorithms);
		try {
			fac.build().writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String a : algorithms) {
			AlgorithmImplBuilder impl = new AlgorithmImplBuilder(args[0],args[1],args[2],a);
			try {
				impl.build().writeTo(System.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		RuntimeObjectBuilder ro = new RuntimeObjectBuilder(args[0],args[1],args[2]);
		try {
			ro.build().writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		MainBuilder main = new MainBuilder(args[0],args[1],args[2]);
		try {
			main.build().writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArgDescriptionBuilder ad = new ArgDescriptionBuilder(args[0],args[1],args[2]);
		try {
			ad.build().writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArgStringBuilder as = new ArgStringBuilder(args[0],args[1],args[2]);
		try {
			as.build().writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		RuntimeEnvBuilder re = new RuntimeEnvBuilder(args[0],args[1],args[2]);
		try {
			re.build().writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void displayHelp() {
		System.out.println("Arguments: <package name> <stage name> <abbrev> [method ...]");
	}

}
