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

package org.gradle.launcher.daemon

/**
 * by Szczepan Faber, created at: 1/20/12
 */
class DispachingFailureIntegrationSpec extends DaemonIntegrationSpec {

    def "failing build does not make the daemon send corrupted message"() {
        expect:
        //This kind of failure more likely reproduces the problem
        def settingsFile = file("settings.gradle") << "// empty"
        def projectdir = file("project dir").createDir()

        //requesting x failing builds creates enough stress to expose issues with unsynchronized dispatch
        50.times {
            executer.usingSettingsFile(settingsFile)
                    .usingProjectDirectory(projectdir)
                    .runWithFailure()
        }
    }
}
