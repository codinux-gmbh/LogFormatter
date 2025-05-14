package net.codinux.log.formatter.quarkus;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class QuarkusLogFormatterProcessor {

    // this value gets displayed on Quarkus startup in installed-features
    private static final String FEATURE = "log-formatter";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

}