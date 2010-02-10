Interface for the current version of API contained in simulator.api.SimulatorAPIInterface.
Main API implementation class is simulator.api.SimulatorAPI. It contains a number of the similar looking methods.
Here are some words describing the whole API structure :

For any operation such as simulation, kappa file compiling and so on, there is a class we have implemented.
These classes are contained in api.steps package. So as AbstractOperation class, a parent of any operation.
Each operation has method 'perform', which, being run, notifies a user through console.

OperationManager controls execution of any operation.
It can 'perform' operation two different ways:

operationManager.perform : this method executed given operation and some previousoperations if needed.
For example, if user wants to run simulation in a moment when solution isn't initialized yet, then
manager should first execute operation of initialization. OperationType class contains a field 'ordering',
keeping all relations between operations.

operationManager.performSequentially : this one just executes given operation and that's all. we can use this one
in our usual simulator workflow.