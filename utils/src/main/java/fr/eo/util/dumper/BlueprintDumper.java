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

import fr.eo.util.dumper.bean.Blueprint;
import fr.eo.util.dumper.bean.BlueprintActivity;
import fr.eo.util.dumper.bean.JsonTableDump;
import fr.eo.util.dumper.bean.Material;
import fr.eo.util.dumper.bean.Product;
import fr.eo.util.dumper.bean.Skill;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static fr.eo.util.dumper.GenericBeanToJSONDumper.dumpBeans;

/**
 * @author picon.software
 */
public class BlueprintDumper {

	public static void main(String[] args) {
		dump(".");
	}

	public static List<JsonTableDump> dump(String assetFolder) {

		Set<Blueprint> blueprints = BlueprintYamlParser.getBlueprints();
		Set<BlueprintActivity> activities = new LinkedHashSet<>();
		Set<Material> materials = new LinkedHashSet<>();
		Set<Product> products = new LinkedHashSet<>();
		Set<Skill> skills = new LinkedHashSet<>();

		List<JsonTableDump> dumps = new ArrayList<>();

		for (Blueprint blueprint : blueprints) {
			activities.addAll(blueprint.activities);

			for (BlueprintActivity activity : blueprint.activities) {
				if(activity.materials != null) {
					materials.addAll(activity.materials);
				}
				if(activity.products != null) {
					products.addAll(activity.products);
				}
				if(activity.skills != null) {
					skills.addAll(activity.skills);
				}
			}
		}

		dumps.add(dumpBeans(blueprints, assetFolder));
		dumps.add(dumpBeans(activities, assetFolder));
		dumps.add(dumpBeans(materials, assetFolder));
		dumps.add(dumpBeans(products, assetFolder));
		dumps.add(dumpBeans(skills, assetFolder));

		return dumps;
	}
}
