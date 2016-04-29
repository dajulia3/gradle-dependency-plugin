package com.david.gradle.dependencyplugin

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.BeforeClass
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

public class ReportGeneratorTest {
    @BeforeClass
    public static void setupProjectBuilder(){
        org.gradle.testfixtures.ProjectBuilder.metaClass.runtimeProjectDeps = []
        org.gradle.testfixtures.ProjectBuilder.metaClass.withRuntimeProjectDependencyOn = { project ->
            runtimeProjectDeps << project
            return delegate
        }

        def originalBuildFn = org.gradle.testfixtures.ProjectBuilder.metaClass.getMetaMethod("build");
        org.gradle.testfixtures.ProjectBuilder.metaClass.build = {
            ProjectInternal project = originalBuildFn.invoke(delegate);
            project.configurations{ runtime }
            def dependencies = project.configurations.getByName("runtime").getDependencies()
            delegate.runtimeProjectDeps.each {
                println "GONNA ADD it = $it"
                dependencies.add(it)
                println "dependencies = $dependencies"
            }
            return project
        }
    }

    private ReportGenerator reportGenerator = new ReportGenerator();
    Project root;
    {
        root = ProjectBuilder.builder().withName("root").build();
    }

    @Test
    public void generatesEmptyReportForProjectWithNoDependencies(){
        String result = reportGenerator.generateReport(root);
        String spaceInsensitiveResult = result.replaceAll(" ", "");

        assertThat(spaceInsensitiveResult, startsWith("digraph{"));
        assertThat(spaceInsensitiveResult, containsString("ranksep=1.5"));
        assertThat(spaceInsensitiveResult, endsWith("}"));
    }

    @Test
    public void generatesEdgesForDependencies(){
        ProjectBuilder.builder().withName("child-one").withParent(root).build();
        ProjectBuilder.builder().withName("child-two").withParent(root).build();

        String result = reportGenerator.generateReport(root);
        String spaceInsensitiveResult = result.replaceAll(" ", "");

        assertThat(spaceInsensitiveResult, containsString("\"root\"->\"child-one\""));
        assertThat(spaceInsensitiveResult, containsString("\"root\"->\"child-two\""));
    }

    @Test
    public void generatesEdgesForDependenciesForArbitrarilyDeepGraphs(){
        Project childOne = ProjectBuilder.builder().withName("child-one").withParent(root).build();
        ProjectBuilder.builder().withName("grandchild-a").withParent(childOne).build();
        ProjectBuilder.builder().withName("grandchild-b").withParent(childOne).build();
        Project childTwo = ProjectBuilder.builder().withName("child-two").withParent(root).build();
        ProjectBuilder.builder().withName("grandchild-c").withParent(childTwo).build();
        ProjectBuilder.builder().withName("grandchild-d").withParent(childTwo).build();

        String result = reportGenerator.generateReport(root);
        String spaceInsensitiveResult = result.replaceAll(" ", "");

        assertThat(spaceInsensitiveResult, containsString("\"root\"->\"child-one\""));
        assertThat(spaceInsensitiveResult, containsString("\"child-one\"->\"grandchild-a\""));
        assertThat(spaceInsensitiveResult, containsString("\"child-one\"->\"grandchild-b\""));
        assertThat(spaceInsensitiveResult, containsString("\"root\"->\"child-two\""));
        assertThat(spaceInsensitiveResult, containsString("\"child-two\"->\"grandchild-c\""));
        assertThat(spaceInsensitiveResult, containsString("\"child-two\"->\"grandchild-d\""));
    }


    @Test
    public void generatesEdgesForInterprojectDependencies(){
        //TODO: not sure how to sort all the configuration stuff out just yet
        Project utils = ProjectBuilder.builder().withName("utils")
                .withParent(root)
                .build();

        Project usingUtils = ProjectBuilder.builder().withName("some-project-using-utils")
                .withRuntimeProjectDependencyOn(utils)
                .withParent(root)
                .build();

        String result = reportGenerator.generateReport(root);
        String spaceInsensitiveResult = result.replaceAll(" ", "");

        assertThat(spaceInsensitiveResult, containsString("\"root\"->\"utils\""));
        assertThat(spaceInsensitiveResult, containsString("\"root\"->\"some-project-using-utils\""));
        assertThat(spaceInsensitiveResult, containsString("\"some-project-using-utils\"->\"utils\""));
    }

    @Test
    public void addingRuntimeConfigurationProjectDependencies(){
        Project root = ProjectBuilder.builder().withName("root").build();
        Project testFixtureProject = ProjectBuilder.builder().withName("child-dep")
                .withParent(root).build();
        Project child = ProjectBuilder.builder().withName("child")
                .withParent(root)
                .withRuntimeProjectDependencyOn(testFixtureProject).build();

        testFixtureProject.dependencies {
            String childDepJar =  this.getClass()
                .getResource("/com/david/gradle/dependencyplugin/testfixture.jar").getPath()
            println "childDepJar = $childDepJar"
            runtime files(childDepJar)
        }
        assertThat(root.configurations.getByName("runtime").size(), equalTo(0))
        assertThat(child.configurations.getByName("runtime").size(), equalTo(1))
        assertThat(child.configurations.getByName("runtime").size(), contains(testFixtureProject))
    }
}