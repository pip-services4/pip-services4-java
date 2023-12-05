package org.pipservices4.aws.controllers;

import java.util.List;

/**
 * An interface that allows to integrate lambda services into lambda function containers
 * and connect their actions to the function calls.
 */
public interface ILambdaController {
    /**
     * Get all actions supported by the controller.
     * @return an array with supported actions.
     */
    List<LambdaAction> getActions();
}
