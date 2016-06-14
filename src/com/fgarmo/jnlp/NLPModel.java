/* 
 * JNLP is an open-source useful graphical desktop application for Natural Language Processing.
 * More details on <https://github.com/garmo/JNLP/blob/master/README.md>
 * Copyright (C) 2016 Fran García
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.fgarmo.jnlp;

import java.text.DecimalFormat;

/**
*
* @author Fran García <garmodev@gmail.com>
*/
public class NLPModel {
	private String term;
	private Integer ocurrencies;
	private Double relativeFrecuency;
	
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public Integer getOcurrencies() {
		return ocurrencies;
	}
	public void setOcurrencies(Integer ocurrencies) {
		this.ocurrencies = ocurrencies;
	}
	public Double getRelativeFrecuency() {
		return relativeFrecuency;
	}
	public void setRelativeFrecuency(Double relativeFrecuency) {
		this.relativeFrecuency = relativeFrecuency;
	}
	
	public NLPModel(String term, Integer ocurrencies, Double relativeFrecuency) {
		super();
		this.term = term;
		this.ocurrencies = ocurrencies;
		this.relativeFrecuency = relativeFrecuency;
	}
	
	@Override
	public String toString() {
		DecimalFormat rfDecf = new DecimalFormat("#######.##################");
        String rfSt = rfDecf.format(relativeFrecuency);
        
		return "term=" + term + ", ocurrencies=" + ocurrencies + ", relativeFrecuency=" + rfSt;
	}
	
	
}
