import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.2"

project {
    description = "Contains all other projects"

    vcsRoot(HttpsGithubComNataliDubotolkovaMyDvna)

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
    }

    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }

    subProject(ProjectName)
}

object HttpsGithubComNataliDubotolkovaMyDvna : GitVcsRoot({
    name = "https://github.com/Natali-Dubotolkova/MyDvna"
    url = "https://github.com/Natali-Dubotolkova/MyDvna"
})


object ProjectName : Project({
    name = "ProjectName"

    buildType(ProjectName_BuildConf)
})

object ProjectName_BuildConf : BuildType({
    name = "BuildConf"

    steps {
        dotnetBuild {
            projects = "workingDirectory/solutionName"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        powerShell {
            name = "setBN"
            scriptMode = script {
                content = """
                    ${'$'}BuildNumber = Get-Date -format "yyyy.MM.dd"
                     
                    Write-Host "##teamcity[buildNumber '${'$'}BuildNumber']"
                """.trimIndent()
            }
        }
    }
})
