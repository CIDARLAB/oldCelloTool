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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cellocad.stagegenerator.builder.AlgorithmBuilder;
import org.cellocad.stagegenerator.builder.AlgorithmFactoryBuilder;
import org.cellocad.stagegenerator.builder.AlgorithmImplBuilder;
import org.cellocad.stagegenerator.builder.ArgDescriptionBuilder;
import org.cellocad.stagegenerator.builder.ArgStringBuilder;
import org.cellocad.stagegenerator.builder.Builder;
import org.cellocad.stagegenerator.builder.MainBuilder;
import org.cellocad.stagegenerator.builder.RuntimeEnvBuilder;
import org.cellocad.stagegenerator.builder.RuntimeObjectBuilder;
import org.cellocad.stagegenerator.runtime.environment.ArgString;
import org.cellocad.stagegenerator.runtime.environment.RuntimeEnv;

/**
 * @author: Timothy Jones
 *
 * @date: Mar 21, 2018
 *
 */
public class Main {

	public static void main(String args[]) {
		RuntimeEnv runEnv = new RuntimeEnv(args);
		runEnv.setName("Stage Generator");
		String pkg = runEnv.getOptionValue(ArgString.PKGNAME);
		String name = runEnv.getOptionValue(ArgString.STAGENAME);
		String abbrev = runEnv.getOptionValue(ArgString.STAGEABBREV);
		String outputDir = runEnv.getOptionValue(ArgString.OUTPUTDIR);
		File out = new File(outputDir);

		List<String> algorithms = new ArrayList<>();
		// for (int i = 3; i < args.length; i++) {
		// 	algorithms.add(args[i]);
		// }
		// if (algorithms.size() == 0) {
			// algorithms.add("Base");
		// }
		algorithms.add("Base");

		AlgorithmBuilder alg = new AlgorithmBuilder(pkg,name,abbrev);
		write(alg,out);
		AlgorithmFactoryBuilder fac = new AlgorithmFactoryBuilder(pkg,name,abbrev,algorithms);
		write(fac,out);

		for (String a : algorithms) {
			AlgorithmImplBuilder impl = new AlgorithmImplBuilder(pkg,name,abbrev,a);
			write(impl,out);
		}

		RuntimeObjectBuilder ro = new RuntimeObjectBuilder(pkg,name,abbrev);
		write(ro,out);
		MainBuilder main = new MainBuilder(pkg,name,abbrev);
		write(main,out);
		ArgDescriptionBuilder ad = new ArgDescriptionBuilder(pkg,name,abbrev);
		write(ad,out);
		ArgStringBuilder as = new ArgStringBuilder(pkg,name,abbrev);
		write(as,out);
		RuntimeEnvBuilder re = new RuntimeEnvBuilder(pkg,name,abbrev);
		write(re,out);
	}

	private static void write(Builder b, File out) {
		try {
			b.build().writeTo(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
