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

package fr.eo.util.dumper.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.eo.util.dumper.bean.JsonTableReadable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author picon.software
 */
public final class DumperUtil {

	private DumperUtil() {
	}

	public static String formatString(String value) {
		value = value.replaceAll("'", "''");
		value = value.replaceAll("‘", "''");
		value = value.replaceAll("’", "''");
		value = value.replaceAll("\"", "''");

		return value;
	}

	public static BufferedWriter getWriter(String dumpName, String mAssetFolder) throws IOException {
		String path, fileName;

		fileName = "dump_" + dumpName.toLowerCase().replaceAll(" ", "_") + ".json";

		path = mAssetFolder + "/" + fileName;
		File dumpFile = new File(path);

		//noinspection ResultOfMethodCallIgnored
		dumpFile.createNewFile();

		System.out.println("Getting writer for file : '" + dumpFile.getAbsolutePath() + "'");

		return new BufferedWriter(new FileWriter(dumpFile));
	}

	public static Connection getConnection(String connectionType) throws SQLException, ClassNotFoundException {
		switch (connectionType) {
			case "sqlite" :
				return getSqliteConnection();
			case "mysql":
				return getMySQLConnection();
			case "jtds" :
			default:
				return getJtdsConnection();
		}
	}

	@SuppressWarnings("unused")
	private static Connection getSqliteConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");

		return DriverManager.getConnection("jdbc:sqlite:E:/git-views/sqlite-latest.sqlite", "", "");
	}

	@SuppressWarnings("unused")
	private static Connection getJtdsConnection() throws SQLException, ClassNotFoundException {
		Class.forName("net.sourceforge.jtds.jdbc.Driver");

		return DriverManager.getConnection("jdbc:jtds:sqlserver://localhost:1433/ebs_DATADUMP", "eve", "eve");
	}

	@SuppressWarnings("unused")
	private static Connection getMySQLConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");

		return DriverManager.getConnection("jdbc:mysql://localhost:3306/eve_dump", "root", "");
	}

	public static JsonTableReadable loadJsonTableDump(InputStream is) throws IOException {
		Gson gson = new GsonBuilder().create();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			return gson.fromJson(br, JsonTableReadable.class);
		}
	}
}
