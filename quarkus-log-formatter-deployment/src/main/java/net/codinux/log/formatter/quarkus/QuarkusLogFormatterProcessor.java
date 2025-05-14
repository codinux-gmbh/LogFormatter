package net.codinux.log.formatter.quarkus;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class QuarkusLogFormatterProcessor {

    private static final String FEATURE = "quarkus-log-formatter";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

}