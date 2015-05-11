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

package fr.eo.util;

import fr.eo.util.dumper.bean.JsonDeltaTableDump;
import fr.eo.util.dumper.bean.JsonTableReadable;
import org.junit.Test;

import java.io.IOException;

import static fr.eo.util.dumper.JSONDeltaDumper.computeDelta;
import static fr.eo.util.dumper.util.DumperUtil.loadJsonTableDump;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author picon.software
 */
public class TestJSONDeltaDumper {

	@Test
	public void testComputeDelta() throws IOException {
		JsonTableReadable oldTable = loadJsonTableDump(ClassLoader.getSystemResourceAsStream("dump_group_old.json"));
		JsonTableReadable newTable = loadJsonTableDump(ClassLoader.getSystemResourceAsStream("dump_group.json"));

		JsonDeltaTableDump jsonDeltaTableDump = computeDelta(oldTable, newTable);

		assertThat(jsonDeltaTableDump).isNotNull();
		assertThat(jsonDeltaTableDump.delete).isNotNull().isNotEmpty().contains(1292L);
	}

	@Test
	public void testComputeDelta2() throws IOException {
		JsonTableReadable oldTable = loadJsonTableDump(ClassLoader.getSystemResourceAsStream("dump_translation_old.json"));
		JsonTableReadable newTable = loadJsonTableDump(ClassLoader.getSystemResourceAsStream("dump_translation.json"));

		JsonDeltaTableDump jsonDeltaTableDump = computeDelta(oldTable, newTable);

		assertThat(jsonDeltaTableDump).isNotNull();
	}

}
