package br.com.ais.pontoeletronico

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("br.com.ais.pontoeletronico")

        noClasses()
            .that()
                .resideInAnyPackage("br.com.ais.pontoeletronico.service..")
            .or()
                .resideInAnyPackage("br.com.ais.pontoeletronico.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..br.com.ais.pontoeletronico.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses)
    }
}
