package com.yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class App {
	// Map<String, String> nameVersionMap = new LinkedHashMap<>();

	public static void main(String[] args) {
		System.out.println("hello");
		App a = new App();
		a.createYamlFile();
		a.loadYamlFile();
		System.out.println("bye");
	}

	public void createYamlFile() {
		try (BufferedReader br = new BufferedReader(new FileReader(new File("C:\\inpfile.jar.txt")));
				FileWriter w = new FileWriter("opfile.yaml");) {
			br.readLine();
			String line = br.readLine();
			List<Object> list = new ArrayList<>();

			while (line != null) {
				String s[] = line.split(":");
				Map<Object, Object> metadataMap = new LinkedHashMap<>();
				metadataMap.put("name", s[1] + "_" + s[3]);
				metadataMap.put("namespace", "default");
				metadataMap.put("description", s[1] + " " + s[3]);

				Map<Object, Object> specMap = new LinkedHashMap<>();
				specMap.put("type", "library");
				specMap.put("lifestyle", "production");
				specMap.put("owner", "external");

				Map<Object, Object> map = new LinkedHashMap<>();
				map.put("apiVersion", "a1");
				map.put("kind", "Component");
				map.put("metadata", metadataMap);
				map.put("spec", specMap);
				list.add(map);
				// nameVersionMap.put(s[1], s[3]);
				line = br.readLine();
			}

			DumperOptions op = new DumperOptions();
			op.setIndent(2);
			op.setPrettyFlow(true);
			op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			Yaml yaml = new Yaml(op);
			yaml.dumpAll(list.iterator(), w);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void loadYamlFile() {
		DumperOptions op = new DumperOptions();
		op.setIndent(2);
		op.setPrettyFlow(true);
		op.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		op.setIndicatorIndent(1);
		op.setIndentWithIndicator(true);
		Yaml yaml = new Yaml(op);
		List<Object> list = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("opfile.yaml")));
			for (Object object : yaml.loadAll(br)) {
				System.out.println(object);
				Map map = (LinkedHashMap) object;
				String kind = (String) map.get("kind");

				Map metadataMap = (LinkedHashMap) map.get("metadata");
				String name = (String) metadataMap.get("name");
				String namespace = (String) metadataMap.get("namespace");
				// name=name.split("_")[0];

				Map<Object, Object> kindNameMap = new LinkedHashMap<>();
				kindNameMap.put(kind, namespace + "/" + name);
				list.add(kindNameMap);
			}
			System.out.println(list);
			br.close();

			BufferedReader br2 = new BufferedReader(new FileReader(new File("op2.yaml")));
			Map map = (LinkedHashMap) yaml.load(br2);
			System.out.println(map);
			Map specMap = (LinkedHashMap) map.get("spec");
			if(specMap.containsKey("dependsOn")) {
				
				Set<Object> component=new LinkedHashSet<>();
				ArrayList<Object> newlist=(ArrayList)specMap.get("dependsOn");
				newlist.addAll(list);
				component.addAll(newlist);
				newlist.clear();
				newlist.addAll(component);
				specMap.put("dependsOn", newlist);
			}else {
				specMap.put("dependsOn", list);
			}
			
			System.out.println(map);
			map.remove("spec");
			String res = yaml.dump(map);
			
			Map spec=new LinkedHashMap<Object,Object>();
			spec.put("spec",specMap);
			
			
			op.setIndicatorIndent(2);
			res += yaml.dump(spec);
			
			System.out.println(res);
			FileWriter fw = new FileWriter("op2.yaml");
			fw.write(res);

			fw.close();
			br2.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
