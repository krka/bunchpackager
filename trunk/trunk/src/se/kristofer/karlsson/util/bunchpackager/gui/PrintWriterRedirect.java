/*
    Copyright (c) 2005, 2008 Kristofer Karlsson

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package se.kristofer.karlsson.util.bunchpackager.gui;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.swing.JTextArea;


public class PrintWriterRedirect extends PrintWriter {

    private JTextArea area; 
    private CountDown c;
	private StringWriter sw;
	
	public static PrintWriterRedirect create(JTextArea directTo, CountDown c) {
		StringWriter sw = new StringWriter();
		PrintWriterRedirect pwr = new PrintWriterRedirect(sw, directTo, c);
		pwr.sw = sw;
		return pwr;
	}
	
    private PrintWriterRedirect(StringWriter sw, JTextArea directTo, CountDown c) {
        super(sw);
        area = directTo;
        this.c = c;
    }

	private void pre() {
		StringBuffer buffer = sw.getBuffer();
		buffer.delete(0, buffer.length());
	}
	
    private void post() {
    	String s = sw.getBuffer().toString();
        if (s.startsWith("Processing bunch ")) {
            c.start();
        }
		area.append(s);
	}

    @Override
	public PrintWriter append(char arg0) {
    	pre();
		super.append(arg0);
    	post();
    	return this;
	}

	@Override
	public PrintWriter append(CharSequence arg0, int arg1, int arg2) {
    	pre();
		super.append(arg0, arg1, arg2);
    	post();
    	return this;
	}

	@Override
	public PrintWriter append(CharSequence arg0) {
    	pre();
		super.append(arg0);
    	post();
    	return this;
	}
	@Override
	public PrintWriter format(Locale arg0, String arg1, Object... arg2) {
    	pre();
		super.format(arg0, arg1, arg2);
    	post();
    	return this;

	}
	
	/*
	
	@Override
	public PrintWriter format(String arg0, Object... arg1) {
    	pre();
		super.format(arg0, arg1);
    	post();
    	return this;
	}
*/
	
	/*
	@Override
	public void print(boolean arg0) {
    	pre();
		super.print(arg0);
    	post();
	}

	@Override
	public void print(char arg0) {
    	pre();
		super.print(arg0);
    	post();
	}

	@Override
	public void print(char[] arg0) {
    	pre();
		super.print(arg0);
    	post();
	}

	@Override
	public void print(double arg0) {
    	pre();
		super.print(arg0);
    	post();
	}

	@Override
	public void print(float arg0) {
    	pre();
		super.print(arg0);
    	post();
	}

	@Override
	public void print(int arg0) {
    	pre();
		super.print(arg0);
    	post();
	}

	@Override
	public void print(long arg0) {
    	pre();
		super.print(arg0);
    	post();
	}

	@Override
	public void print(Object arg0) {
    	pre();
		super.print(arg0);
    	post();
	}
*/
	@Override
	public void print(String arg0) {
    	pre();
		super.print(arg0);
    	post();
	}
/*
    @Override
	public PrintWriter printf(String fmt, Object... params) {
    	pre();
    	super.printf(fmt, params);
    	post();
    	return this;
    }
    	
	
	@Override
	public PrintWriter printf(Locale arg0, String arg1, Object... arg2) {
    	pre();
		super.printf(arg0, arg1, arg2);
    	post();
    	return this;
	}
*/
	
	@Override
	public void println() {
    	pre();
		super.println();
    	post();
	}
	
	/*
	@Override
	public void println(boolean arg0) {
    	pre();
		super.println(arg0);
    	post();
	}

	@Override
	public void println(char arg0) {
    	pre();
		super.println(arg0);
    	post();
	}

	@Override
	public void println(char[] arg0) {
    	pre();
		super.println(arg0);
    	post();
	}

	@Override
	public void println(double arg0) {
    	pre();
		super.println(arg0);
    	post();
	}

	@Override
	public void println(float arg0) {
    	pre();
		super.println(arg0);
    	post();
	}

	@Override
	public void println(int arg0) {
    	pre();
		super.println(arg0);
    	post();
	}

	@Override
	public void println(long arg0) {
    	pre();
		super.println(arg0);
    	post();
	}

	@Override
	public void println(Object arg0) {
    	pre();
		super.println(arg0);
    	post();
	}

	@Override
	public void println(String arg0) {
    	pre();
		super.println(arg0);
    	post();
	}
	/*
	@Override
	public void write(char[] arg0, int arg1, int arg2) {
    	pre();
		super.write(arg0, arg1, arg2);
    	post();
	}

	@Override
	public void write(char[] arg0) {
    	pre();
		super.write(arg0);
    	post();
	}

	@Override
	public void write(int arg0) {
    	pre();
    	super.write(arg0);
    	post();
	}

	@Override
	public void write(String arg0, int arg1, int arg2) {
    	pre();
    	super.write(arg0, arg1, arg2);
    	post();
	}

	@Override
	public void write(String arg0) {
    	pre();
    	super.write(arg0);
    	post();
	}
*/
}
