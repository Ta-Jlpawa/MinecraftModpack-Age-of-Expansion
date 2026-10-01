package com.tajlpawa.ageofexpansionmodpackfix.create.toolbox;

// Keep the transient adapter on its player instead of retaining players in a global cache.
public interface CarriedToolboxHolder {
    CarriedToolboxBlockEntity expansion$getToolbox();
    void expansion$setToolbox(CarriedToolboxBlockEntity toolbox);
}
