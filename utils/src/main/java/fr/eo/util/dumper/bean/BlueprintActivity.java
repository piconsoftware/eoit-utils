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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author picon.software
 */
public class BlueprintActivity implements DumpableBean, Comparable<BlueprintActivity> {

	public int blueprintId;
	public int activityId;
	public int time;
	public List<Material> materials;
	public List<Product> products;
	public List<Skill> skills;

	@Override
	public int compareTo(BlueprintActivity o) {
		return Integer.valueOf(activityId).compareTo(o.activityId);
	}

	public void addMaterial(Material material) {
		if(materials == null) {
			materials = new ArrayList<>();
		}

		materials.add(material);

		material.blueprintId = blueprintId;
		material.activityId = activityId;
	}

	public void addProduct(Product product) {
		if(products == null) {
			products = new ArrayList<>();
		}

		products.add(product);

		product.blueprintId = blueprintId;
		product.activityId = activityId;
	}

	public void addSkill(Skill skill) {
		if(skills == null) {
			skills = new ArrayList<>();
		}

		skills.add(skill);

		skill.blueprintId = blueprintId;
		skill.activityId = activityId;
	}


	@Override
	public String getDumpName() {
		return "blueprint_activity";
	}

	@Override
	public String dump() {
		return "INSERT INTO blueprint_activity VALUES (" +
				blueprintId + "," + activityId + "," + time + ");";
	}

	@Override
	public List<Object> columns() {
		return Arrays.asList((Object) blueprintId, activityId, time);
	}

}
