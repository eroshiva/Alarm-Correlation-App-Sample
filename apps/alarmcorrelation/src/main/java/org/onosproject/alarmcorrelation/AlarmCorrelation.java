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
 * This work was done in Nokia Bell Labs Paris
 *
 */
package org.onosproject.alarmcorrelation;

import org.onosproject.cfg.ComponentConfigService;

import org.onosproject.alarm.Alarm;
import org.onosproject.alarm.AlarmService;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Annotations;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.config.NetworkConfigService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.link.LinkDescription;
import org.onosproject.net.link.LinkProvider;
import org.onosproject.net.link.LinkProviderRegistry;
import org.onosproject.net.link.LinkProviderService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.topology.PathService;
import org.onosproject.net.topology.Topology;
import org.onosproject.net.topology.TopologyService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AlarmCorrelation extends AbstractProvider implements LinkProvider {

    private static final Logger log =  LoggerFactory.getLogger(AlarmCorrelation.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected ComponentConfigService cfgService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected NetworkConfigService networkConfigService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkProviderRegistry linkProviderRegistry;

    private ApplicationId appId;

    private LinkProviderService linkProviderService = null;
    private static final DeviceId DEVICE_ID_SOURCE = DeviceId.deviceId("netconf:127.0.0.1:11001");
    private static final DeviceId DEVICE_ID_DESTINATION = DeviceId.deviceId("netconf:127.0.0.1:11005");
    private Path path = null;


    /**
     * Creates an AlarmCorrelation device provider.
     */
    public AlarmCorrelation() {
        super(new ProviderId("l0", "org.onosproject.alarmcorrelation"));
    }

    @Activate
    protected void activate() {

        appId = coreService.registerApplication("org.onosproject.alarmcorrelation");
        linkProviderService = linkProviderRegistry.register(this);
        log.info("\n\n App ID ({}) was read and app was registered \n", appId.id());

        // Initializing variable
//        path = ReconfigurationHandler.getPath(DEVICE_ID_SOURCE, DEVICE_ID_DESTINATION);

        //Faking (inserting LoS alarms inside the storage)
        FiberCutAlarmHandler.insertLosAlarms();
        delay(5000);
        Link l = AlarmHandler.detectFiberCut(ReconfigurationHandler.getPath(DEVICE_ID_SOURCE, DEVICE_ID_DESTINATION));
        // Once alarm is detected, disabling the link
        disableLink(l.src(), l.dst());

        log.info("\n\n Started {} \n", appId.id());
    }

    @Deactivate
    protected void deactivate() {
        linkProviderRegistry.unregister(this);
        linkProviderService = null;
        log.info("Stopped");
    }

    @Modified
    public void modified(ComponentContext context) {

        // Reconfiguring the network
//        reconfigure();

        log.info("Reconfigured");
    }

    /**
     * This method starts the reconfiguration routine
     * once fiber cut was detected. It sets the network
     * with respect to new constraints.
     */
    public void reconfigure() {

        Path newPath = null;

        if (AlarmHandler.detectFiberCut(path) != null) {
            newPath = ReconfigurationHandler.getPath(DEVICE_ID_SOURCE, DEVICE_ID_DESTINATION);
            if (newPath != null) {
                ReconfigurationHandler.reconfigureTopology(newPath);
                path = newPath;
                log.info("\n\n E2E connectivity should be reconfigured! \n");
            } else {
                log.error("\n\n E2E connectivity is not possible! \n");
            }
        }

    }


    /**
     * This function de-activates the link between two port, once alarm is detected.
     * Maybe it's enough to pass only one parameter.
     * @param cpSrc - source port.
     * @param cpDst - destination port.
     */
    private void disableLink(ConnectPoint cpSrc, ConnectPoint cpDst) {

        log.info("\n\n [disableLink] Deleting link between {} and {} \n", cpSrc, cpDst);
        linkProviderService.linksVanished(cpSrc);
        linkProviderService.linksVanished(cpDst);

    }

    /**
     * This method freezes the program for defined time.
     * @param time - time to sleep in [ms].
     */
    public static void delay(int time) {
        try {
            log.info("\n\n Sleeping for {} seconds \n", time / 1000);
            Thread.sleep(time);
        } catch (Exception e) {
            log.info("\n\n Something went wrong during the sleeping.. \n\n");
        }
    }


}

