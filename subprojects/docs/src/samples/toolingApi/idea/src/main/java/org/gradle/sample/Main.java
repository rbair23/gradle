package org.gradle.sample;

import org.gradle.tooling.*;
import org.gradle.tooling.model.*;
import org.gradle.tooling.model.idea.*;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Configure the connector and create the connection
        GradleConnector connector = GradleConnector.newConnector();
        connector.forProjectDirectory(new File("."));

        // Workaround http://issues.gradle.org/browse/GRADLE-2405
        String gradleUserHomeDir = System.getProperty("gradle.user.home");
        if (gradleUserHomeDir != null && gradleUserHomeDir.length() > 0) {
            connector.useGradleUserHomeDir(new File(gradleUserHomeDir));
        }

        if (args.length > 0) {
            connector.useInstallation(new File(args[0]));
        }

        ProjectConnection connection = connector.connect();
        try {
            IdeaProject project = connection.getModel(IdeaProject.class);
            System.out.println("***");
            System.out.println("Project details: ");
            System.out.println(project);

            System.out.println("***");
            System.out.println("Project modules: ");
            for(IdeaModule module: project.getModules()) {
                System.out.println("  " + module);
                System.out.println("  module details:");

                System.out.println("    tasks from associated gradle project:");
                for (GradleTask task: module.getGradleProject().getTasks()) {
                    System.out.println("      " + task.getName());
                }

                for (IdeaContentRoot root: module.getContentRoots()) {
                    System.out.println("    Content root: " + root.getRootDirectory());
                    System.out.println("    source dirs:");
                    for (IdeaSourceDirectory dir: root.getSourceDirectories()) {
                        System.out.println("      " + dir);
                    }

                    System.out.println("    test dirs:");
                    for (IdeaSourceDirectory dir: root.getTestDirectories()) {
                        System.out.println("      " + dir);
                    }

                    System.out.println("    exclude dirs:");
                    for (File dir: root.getExcludeDirectories()) {
                        System.out.println("      " + dir);
                    }
                }

                System.out.println("    dependencies:");
                for (IdeaDependency dependency: module.getDependencies()) {
                    System.out.println("      * " + dependency);
                }
            }
        } finally {
            // Clean up
            connection.close();
        }
    }
}
