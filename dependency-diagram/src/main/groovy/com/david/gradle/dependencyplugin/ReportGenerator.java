package com.david.gradle.dependencyplugin;

import org.gradle.api.Project;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class ReportGenerator {
    String generateReport(Project root) {
        DependencyNode rootNode = new DependencyNode(root);
        return "digraph {\n" +
                rootNode.toEdgeArrowString() + "\n" +
                "ranksep=1.5\n" +
                "}";
    }

    private static class DependencyNode {
        private Project project;
        public Set<DependencyNode> children = new HashSet<>();

        public DependencyNode(Project project) {
            this.project = project;
            project.getChildProjects().forEach((childProjectName, childProject) -> {
                DependencyNode node = new DependencyNode(childProject);
                children.add(node);
            });
        }

        public String toEdgeArrowString() {
            return this.children.stream()
                    .map(child -> "\"" + this.project.getName() + "\"->\"" + child.project.getName() + "\"\n"
                            + child.toEdgeArrowString())
                    .collect(Collectors.joining("\n"));
        }
    }
}
