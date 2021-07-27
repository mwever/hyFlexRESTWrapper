# HyFlex REST Wrapper

This project provides a REST wrapper for the HyFlex library. With the help of this wrapper, one can initialize problems and retrieve a token to refer to the respective problem. In principle all methods of the HyFlex library are forwarded via a respective REST-API method.

## Setup

In order to run the REST wrapper, you need to conduct the following steps:
1. Open a command line prompt and clone this repository ``git clone git@github.com:mwever/hyFlexRESTWrapper.git``
2. Change into the project directory ``cd hyFlexRESTWrapper``
3. Compile and start the wrapper via the following command: ``.\gradlew startWrapper``. This will automatically download all required dependencies, compile the Java code and finally execute the REST Wrapper as a Springboot Application.
