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

import javax.swing.JProgressBar;

/**
*
* @author Fran García <garmodev@gmail.com>
*/
public class ProgressBar implements Runnable{

	private JProgressBar bar;
	private Integer count;
	
	public ProgressBar(JProgressBar b){
		bar = b;
		count = 0;
	}
	
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public void run() {
		bar.repaint();
	}

	public void inc(){
		count++;
		bar.setValue(count);
		bar.repaint();
	}
	
	public void inc(int i){
		bar.setValue(i);
		bar.repaint();
	}
}
