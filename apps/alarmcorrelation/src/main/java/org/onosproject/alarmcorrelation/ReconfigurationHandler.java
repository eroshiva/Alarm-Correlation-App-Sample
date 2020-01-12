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

import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.topology.PathService;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class defines set of methods which are capable of reconfiguring the network.
 * ***********
 * It is a proposal of how does such algorithm could look like.
 * ***********
 */
public final class ReconfigurationHandler {

    private static final Logger log = getLogger(ReconfigurationHandler.class);

    // Set of global parameters, where to store the necessary information
    private static DeviceService deviceService;
    private static PathService pathService;

    private ReconfigurationHandler() {}


    /**
     * Reconfigures the network topology as a reaction on alarm detection.
     * @param newPath - new path (to configure the network).
     * @return - true or false depending on a status of reconfiguration process.
     */
    public static boolean reconfigureTopology(Path newPath) {

        boolean success = false;
        boolean check1 = false;
        boolean check2 = false;
        boolean flag = false;
        ConnectPoint src = null;
        ConnectPoint dst = null;

        //TODO: Reconfigure devices.
        List<Link> newLinks = newPath.links();
        for (Link l : newLinks) {

            check1 = false;
            check2 = false;
            src = l.src();
            dst = l.dst();

            // Configuring source only for the first time.
            // Afterwards configuring only destination point
            if (!flag) {
                check1 = reconfigureNode(newPath, src.deviceId(), src.port());
                flag = true;
            }
            // Configuring destination after first
            check2 = reconfigureNode(newPath, dst.deviceId(), dst.port());

            if (check1) {
                log.info("\n\n [ReconfigurationHandler - reconfigureTopology] " +
                                 "Node {} was reconfigured successfully! \n", src.deviceId());
            } else {
                log.error("\n\n [ReconfigurationHandler - reconfigureTopology]" +
                                  "Node {} was NOT reconfigured.. \n", src.deviceId());
                return false;
            }
            if (check2) {
                log.info("\n\n [ReconfigurationHandler - reconfigureTopology] " +
                                 "Node {} was reconfigured successfully! \n", dst.deviceId());
            } else {
                log.error("\n\n [ReconfigurationHandler - reconfigureTopology]" +
                                  "Node {} was NOT reconfigured.. \n", dst.deviceId());
                return false;
            }

            if ((check1) & (check2)) {
                log.info("\n\n [ReconfigurationHandler - reconfigureTopology] " +
                                 "Link has been configured successfully! \n");
            } else {
                log.error("\n\n [ReconfigurationHandler - reconfigureTopology] " +
                                  "Something went wrong during the reconfiguring of the Link.. \n");
                return false;
            }

        }

        log.info("\n\n [reconfigureTopology] Some routine \n");

        return success;
    }

    /**
     * This method is capable of reconfiguring the node with regard to the device.
     * @param path - currently configured path.
     * @param deviceId - device ID we try to configure.
     * @param portNumber - port of the device, which we try to configure.
     * @return - true or false with regard to the result of device configuration.
     */
    public static boolean reconfigureNode(Path path, DeviceId deviceId, PortNumber portNumber) {

        boolean status = false;

        if (checkDeviceType(deviceId).equals("OT")) {
            // Some routine
            log.info("\n\n [ReconfigurationHandler - reconfigureNode] " +
                             "Node (OT - {}/{}) is being reconfigured \n",
                     deviceId, portNumber);
//            status = SpecificVendorReconfigHandler.reconfigDeviceModel(path, deviceId, portNumber);
//            log.info("\n\n [ReconfigurationHandler - reconfigureNode] " +
//                             "Result of the configuration is {} \n", status);
        }
        if (checkDeviceType(deviceId).equals("ROADM_OTN")) {
            // Some routine
            log.info("\n\n [ReconfigurationHandler - reconfigureNode] " +
                             "Node (ROADM_OTN - {}/{}) is being reconfigured \n",
                     deviceId, portNumber);
//            status = SpecificVendorReconfigHandler.reconfigDeviceModel(path, deviceId, portNumber);
//            log.info("\n\n [ReconfigurationHandler - reconfigureNode] " +
//                             "Result of the configuration is {} \n", status);
        }
        if (checkDeviceType(deviceId).equals("ROADM")) { 
            // Some routine
            log.info("\n\n [ReconfigurationHandler - reconfigureNode] " +
                             "Node (ROADM - {}/{}) is being reconfigured \n",
                     deviceId, portNumber);
//            status = SpecificVendorReconfigHandler.reconfigDeviceModel(path, deviceId, portNumber);
//            log.info("\n\n [ReconfigurationHandler - reconfigureNode] " +
//                             "Result of the configuration is {} \n", status);
        }
        // Add more devices (types) optionally

        return status;
    }

    /**
     * Gets shortest path between two endpoints.
     * @param src - source device ID.
     * @param dst - destination device ID.
     * @return - shortest path between src and dst.
     */
    public static Path getPath(DeviceId src, DeviceId dst) {

        Path path = null;
        double min = 100000;
        pathService = AbstractShellCommand.get(PathService.class);

        Set<Path> shortest = pathService.getPaths(src, dst);
        // Finding shortest path
        for (Path p : shortest) {
            if (p.cost() < min) {
                min = p.cost();
                path = p;
            }
        }

        log.info("\n\n [ReconfigurationHandler - getPath] Returned SHORTEST path is \n {} \n", path);

        return path;
    }

}
