/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.tasks.diagnostics;

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.diagnostics.internal.DependencyReportRenderer
import org.gradle.api.tasks.diagnostics.internal.dependencies.AsciiDependencyReportRenderer
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.util.HelperUtil

import spock.lang.Specification

class DependencyReportTaskTest extends Specification {
    private Project project = new ProjectBuilder().build()
    private DependencyReportTask task = HelperUtil.createTask(DependencyReportTask.class, project)
    private DependencyReportRenderer renderer = Mock(DependencyReportRenderer)
    private Configuration conf1 = project.configurations.add("conf1")
    private Configuration conf2 = project.configurations.add("conf2")

    void setup() {
        task.renderer = renderer
    }

    def "task is configured correctly"() {
        task = HelperUtil.createTask(DependencyReportTask.class);

        expect:
        task.renderer instanceof AsciiDependencyReportRenderer
        task.configurations == null
    }

    def "renders all configurations in the project"() {
        when:
        task.generate(project)

        then: 1 * renderer.startConfiguration(conf1)
        then: 1 * renderer.render(conf1)
        then: 1 * renderer.completeConfiguration(conf1)


        then: 1 * renderer.startConfiguration(conf2)
        then: 1 * renderer.render(conf2)
        then: 1 * renderer.completeConfiguration(conf2)

        0 * renderer._
    }

    def "rendering can be limited to specific configurations"() {
        given:
        project.configurations.add("a")
        def bConf = project.configurations.add("b")
        task.configurations = [bConf] as Set

        when:
        task.generate(project)

        then:
        1 * renderer.startConfiguration(bConf)
        1 * renderer.render(bConf)
        1 * renderer.completeConfiguration(bConf)
        0 * renderer._
    }

    def "rendering can be limited to a single configuration, specified by name"() {
        given:
        project.configurations.add("a")
        def bConf = project.configurations.add("b")

        when:
        task.configuration = "b"

        then:
        task.configurations == [bConf] as Set
    }
}
