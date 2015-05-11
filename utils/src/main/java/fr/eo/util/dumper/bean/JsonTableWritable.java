/*
 * Copyright (C) 2014 Picon software
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package fr.eo.util.dumper.bean;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author picon.software
 */
public class JsonTableWritable extends JsonTableDump {

	transient private List<Object> currentLine = new ArrayList<>();
	transient private int nullColumns = 0;

	public JsonTableWritable addColumn(Object column) {
		if(column != null) {
			currentLine.add(column);
		} else {
			nullColumns++;
		}

		return this;
	}

	public JsonTableWritable addColumns(List<Object> columns) {
		for (Object column : columns) {
			addColumn(column);
		}

		return this;
	}

	public JsonTableWritable commit() {
		insert.add(currentLine);
		columns = currentLine.size() + nullColumns;
		currentLine = new ArrayList<>();
		nullColumns = 0;

		return this;
	}

	public String toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"name\":\"").append(name)
				.append("\",\"columns\":").append(columns)
				.append(",\"insert\":[\n");

		List<String> jsonLines = new ArrayList<>();
		for (List<Object> line : insert) {
			jsonLines.add(toJson(convert(line)));
		}

		sb.append(Joiner.on(",\n").join(jsonLines));
		sb.append("\n]}");
		return sb.toString();
	}

	private String toJson(List<String> columns) {
		return "[" + Joiner.on(",").join(columns) + "]";
	}

	private List<String> convert(List<Object> columns) {
		List<String> serializedColumns = new ArrayList<>();

		for (Object column : columns) {
			serializedColumns.add(serialize(column));
		}


		return serializedColumns;
	}

	private String serialize(Object obj) {
		if(obj instanceof String) {
			return "\"" + obj + "\"";
		} else {
			return obj.toString();
		}
	}
}
