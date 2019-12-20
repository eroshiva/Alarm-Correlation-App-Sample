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

import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.slf4j.Logger;


import java.util.List;


import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class defines set of methods which are capable of reconfiguring:
 *      - Nokia's Optical Transponder (1830 PSI-2T) and
 *      - Nokia's ROADM (Nokia 1830 PSS).
 */
public final class AlarmHandler {

    private static final Logger log = getLogger(AlarmHandler.class);



    private AlarmHandler() {
    }

    /**
     * This method detects, whether there is a fiber cut on current path.
     * @param path - current path to check.
     * @return - true, if there is a fiber cut, or false, if there is nothing to report.
     */
    public static Link detectFiberCut(Path path) {

        List<Link> links = path.links();
        log.info("\n\n [AlarmHandler - detectFiberCut] We entered the function \n {}\n", links);

        for (Link l : links) {
            log.info("\n\n\n [AlarmHandler - detectFiberCut]\n Checking link \n{}\n\n", l);
          if ((FiberCutAlarmHandler.checkLoS(l.src().deviceId(), l.src().port())) &
                  (FiberCutAlarmHandler.checkLoS(l.dst().deviceId(), l.dst().port()))) {
              log.error("\n\n [AlarmHandler - detectFiberCut] Fiber cut was detected! \n {} \n", l);
              return l; // We found a fiber cut!
          }
        }

        log.info("\n\n [AlarmHandler - detectFiberCut] No fiber cut was detected \n");
        return null;
    }


    /**
     * This method detect other type of scenario..
     * @return - true, if other scenario was detected, or false, if there is nothing to report.
     */
    public static boolean detectOtherScenario() {

        return false;
    }

}
