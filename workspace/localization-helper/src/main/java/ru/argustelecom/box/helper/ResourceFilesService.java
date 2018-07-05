package ru.argustelecom.box.helper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

import ru.argustelecom.box.helper.model.Resource;
import ru.argustelecom.box.helper.model.ResourceBundle;

public class ResourceFilesService {

	public List<ResourceBundle> read(Path resourcesFilesRoot) {
		List<ResourceBundle> resources = new ArrayList<>();
		List<Path> resourceFiles = new ResourceFilesSearcher().find(resourcesFilesRoot);
		Map<String, List<Path>> resourcesGroups = resourceFiles.stream().collect(
				Collectors.groupingBy(path -> ResourceBundle.getNameByFileName(path.getFileName().toString())));
		resourcesGroups.entrySet()
				.forEach(resourceGroup -> resources.add(createResourceBundle(resourceGroup.getValue())));
		return resources;
	}

	public void write(List<ResourceBundle> resourceBundles, Path resourcesFilesRoot) {
		List<Path> resourcesFiles = new ResourceFilesSearcher().find(resourcesFilesRoot);
		Map<String, List<Path>> resourcesGroups = resourcesFiles.stream().collect(
				Collectors.groupingBy(path -> ResourceBundle.getNameByFileName(path.getFileName().toString())));
		for (ResourceBundle resourceBundle : resourceBundles) {
			List<Path> resourceBundleFiles = resourcesGroups.get(resourceBundle.getName());
			if (resourceBundleFiles == null || resourceBundleFiles.isEmpty()) {
				System.out.println("Не найдено ни одного файла ресурсов для " + resourceBundle.getName());
				//throw new RuntimeException("Не найдено ни одного файла ресурсов для " + resourceBundle.getName());
				continue;
			}
			for (Resource resource : resourceBundle.getResources()) {
				saveResourceToFile(resource, resourceBundleFiles);
			}
		}
	}

	private ResourceBundle createResourceBundle(List<Path> resourcesFiles) {
		List<Resource> resources = new ArrayList<>();
		for (Path resourceFile : resourcesFiles) {
			try (InputStream is = Files.newInputStream(resourceFile)) {
				FileBasedConfigurationBuilder<PropertiesConfiguration> builder = getConfigurationBuilder(resourceFile);
				PropertiesConfiguration config = builder.getConfiguration();
				String localeName = Resource.getLocaleNameByFileName(resourceFile.getFileName().toString());
				Map<String, String> props = new HashMap<>();
				Iterator<String> keys = config.getKeys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					props.put(key, config.getProperty(key).toString());
				}
				resources.add(new Resource(localeName, props));
			} catch (IOException | ConfigurationException e) {
				e.printStackTrace();
			}
		}

		String bundleName = ResourceBundle.getNameByFileName(resourcesFiles.get(0).getFileName().toString());
		Collections.sort(resources, (lb1, lb2) -> lb1.getLocaleName().compareTo(lb2.getLocaleName()));
		return new ResourceBundle(bundleName, resources);
	}

	private void saveResourceToFile(Resource resource, List<Path> existentResourceBundleFiles) {
		try {
			Path dir = existentResourceBundleFiles.get(0).getParent();
			Path resourceFile = dir.resolve(Paths.get(resource.getResourceFileName()));
			if (!resourceFile.toFile().exists()) {
				Files.createFile(resourceFile);
				System.out.println("Создан ресурс: " + resourceFile.toRealPath());
			}
			FileBasedConfigurationBuilder<PropertiesConfiguration> builder = getConfigurationBuilder(resourceFile);
			PropertiesConfiguration config = builder.getConfiguration();
			boolean needSaveResourceFile = false;
			if (existentResourceBundleFiles.contains(resourceFile)) {
				needSaveResourceFile = changeResourceFile(resource, config);
			} else {
				needSaveResourceFile = createResourceFile(resource, config);
			}
			if (needSaveResourceFile) {
				builder.save();
			}
		} catch (ConfigurationException | IOException e) {
			throw new RuntimeException("Не удалось сохранить ресурс " + resource.getResourceFileName(), e);
		}
	}

	private boolean createResourceFile(Resource resource, PropertiesConfiguration config) {
		resource.getProperties().entrySet()
				.forEach(property -> config.addProperty(property.getKey(), property.getValue()));
		return true;
	}

	private boolean changeResourceFile(Resource resource, PropertiesConfiguration config) {
		boolean changed = false;
		for (Entry<String, String> property : resource.getProperties().entrySet()) {
			if (config.containsKey(property.getKey())) {
				if (!config.getString(property.getKey()).equals(property.getValue())) {
					config.setProperty(property.getKey(), property.getValue());
					changed = true;
				}
			} else {
				if (StringUtils.isNotBlank(property.getValue())) {
					config.addProperty(property.getKey(), property.getValue());
					changed = true;
				}
			}
		}

		return changed;
	}

	private FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigurationBuilder(Path filePath) {
		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(
				PropertiesConfiguration.class);
		return builder.configure(
				new Parameters().properties().setEncoding(StandardCharsets.UTF_8.name()).setFile(filePath.toFile()));
	}

}
