/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * This work was done in Nokia Bell Labs Paris
 *
 */

package org.onosproject.alarmcorrelation;

import org.onosproject.alarm.Alarm;
import org.onosproject.alarm.AlarmId;
import org.onosproject.alarm.AlarmService;
import org.onosproject.alarm.DefaultAlarm;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.faultmanagement.api.AlarmStore;
import org.onosproject.net.AnnotationKeys;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.link.LinkProviderRegistry;
import org.onosproject.net.link.LinkProviderService;
import org.slf4j.Logger;


import java.util.List;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

public final class FiberCutAlarmHandler {


    private static final Logger log = getLogger(FiberCutAlarmHandler.class);

    // Set of global variables to handle some functionality
    private static DeviceService deviceService = null;
    protected LinkProviderRegistry linkProviderRegistry;
    private static LinkProviderService linkProviderService = null;
    private static AlarmService alarmService = null;



    private FiberCutAlarmHandler() {
    }

    /**
     * Checks, whether there is a Loss of Signal (LoS) alarm in a storage.
     * @param deviceId - device ID to check.
     * @param port - port number.
     * @return - true or false depending on the results of searching.
     */
    public static boolean checkLoS(DeviceId deviceId, PortNumber port) {

        //log.debug("\n\n [FiberCutAlarmHandler - checkLoS] We entered the function \n" +
                         "{}/{}", deviceId, port);
        //FIXME: Probably it is necessary to invoke AlarmService each time here, to get fresh data.. ?
        alarmService = AbstractShellCommand.get(AlarmService.class);

        Set<Alarm> alarms = alarmService.getAlarms(deviceId);
        for (Alarm a : alarms) {
            log.info("\n\n [FiberCutAlarmHandler - checkLoS]\n Alarm is \n {} \n", a);
            if ((!a.cleared()) & (a.description().contains("Loss of Signal")) &
            (findPort(deviceId, port).contains(extractPortFromAlarm(a.description())))) {
                log.debug("\n\n [FiberCutAlarmHandler - checkLoS] We found 'Loss of Signal' on port {}/{} \n",
                         deviceId, findPort(deviceId, port));

                return true;
            }
        }

        log.debug("\n\n [FiberCutAlarmHandler - checkLoS] No 'Loss of Signal' were found on port {}/{} \n",
                            deviceId, findPort(deviceId, port));
        return false;
        }

    /**
     * This function de-activates the link between two port, once alarm is detected.
     * Maybe it's enough to pass only one parameter.
     * @param cpSrc - source port.
     * @param cpDst - destination port.
     */
    public static void disableLink(ConnectPoint cpSrc, ConnectPoint cpDst) {

        //log.debug("\n\n [FiberCutAlarmHandler - disableLink] We entered the function \n");

        linkProviderService = AbstractShellCommand.get(LinkProviderService.class);
        //TODO: Check, whether it works. If not, implement in the same way as in AlarmCorrelation.java
        log.debug("\n\n [FiberCutAlarmHandler - disableLink] Deleting link between " +
                             "{} and {} \n", cpSrc, cpDst);
        linkProviderService.linksVanished(cpSrc);
        linkProviderService.linksVanished(cpDst);

    }

    /**
     * Translates port from internal ONOS representation to actual name of the port.
     * @param deviceId - device ID.
     * @param port - port number in internal ONOS storage.
     * @return - port name as a String.
     */
    public static String findPort(DeviceId deviceId, PortNumber port) {

        //log.debug("\n\n [FiberCutAlarmHandler - findPort] We entered the function \n");
        deviceService = AbstractShellCommand.get(DeviceService.class);

        List<Port> ports = deviceService.getPorts(deviceId);
        for (Port p : ports) {
            if (p.number().equals(port)) {
                String portName = p.number().toString();
                log.debug("\n\n [FiberCutAlarmHandler - findPort] " +
                                 "Result is: {}/{} \n", deviceId, portName);
                return portName;
                }
        }

        log.debug("\n\n [FiberCutAlarmHandler - findPort] " +
                         "We didn't find a port name for port {}/{} " +
                        "in a port storage \n", deviceId,  port);
        return null;
        }

    /**
     * This method extract port name from alarm ID
     * as were formed and stored in NokiaAlarmConfig interface.
     * @param alarmDescription - alarm description to extract information.
     * @return - port name, on which this alarm occurred.
     */
    public static String extractPortFromAlarm(String alarmDescription) {

        //log.debug("\n\n [FiberCutAlarmHandler - extractPortFromAlarm] We entered the function \n" +
                         "{} \n", alarmDescription);

        String portName = null;

        portName = alarmDescription.substring(alarmDescription.indexOf(":") + 1);

        log.debug("\n\n [FiberCutAlarmHandler - extractPortFromAlarm] " +
                         "Result is: {} \n", portName);
        return portName;
    }

    /**
     * This method inserts two LoS alarms inside the alarm storage.
     */
    public static void insertLosAlarms() {

        AlarmStore alarmStore = AbstractShellCommand.get(AlarmStore.class);

        Alarm alarm1 = generateLosAlarm("1", DeviceId.deviceId("netconf:127.0.0.1:11002"),
                                        "202", 12);
        Alarm alarm2 = generateLosAlarm("2", DeviceId.deviceId("netconf:127.0.0.1:11003"),
                                        "202", 15);
        alarmStore.createOrUpdateAlarm(alarm1);
        alarmStore.createOrUpdateAlarm(alarm2);

    }

    /**
     * This method generates LoS alarm.
     * @param id - alarm id.
     * @param deviceId - device id.
     * @param source - information about source of the alarm.
     * @param timeStamp - timestamp when alarm was generated.
     * @return - alarm.
     */
    public static Alarm generateLosAlarm(String id, DeviceId deviceId,
                                        String source, long timeStamp) {

        return (new DefaultAlarm.Builder(AlarmId.alarmId(id),
                                        deviceId, "Loss of Signal:" + source,
                                        Alarm.SeverityLevel.CRITICAL, timeStamp).
                withServiceAffecting(true).build());
    }

}
