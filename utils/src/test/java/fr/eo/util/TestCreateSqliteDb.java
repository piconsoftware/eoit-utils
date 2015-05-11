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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.eo.util.dumper.bean.JsonTableWritable;
import fr.eo.util.dumper.property.RequestDefinitionParser;
import fr.eo.util.helper.JsonDumpReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author picon.software
 */
public class TestCreateSqliteDb {

	@Test
	public void testCreate() throws IOException, SQLException, ClassNotFoundException {
		String assetDir = "../" + RequestDefinitionParser.getAppBaseDir("eoit-json");

		JsonDumpReader dumpReader = new JsonDumpReader(assetDir);

		int cpt = 0, totalCpt = 0;
		try (Connection conn = getSqliteConnection()) {
			conn.setAutoCommit(false);
			try (Statement stmt = conn.createStatement()) {
				while (dumpReader.hasNextLine()) {
					stmt.addBatch(dumpReader.nextLine());
					totalCpt++;
					if (cpt > 5000) {
						System.out.println("Executing batch ...");
						stmt.executeBatch();
						conn.commit();
						cpt = 0;
					} else {
						cpt++;
					}
				}

				System.out.println("Executing batch ...");
				stmt.executeBatch();
				conn.commit();

			}
		} catch (BatchUpdateException e) {
			e.printStackTrace();
		}

		System.out.println(totalCpt + " lines inserted.");

	}

	private static Connection getSqliteConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");

		return DriverManager.getConnection("jdbc:sqlite:testCreate.sqlite", "", "");
	}

	@Test
	public void testTableDump() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		final Gson gson = gsonBuilder.create();

		JsonTableWritable given = new JsonTableWritable();
		given.name = "groups";
		given.addColumn(21).addColumn(15).addColumn("sdfsdf").commit();
		given.addColumn(21).addColumn(16).addColumn("sdfsdf").commit();
		given.addColumn(0.25f).addColumn(17).addColumn("sdfsdf").commit();
		given.addColumn(21065160.489).addColumn(18L).addColumn("sdfsdf").commit();

		final String json = given.toJson();
		System.out.println(json);
		JsonTableWritable result = gson.fromJson(json, JsonTableWritable.class);

		Assert.assertNotNull(result);
		Assert.assertTrue(given.name.equals(result.name));
		Assert.assertTrue(result.insert.size() == 4);
	}
}
