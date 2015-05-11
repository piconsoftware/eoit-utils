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
import fr.eo.util.dumper.bean.JsonTableWritable;
import fr.eo.util.dumper.property.RequestDefinitionBean;
import fr.eo.util.dumper.property.RequestDefinitionParser;
import org.apache.commons.codec.binary.Hex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;

import static fr.eo.util.dumper.util.DumperUtil.formatString;
import static fr.eo.util.dumper.util.DumperUtil.getConnection;
import static fr.eo.util.dumper.util.DumperUtil.getWriter;

/**
 * @author picon.software
 */
public class JSONDumper {

	private static String baseFolder = null;

	/**
	 * @param args main args
	 */
	public static void main(String[] args) {

		String appName = args[0];
		String jdbcConnectionType = args.length > 1 ? args[1] : "jtds";

		System.out.println("Starting dumper ...");

		try (Connection conn = getConnection(jdbcConnectionType)) {
			System.out.println("Getting database connection ...");

			List<RequestDefinitionBean> requests = RequestDefinitionParser.getRequests(appName);

			baseFolder = RequestDefinitionParser.getAppBaseDir(appName) + "/";

			System.out.println("Reading old table dumps...");
			Map<String, JsonTableDump> oldTables = JSONDeltaDumper.readOldTables(baseFolder);
			List<JsonTableDump> newTables = new ArrayList<>();

			for (RequestDefinitionBean request : requests) {
				try (Statement stmt = conn.createStatement()) {
					BufferedWriter bw = getWriter(request.name, baseFolder);

					if (!request.disabled) {
						System.out.println("Dumping " + request.name + "...");
						ResultSet rs = stmt.executeQuery(request.sql);
						JsonTableWritable dump = new JsonTableWritable();
						dump.name = request.table;
						while (rs.next()) {
							int pos = 0;
							for (String fieldName : request.fields) {
								Object obj = getFieldValue(request, pos, rs, fieldName);
								dump.addColumn(obj);
								pos++;
							}
							dump.commit();
						}
						bw.append(dump.toJson());
						newTables.add(dump);
						bw.flush();
						bw.close();
					} else {
						System.out.println("Skiping " + request.name + "...");
					}
				}

				System.out.println("done.");
			}

			newTables.addAll(BlueprintDumper.dump(baseFolder));
			newTables.addAll(TranslationsDumper.dump(baseFolder, jdbcConnectionType));

			System.out.println("Computing delta...");
			JSONDeltaDumper.computeDelta(oldTables, newTables, baseFolder);

		} catch (SQLException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	private static Object getFieldValue(RequestDefinitionBean request,
										int pos,
										ResultSet rs,
										String fieldName)
			throws SQLException {
		String str = String.valueOf(rs.getObject(fieldName)).trim();
		if (str.endsWith(".0")) {
			str = str.substring(0, str.length() - 2);
		}
		if (str.equals("null") || str.isEmpty()) {
			return null;
		}
		if (request.compressedStrPos.contains(pos)) {
			str = getCompressedString(str);
		} else if (request.numberPos.contains(pos)) {
			Number number = rs.getDouble(fieldName);
			if(number.longValue() == number.doubleValue()) {
				return number.longValue();
			}
			return number.doubleValue();
		} else if (request.fieldStrPos.contains(pos)) {
			str = formatString(str);
		}

		return str;
	}

	private static String getCompressedString(String value) {

		byte[] output = new byte[8096];

		try {
			byte[] input = value.getBytes("UTF-8");
			Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION, true);
			compresser.setInput(input);
			compresser.finish();
			int compressedDataLength = compresser.deflate(output);
			return "X'" + Hex.encodeHexString(Arrays.copyOf(output, compressedDataLength)) + "'";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
}
