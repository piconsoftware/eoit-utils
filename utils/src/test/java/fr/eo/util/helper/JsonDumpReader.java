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

package fr.eo.util.helper;

import fr.eo.util.dumper.bean.JsonTableReadable;
import fr.eo.util.dumper.bean.Readable;
import fr.eo.util.dumper.bean.SqlRequestsDump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static fr.eo.util.dumper.util.DumperUtil.loadJsonTableDump;


/**
 * @author picon.software
 */
public class JsonDumpReader {

	private static final String[] JSON_DUMP_TABLES = {
			"dump_categories",
			"dump_groups",
			"dump_items",
			"dump_regions",
			"dump_solar_systems",
			"dump_stations",
			"dump_planet_schematics",
			"dump_planet_schematics_type_map",
			"dump_reaction_materials",
			"dump_refine_materials",
			"dump_blueprint",
			"dump_blueprint_activity",
			"dump_blueprint_material",
			"dump_blueprint_product",
			"dump_blueprint_skill",
			"dump_translation",
			"dump_translation_key"
	};

    private final String baseDir;

    private Iterator<Readable> dumpableIterator;
	private Readable currentReadable;

	@SuppressWarnings("unchecked")
	public JsonDumpReader(String baseDir) throws IOException {
		super();
        this.baseDir = baseDir;

        List<Readable> readables = new ArrayList<>();
        readables.add(loadSqlRequestDump("dump_structure_bdd"));
        readables.add(loadSqlRequestDump("dump_structure_bdd_views"));

		for (String jsonDumpTable : JSON_DUMP_TABLES) {
			readables.add(loadJsonTableDump(getInputStream(jsonDumpTable)));
		}

		readables.add(loadSqlRequestDump("dump_structure_bdd_idx"));
		readables.add(loadSqlRequestDump("dump_workaround"));
		dumpableIterator = readables.iterator();
		currentReadable = dumpableIterator.next();
	}

	public boolean hasNextLine() throws IOException {
		if (!currentReadable.hasNext() && !dumpableIterator.hasNext()) {
			return false;
		}
		if (!currentReadable.hasNext() && dumpableIterator.hasNext()) {
			currentReadable = dumpableIterator.next();
			if(currentReadable instanceof JsonTableReadable) {
                System.out.println("Starting to read dump : " + ((JsonTableReadable) currentReadable).name);
			}
		}

		return currentReadable.hasNext();
	}

	public String nextLine() {
		return currentReadable.nextLine();
	}

	private SqlRequestsDump loadSqlRequestDump(String rawResourceId) throws IOException {
		InputStream is = new FileInputStream(new File(baseDir + "/" + rawResourceId + ".sql"));
        SqlRequestsDump dump = new SqlRequestsDump();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() != 0 && !line.startsWith("--")) {
                    dump.addLine(line);
                }
            }
            return dump;
        }
	}

	private InputStream getInputStream(String rawResourceId) throws FileNotFoundException {
		return new FileInputStream(new File(baseDir + "/" + rawResourceId + ".json"));
	}
}
