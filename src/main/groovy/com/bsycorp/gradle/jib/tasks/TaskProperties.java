package com.bsycorp.gradle.jib.tasks;

import com.bsycorp.gradle.jib.JibExtension;
import com.bsycorp.gradle.jib.JibTaskSupport;
import org.gradle.api.Project;
import org.gradle.api.distribution.Distribution;
import org.gradle.api.distribution.DistributionContainer;
import org.gradle.api.distribution.plugins.DistributionPlugin;
import org.gradle.api.file.CopySpec;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Internal;

public interface TaskProperties extends ImageInputs {

    void sourceDistribution(CopySpec distribution);

    void logger(Logger logger);

    void extension(JibExtension extension);

    void taskSupport(JibTaskSupport taskSupport);

    @Internal
    JibExtension extension();

    default void setupProperties(Project project) {
        logger(project.getLogger());
        extension(project.getExtensions().findByType(JibExtension.class));
        taskSupport(new JibTaskSupport(project.getLogger(), extension(), project.getObjects()));

        getSourceDistributionName().set(extension().getSourceDistributionName());
        getLayerFilters().set(extension().getLayerFilters());
        getTimestampFromHash().set(extension().getTimestampFromHash());
        getBaseContainer().set(extension().getBaseContainer());
        getImageTag().set(extension().getImageTag());
        getImageEntrypoint().set(extension().getImageEntrypoint());

        project.getPlugins().withType(DistributionPlugin.class, newPlugin ->  {
            DistributionContainer container = project.getExtensions().findByType(DistributionContainer.class);
            Distribution distro = container.findByName(getSourceDistributionName().get());
            if (distro != null) {
                sourceDistribution(distro.getContents());
            } else {
                project.getLogger().warn("Distribution with name '" + getSourceDistributionName().get() + "' not found but configured");
            }
        });

    }
}
