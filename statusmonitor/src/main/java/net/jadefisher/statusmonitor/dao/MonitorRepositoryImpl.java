package net.jadefisher.statusmonitor.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.jadefisher.statusmonitor.model.Monitor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;

@Repository
public class MonitorRepositoryImpl implements MonitorRepository {

	@Value("${statusmonitor.monitorDefsPath}")
	private String monitorDefsPath;

	@SuppressWarnings("unchecked")
	@Override
	public List<Monitor> getMonitors() {
		Yaml yaml = new Yaml();

		InputStream yamlStream = null;
		try {
			yamlStream = new FileInputStream(monitorDefsPath);
			return (List<Monitor>) yaml.load(yamlStream);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (yamlStream != null) {
				try {
					yamlStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return null;
	}

}
