package org.goafabric.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import org.goafabric.invoice.Application;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameStartingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packagesOf = Application.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class PersistenceRulesTest {

    @ArchTest
    static final ArchRule layerAreRespectedWithPersistence = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .ignoreDependency(simpleNameStartingWith("DemoDataImporter"), DescribedPredicate.alwaysTrue())

            .layer("Controller").definedBy("..controller")
            .layer("Logic").definedBy("..logic..")
            .layer("Persistence").definedBy("..persistence..")

            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Logic").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Logic")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule classesExtendingRepositoryShouldEndWithRepository = classes()
            .that().areAssignableTo("org.springframework.data.repository.Repository")
            .should().haveSimpleNameEndingWith("Repository")
            .because("all classes extending Repository should end with 'Repository' in their name")
            .allowEmptyShould(true);



    @ArchTest
    static final ArchRule logicAnnotatedWithTransactional = classes()
            .that().areAssignableTo("org.springframework.data.repository.Repository")
            .should(new ArchCondition<>("Repository used") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            classes()
                                    .that().haveSimpleNameEndingWith("Logic")
                                    .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                                    .because("Logic Classes should be annotated with @Transactional")
                                    .check(new ClassFileImporter().importPackagesOf(Application.class));
                        }
                    }
            ).allowEmptyShould(true);
}
