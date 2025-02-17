/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package org.cellocad.common.target;

import org.cellocad.common.CObjectCollection;
import org.cellocad.common.profile.ProfileObject;
import org.cellocad.common.stage.Stage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 20, 2017
 *
 */
public class TargetConfiguration extends ProfileObject{

	private void init() {
		this.stages = new CObjectCollection<Stage>();
	}

	public TargetConfiguration(final JsonObject JObj, final String TargetConfigurationDir){
		super(JObj);
		init();
		parse(JObj, TargetConfigurationDir);
	}

	public TargetConfiguration(final JsonObject JObj){
		this(JObj, "");
	}

	/*
	 * Parse
	 */
	private void parseStages(final JsonObject JObj, final String TargetConfigurationDir){
		JsonArray jsonArr;
		// parse PartitionProfile
		jsonArr = JObj.getAsJsonArray("stages");
		if (jsonArr == null) {
			throw new RuntimeException("'stages' missing in TargetInfo!");
		}
		for (int i = 0; i < jsonArr.size(); i++)
		{
			JsonObject jsonObj = jsonArr.get(i).getAsJsonObject();
			Stage S = new Stage(jsonObj, TargetConfigurationDir);
			this.addStage(S);
		}
	}

	private void parse(final JsonObject JObj, final String TargetConfigurationDir){
		this.parseStages(JObj, TargetConfigurationDir);
	}

	private void addStage(final Stage stage){
		if (stage != null){
			this.getStages().add(stage);
		}
	}

	public Stage getStageByName(final String name){
		Stage rtn = this.getStages().findCObjectByName(name);
		return rtn;
	}

	public Stage getStageAtIdx(int index){
		Stage rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumStage())
				){
			rtn = this.getStages().get(index);
		}
		return rtn;
	}

	public int getNumStage(){
		int rtn = this.getStages().size();
		return rtn;
	}

	private CObjectCollection<Stage> getStages(){
		return this.stages;
	}

	CObjectCollection<Stage> stages;
}
