package br.gov.rs.defensoria.dpe_gradle_plugins.valid_migrations_order

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.apache.commons.lang.SystemUtils

class ValidMigrationsOrder implements Plugin<Project> {
    void apply(Project project) {

        project.tasks.build.doFirst {
            project.tasks.isValidMigrationsOrder.execute()
        }

        project.task('isValidMigrationsOrder') {
            description 'Valida se os arquivos de migrations incluídos/alterados pelo último commit são os últimos na ordem do repositório'

            def extension = project.extensions.create('valid_migrations_order', PluginExtension)

            doLast {

                println '--Iniciando validacao de novas Migrations--'

                assert extension.migrationsFolderPath != "": "É necessário informar o diretório raiz das migrations"

                validateFolder(project.file(extension.migrationsFolderPath as String))

                println '--Migrations validadas com sucesso--'
            }

        }

    }

    private void validateFolder(File rootFolder) {

        for (File subFile : rootFolder.listFiles()) {

            if (subFile.isDirectory()) {
                validateFolder(subFile)
            } else {
                validateFileOrder(subFile.getParentFile())
                return
            }

        }
    }

    private void validateFileOrder(File rootFolder) {

        println 'Validando: ' + rootFolder.path

        def migrationsNoDiretorio = rootFolder.listFiles()
        Arrays.sort(migrationsNoDiretorio)
        def newMigrations = "git diff HEAD^  HEAD --name-only $rootFolder.path".execute().text

        int totalNewMigrations = newMigrations.readLines().size()
        int totalMigrations = migrationsNoDiretorio.size()

        newMigrations.eachLine { line, count ->
            def correctMigrationPosition = totalMigrations - (totalNewMigrations - count)
            def fileNameInTheCorrectPosition = migrationsNoDiretorio[correctMigrationPosition].getName()
            assert line.contains(fileNameInTheCorrectPosition)
        }
    }

}
