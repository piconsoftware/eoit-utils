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

import fr.eo.util.dumper.bean.JsonTableDump;
import fr.eo.util.dumper.bean.TranslationDump;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static fr.eo.util.dumper.GenericBeanToJSONDumper.dumpBeans;
import static fr.eo.util.dumper.util.DumperUtil.formatString;
import static fr.eo.util.dumper.util.DumperUtil.getConnection;

/**
 * @author picon.software
 */
public class TranslationsDumper {

	private static final String SQL = "SELECT c.categoryID AS keyID, t.tcID AS tcID, languageID, t.text AS text FROM trnTranslations t JOIN invCategories c ON t.keyID = c.categoryID AND c.published = 1 AND t.tcID = 6 WHERE languageID IN ('EN-US', 'DE', 'RU', 'FR') UNION SELECT g.groupID, t.tcID, languageID, t.text FROM trnTranslations t JOIN invGroups g ON t.keyID = g.groupID AND g.published = 1 AND t.tcID = 7 WHERE languageID IN ('EN-US', 'DE', 'RU', 'FR') UNION SELECT i.typeID AS keyID, t.tcID AS tcID, languageID, t.text AS text FROM trnTranslations t JOIN invTypes i ON t.keyID = i.typeID AND i.published = 1 AND t.tcID = 8 WHERE languageID IN ('EN-US', 'DE', 'RU', 'FR')";

	/**
	 * @param args main args
	 */
	public static void main(String[] args) {
		dump(".", "sqlite");
	}

	public static List<JsonTableDump> dump(String assetFolder, String jdbcConnectionType) {

		List<JsonTableDump> dumps = new ArrayList<>();

		try (Connection conn = getConnection(jdbcConnectionType)) {
			System.out.println("Getting database connection ...");

			TranslationDump dump = new TranslationDump();

			try (Statement stmt = conn.createStatement()) {
				ResultSet rs = stmt.executeQuery(SQL);

				while (rs.next()) {
					int keyId = rs.getInt("keyID");
					int tcId = rs.getInt("tcID");
					String languageId = rs.getString("languageID");
					String text = formatString(rs.getString("text"));
					dump.add(keyId, tcId, languageId, text);
				}
			}

			dumps.add(dumpBeans(dump.getTranslationKey(), assetFolder));
			dumps.add(dumpBeans(dump.getTranslations(), assetFolder));
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return dumps;
	}
}
