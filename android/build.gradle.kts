allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

subprojects {
    fun setNamespaceIfMissing() {
        val android = project.extensions.findByName("android")
        if (android != null) {
            try {
                val getNamespace = android.javaClass.getMethod("getNamespace")
                val setNamespace = android.javaClass.getMethod("setNamespace", String::class.java)
                val current = getNamespace.invoke(android) as? String
                if (current.isNullOrEmpty()) {
                    setNamespace.invoke(android, "com.example.${project.name.replace("-", "_")}")
                }
            } catch (e: Exception) {
            }
        }
    }

    if (project.state.executed) {
        setNamespaceIfMissing()
    } else {
        afterEvaluate {
            setNamespaceIfMissing()
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
