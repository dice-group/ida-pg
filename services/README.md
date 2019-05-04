# IDA Services

This directory contains the Dockerfiles for the IDA services.

The Dockerfiles are separated from the IDA modules since the services do not directly map to the modules.
Because of that, the code module structure was decoupled from the service structure.

IDA consists of the following services:

*   **nginx:** Serves the ida-chatbot frontend but also acts as a reverse proxy for the ida-ws service.
*   **ida-ws:** Runs the ida-ws Spring server but it requires the librarian module in order to run.
