package com.david.gradle.dependencyplugin

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test

import static org.junit.Assert.assertTrue;

public class PluginTest {

    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'com.david.gradle.dependencyplugin'

        println( project.tasks.hello)
    }
}

