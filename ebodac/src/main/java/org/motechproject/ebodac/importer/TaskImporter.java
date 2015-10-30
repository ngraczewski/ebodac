package org.motechproject.ebodac.importer;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.service.TaskService;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Component
public class TaskImporter implements OsgiServiceLifecycleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskImporter.class);

    private TaskService taskService;

    @Override
    public void bind(Object o, Map map) throws Exception {
        this.taskService = (TaskService) o;
        Bundle bundle = FrameworkUtil.getBundle(getClass());
        importTasks(bundle);
    }

    @Override
    public void unbind(Object o, Map map) throws Exception {
        this.taskService = null;
    }

    public void importTasks(Bundle bundle) {
        Enumeration<URL> urls = bundle.findEntries("tasks/", "*.json", false);

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            try {
                InputStream inputStream = url.openStream();
                String json = IOUtils.toString(inputStream);

                if (!taskExists(json)) {
                    taskService.importTask(json);
                }
            } catch (IOException ioe) {
                LOGGER.error("Couldn't read task file " + url.getPath());
            }
        }
    }

    private Boolean taskExists(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        Task task = mapper.readValue(node, Task.class);

        List<Task> existingTasks = taskService.findTasksByName(task.getName());
        return existingTasks.size() > 0;
    }
}
