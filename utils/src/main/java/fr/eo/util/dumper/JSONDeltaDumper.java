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

package fr.eo.util.dumper;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.eo.util.dumper.bean.JsonDeltaTableDump;
import fr.eo.util.dumper.bean.JsonTableDump;
import fr.eo.util.dumper.bean.JsonTableReadable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.eo.util.dumper.util.DumperUtil.loadJsonTableDump;

/**
 * @author picon.software
 */
public class JSONDeltaDumper {

	public static final String DELTA_FILE_NAME = "delta.json";

	public static void computeDelta(Map<String, JsonTableDump> oldTables,
									List<JsonTableDump> newTables, String outputFolder) throws IOException {
		FileWriter fw = new FileWriter(outputFolder + "/" + DELTA_FILE_NAME);

		List<JsonDeltaTableDump> deltas = new ArrayList<>();

		for (JsonTableDump newTable : newTables) {
			if(oldTables.containsKey(newTable.name)) {
				JsonDeltaTableDump delta = computeDelta(oldTables.get(newTable.name), newTable);
				if(!delta.isEmpty()) {
					deltas.add(delta);
				}
			} else {
				System.err.println(newTable.name + " n'existait pas avant !");
				return;
			}
		}

		Gson gson = new GsonBuilder().create();
		gson.toJson(deltas, fw);
	}

	public static Map<String, JsonTableDump> readOldTables(String baseFolder) throws IOException {
		Map<String, JsonTableDump> tables = new HashMap<>();

		File folder = new File(baseFolder);
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory() && fileEntry.getName().startsWith("dump_") &&
					fileEntry.getName().endsWith(".json")) {
				JsonTableReadable tableDump = loadJsonTableDump(new FileInputStream(fileEntry));
				tables.put(tableDump.name, tableDump);
			}
		}

		return tables;
	}

	public static JsonDeltaTableDump computeDelta(JsonTableDump oldTable, JsonTableDump newTable) {
		JsonDeltaTableDump delta = new JsonDeltaTableDump();

		Map<Object, List<Object>> mapLinesOld = getMapLines(oldTable.insert, oldTable.pkColumn);
		Map<Object, List<Object>> mapLinesNew = getMapLines(newTable.insert, newTable.pkColumn);

		delta.name = oldTable.name;
		delta.pkName = oldTable.pkName;
		delta.columns = oldTable.columns;
		delta.insert.addAll(findNewLines(mapLinesOld, mapLinesNew));
		delta.update.addAll(findUpdatedLines(mapLinesOld, mapLinesNew));
		delta.delete = findDeletedLines(mapLinesOld, mapLinesNew);

		return delta;
	}

	private static Map<Object, List<Object>> getMapLines(List<List<Object>> lines, int pkColumn) {
		Map<Object, List<Object>> mapLines = new HashMap<>();

		for (List<Object> line : lines) {
			mapLines.put(truncateNumber(line.get(pkColumn)), truncateNumbers(line));
		}

		return mapLines;
	}

	private static List<List<Object>> findNewLines(Map<Object, List<Object>> mapLinesOld,
												   Map<Object, List<Object>> mapLinesNew) {
		List<List<Object>> lines = new ArrayList<>();

		for (Map.Entry<Object, List<Object>> lineEntry : mapLinesNew.entrySet()) {
			if (!mapLinesOld.containsKey(lineEntry.getKey())) {
				lines.add(lineEntry.getValue());
			}
		}

		return lines;
	}

	private static List<Object> findDeletedLines(Map<Object, List<Object>> mapLinesOld,
												 Map<Object, List<Object>> mapLinesNew) {
		List<Object> pks = new ArrayList<>();

		for (Map.Entry<Object, List<Object>> lineEntry : mapLinesOld.entrySet()) {
			if (!mapLinesNew.containsKey(lineEntry.getKey())) {
				pks.add(lineEntry.getKey());
			}
		}

		return pks;
	}

	private static List<List<Object>> findUpdatedLines(Map<Object, List<Object>> mapLinesOld,
													   Map<Object, List<Object>> mapLinesNew) {
		List<List<Object>> lines = new ArrayList<>();

		for (Map.Entry<Object, List<Object>> lineEntry : mapLinesOld.entrySet()) {
			if (mapLinesNew.containsKey(lineEntry.getKey()) &&
					hasChange(mapLinesOld.get(lineEntry.getKey()), mapLinesNew.get(lineEntry.getKey()))) {
				lines.add(mapLinesNew.get(lineEntry.getKey()));
			}
		}

		return lines;
	}

	private static boolean hasChange(List<Object> oldLine, List<Object> newLine) {
		if(oldLine.size() != newLine.size()) {
			return false;
		}

		for (int i = 0; i < oldLine.size(); i++) {
			if (!oldLine.get(i).equals(newLine.get(i))) {
				return true;
			}
		}

		return false;
	}

	private static List<Object> truncateNumbers(List<Object> values) {
		return FluentIterable.from(values).transform(new Function<Object, Object>() {
			@Override
			public Object apply(Object value) {
				return truncateNumber(value);
			}
		}).toList();
	}

	private static Object truncateNumber(Object value) {
		if (value instanceof Number) {
			Number number = (Number) value;
			if (number.longValue() == number.doubleValue()) {
				return number.longValue();
			}
		}

		return value;
	}
}
