package com.david.gradle.dependencyplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec

public class DependencyDiagramPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task("generateDependencyDotFile") << {
            def file = new File("${project.projectDir}/project-dependencies.dot")
            if(file.exists()){
                file.delete()
            }

            def report = new ReportGenerator().generateReport(project)
            file << report;
        }

        project.task(type: Exec, "generateDependencyDiagram", dependsOn: "generateDependencyDotFile") {
            commandLine 'dot', '-Tsvg', "${project.projectDir}/project-dependencies.dot", "-o${project.projectDir}/project-dependencies.svg"
        }

    }
}