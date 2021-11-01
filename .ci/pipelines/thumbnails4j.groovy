/*
 *
 *  * Licensed to Elasticsearch B.V. under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Elasticsearch B.V. licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *	http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */


// Loading the shared lib
@Library('estc') _

pipeline {
    agent { label('linux && immutable') }
    stages {
        stage('Setup') {
            steps {
                estcGithubCheckout(repository: 'thumbnails4j',
                        revision: 'seanstory/expand-ci-pipeline')
            }
        }
        stage ('Build and Test') {
            steps {
                withMaven {
                    sh "./mvnw clean verify"
                } // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe reports and FindBugs reports
            }
        }
    }
}