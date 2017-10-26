package br.gov.rs.defensoria.dpe_gradle_plugins.valid_migrations_order

import org.gradle.api.Plugin
import org.gradle.api.Project

class ValidMigrationsOrder implements Plugin<Project> {
    void apply(Project project) {

        project.tasks.build.doFirst {
            project.tasks.isValidMigrationsOrder.execute()
        }

        project.task('isValidMigrationsOrder') {
            description 'Valida se a os arquivos de migrations incluidos/alterados pelo ultimo commit sao os ultimos na ordem do repositorio'

            def extension = project.extensions.create('valid_migrations_order', PluginExtension)

            doLast {

                println '--Iniciando validacao de novas Migrations--'

                assert extension.migrationsFolderPath != "": "É necessário informar o diretório raiz das migrations"

                validadeFolder(project.file(extension.migrationsFolderPath as String))

                println '--Migrations validadas com sucesso--'
            }

        }

    }

    private void validateFolder(File rootFolder) {

        for (File subFile : rootFolder.listFiles()) {

            if (subFile.isDirectory()) {
                validadeFolder(subFile)
            } else {
                validadeFileOrder(subFile.getParentFile())
                return
            }

        }
    }

    private void validateFileOrder(File rootFolder) {

        println 'Validando: ' + rootFolder.path

        def migrationsNoDiretorio = rootFolder.listFiles()
        def newMigrations = "git diff HEAD^  HEAD --name-only $rootFolder.path".execute().text

        def totalNewMigrations = newMigrations.readLines().size()
        def totalMigrations = migrationsNoDiretorio.size()

        newMigrations.eachLine { line, count ->
            def correctMigrationPosition = totalMigrations - (totalNewMigrations - count)
            def fileNameInTheCorrectPosition = migrationsNoDiretorio[correctMigrationPosition].getName()
            assert line.contains(fileNameInTheCorrectPosition)
        }
    }

}