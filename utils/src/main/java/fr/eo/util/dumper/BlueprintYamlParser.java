package fr.eo.util.dumper;

import fr.eo.util.dumper.bean.Blueprint;
import fr.eo.util.dumper.bean.BlueprintActivity;
import fr.eo.util.dumper.bean.Material;
import fr.eo.util.dumper.bean.Product;
import fr.eo.util.dumper.bean.Skill;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * @author picon.software
 */
public final class BlueprintYamlParser {

	private static final Map<String, Integer> ACTIVITY_NAME_MAP = new HashMap<>();

	static {
		ACTIVITY_NAME_MAP.put("manufacturing", 1);
		ACTIVITY_NAME_MAP.put("research_time", 3);
		ACTIVITY_NAME_MAP.put("research_material", 4);
		ACTIVITY_NAME_MAP.put("copying", 5);
		ACTIVITY_NAME_MAP.put("invention", 8);
	}

	private BlueprintYamlParser() {
	}

	public static Set<Blueprint> getBlueprints() {
		Yaml yaml = new Yaml();

		System.out.println("Opening YAML file ...");
		InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("blueprints.yaml");

		System.out.println("Parsing YAML file ...");
		@SuppressWarnings("unchecked") Set<Blueprint> blueprints =
				toBlueprints((Map) yaml.load(systemResourceAsStream));

		return blueprints;
	}

	private static Set<Blueprint> toBlueprints(Map<Integer, Map> parsedBlueprints) {

		System.out.println("Converting parsed data ...");
		Set<Blueprint> list = new LinkedHashSet<>();

		for (Map.Entry<Integer, Map> entry : parsedBlueprints.entrySet()) {
			list.add(toBlueprint(entry));
		}

		return list;
	}

	private static Blueprint toBlueprint(Map.Entry<Integer, Map> parsedBlueprintEntry) {
		Blueprint blueprint = new Blueprint();

		Map parsedBlueprint = parsedBlueprintEntry.getValue();

		blueprint.id = parsedBlueprintEntry.getKey();
		blueprint.maxProductionLimit =
				(int) parsedBlueprint.get("maxProductionLimit");

		@SuppressWarnings("unchecked") Map<String, Map> activities =
				(Map<String, Map>) parsedBlueprint.get("activities");

		for (Map.Entry<String, Map> entry : activities.entrySet()) {
			blueprint.addActivity(toActivity(entry, blueprint.id));
		}

		return blueprint;
	}

	private static BlueprintActivity toActivity(Map.Entry<String, Map> parsedActivity, int blueprintId) {
		BlueprintActivity activity = new BlueprintActivity();

		activity.blueprintId = blueprintId;
		activity.activityId = activityTranscoder(parsedActivity.getKey());
		activity.time = (int) parsedActivity.getValue().get("time");

		@SuppressWarnings("unchecked") List<Map<String, Number>> materials =
				(List<Map<String, Number>>) parsedActivity.getValue().get("materials");
		@SuppressWarnings("unchecked") List<Map<String, Number>> products =
				(List<Map<String, Number>>) parsedActivity.getValue().get("products");
		@SuppressWarnings("unchecked") List<Map<String, Number>> skills =
				(List<Map<String, Number>>) parsedActivity.getValue().get("skills");

		if (materials != null) {
			for (Map<String, Number> material : materials) {
				activity.addMaterial(toMaterial(material));
			}
		}

		if (products != null) {
			for (Map<String, Number> product : products) {
				activity.addProduct(toProduct(product));
			}
		}

		if (skills != null) {
			for (Map<String, Number> skill : skills) {
				activity.addSkill(toSkill(skill));
			}
		}

		return activity;
	}

	private static int activityTranscoder(String activityName) {
		return ACTIVITY_NAME_MAP.get(activityName);
	}

	public static Material toMaterial(Map<String, Number> parsedMaterial) {
		Material material = new Material();

		material.itemId = parsedMaterial.get("typeID").intValue();
		material.quantity = parsedMaterial.get("quantity").longValue();

		return material;
	}

	public static Product toProduct(Map<String, Number> parsedProduct) {
		Product product = new Product();

		product.itemId = parsedProduct.get("typeID").intValue();
		product.quantity = parsedProduct.get("quantity").longValue();
		product.probability = parsedProduct.containsKey("probability") ?
				parsedProduct.get("probability").floatValue() : 1;

		return product;
	}

	public static Skill toSkill(Map<String, Number> parsedSkill) {
		Skill skill = new Skill();

		skill.itemId = parsedSkill.get("typeID").intValue();
		skill.level = parsedSkill.get("level").intValue();

		return skill;
	}
}
