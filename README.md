# Alarm Correlation Function
*This work was done in Nokia Bell Labs France. All rights reserved by Nokia.*

## Description
This Alarm Correlation Function is composed as a SDN application for ONOS. 
It targets to detect and localize the *"fiber cut"* scenario in optical networks. 

Failure detection is based on the analysis of alarms collected through the NETCONF subscription. Once two *"Loss of Signal"* 
types of the alarms are collected within the same link (on interconnected ports), fiber cut is detected. 

Path Computation Element (PCE) function of ONOS is utilized for failure localization and 
path restoration purposes. PCE function is based by default on Dijkstra algorithm and interacts with 
a Topology subsystem of the controller (search could be also based on different algorithms).
PCE function is capable of searching for a path between two nodes (devices).

## Architecture
On the picture below is shown an architecture of Alarm Correlation Function as a SDN application.

[![Alarm Correlation Function](https://github.com/eroshiva/Alarm-Correlation-App-Sample/blob/master/alarm_correlation_app_arch.png)]() 

It has following modular approach:
* **Main body** of the program has three states:
  * **@Activate** starts the application.
  * **@Deactivate** stops application correspondingly.
  * **@Modified** method reacts on dynamic changes of the network parameters (i.e. network state). 
  From there we could trigger routine for checking the network on different failure states 
  and then corresponding reconfiguration of the network.
* **Alarm Handler** is an instance capable of detecting different failure states in the network. 
Contains various algorithms for detection of different failures. It is linked to the following instances:
  * **Fiber Cut Alarm Handler** contains set of functions necessary for *”fiber cut”* scenario detection. 
  Based on these functions algorithm in Alarm Handler for ”fiber cut” detection is built.
  * **”Specific” Alarm Handler** contains set of functions for other failure scenario detection. 
  Alarm Handler later implements algorithm for this specific case detection based on the functions provided by this utility.
* **Reconfiguration Handler** contains algorithm (and some supportive functions) for (re)configuring path in the network. 
*Since each device requires specific interaction and configuration of a specific parameters, **Reconfiguration Handler** is linked to following instances:*
  * **Nokia Reconfiguration Handler** contains set of functions capable of (re)configuring different types of Nokia’s optical devices.
  * **”Other Vendor” Reconfiguration Handler** contains set of functions capable of (re)configuring 
  different types of optical devices from different vendor.


## Limitations
Within this application following assumptions are made:
* PCE function is perfect and knows everything about the network.
  * Default metric consists in computing number of ”hops”. It is possible to change this metrics on custom one.
* Shortest path is computed and configured between each pair of the nodes in the network.
* Detecting two ”Loss of Signal” alarms on interconnected ports is enough for stating, that the fiber has been cut.
  * ”Loss of Signal” alarm could have various causes. It is also necessary to keep track of history of different parameters
  (like transmitting/receiving power and etc.) on interconnected ports. State of the laser (active/inactive) 
  should be also correlated. 

These assumptions are limiting. In practice, the shortest path between two nodes is not necessary the best one 
and used metrics could differ from number of ”hops” between two nodes.

## Possible enhancements
* Following modular approach, application could be extended on other utilities. For example, 
such module could implement specific **Machine Learning (ML)** technique for predicting specific failure state in the network. 
Other utility could implement other ML algorithm for predicting other failure state in the network.

* More parameters, like laser state, power values and other could be correlated in order to precise 
*"fiber cut"* failure state detection mechanism. Also, other failure scenarios should be taken into account
(they could have an impact on the structure of the app).

What could be also beneficial is to measure delay between the time, when actual failure state has happened, and a moment, 
when this state was detected by the application.

**This application is a proposal of how does Alarm Correlation Function could look like**. 


## Usage
To use this application, simply import all files from the directory to your ONOS distribution 
and run a clean installation with *"ok clean"* command.

Application was tested on Cassini emulators (two *LoS* alarms are inserted to check the algorithm workflow).

*Don't forget to change **tools/build/bazel/modules.bzl** with regard to your version of ONOS!*
